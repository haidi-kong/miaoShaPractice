package com.travel.admin.controller;

import com.travel.admin.serviceCollector.OrderServiceCollector;
import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.OrderDetailVo;
import com.travel.order.apis.entity.OrderInfoVo;
import com.travel.order.apis.service.GoodsService;
import com.travel.order.apis.service.MiaoshaService;
import com.travel.order.apis.service.OrderService;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.PaymentVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.mengyun.tcctransaction.CancellingException;
import org.mengyun.tcctransaction.ConfirmingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;


@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController {


	@Autowired
	OrderServiceCollector orderServiceCollector;

    @RequestMapping("/detail")
    @ResponseBody
    public ResultGeekQ<OrderDetailVo> info(MiaoShaUser user,
										   @RequestParam("orderId") long orderId) {
    	ResultGeekQ result = ResultGeekQ.build();
    	try {
			if (user == null) {
				result.withError(ResultStatus.SESSION_ERROR);
				return result;
			}

			result = orderServiceCollector.getOrderInfo(orderId);

		} catch (Exception e){
    		log.error("查询明细订单失败 error:{}",e);
			result.error(ResultStatus.SYSTEM_ERROR);
			return result;
		}
    	return result;
    }

	@PostMapping("/makePayment")
	@ResponseBody
	public ResultGeekQ<String> pay(MiaoShaUser user, @Valid PaymentVo paymentVo) {
		ResultGeekQ result = ResultGeekQ.build();
		if (user == null) {
			result.withError(ResultStatus.SESSION_ERROR);
			return result;
		}

		try {
			orderServiceCollector.makePayment(user, paymentVo);
			result.setData("支付成功");
		} catch (ConfirmingException confirmingException) {
			log.error("支付失败 error:{}", confirmingException.getMessage());
			//exception throws with the tcc transaction status is CONFIRMING,
			//when tcc transaction is confirming status,
			// the tcc transaction recovery will try to confirm the whole transaction to ensure eventually consistent.
			log.error("支付失败 error:{}", confirmingException.getMessage());
			result.error(ResultStatus.SYSTEM_ERROR);
			return result;
		} catch (CancellingException cancellingException) {
			log.error("支付失败 error:{}", cancellingException.getMessage());
			//exception throws with the tcc transaction status is CANCELLING,
			//when tcc transaction is under CANCELLING status,
			// the tcc transaction recovery will try to cancel the whole transaction to ensure eventually consistent.
			log.error("支付失败 error:{}", cancellingException.getMessage());
			result.error(ResultStatus.SYSTEM_ERROR);
			return result;
		} catch (Throwable e) {
			//other exceptions throws at TRYING stage.
			//you can retry or cancel the operation.
			log.error("支付失败 error:{}", e.getMessage());
			result.error(ResultStatus.SYSTEM_ERROR);
			return result;
		}
		return result;
	}
    
}
