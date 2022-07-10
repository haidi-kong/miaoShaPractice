package com.travel.admin.controller;

import com.travel.admin.config.mvc.UserCheckAndLimit;
import com.travel.admin.serviceCollector.OrderServiceCollector;
import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.AbstractResult;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.common.utils.UUIDUtil;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.OrderDetailVo;
import com.travel.order.apis.entity.OrderInfoVo;
import com.travel.order.apis.service.GoodsService;
import com.travel.order.apis.service.MiaoshaService;
import com.travel.order.apis.service.OrderService;
import com.travel.users.apis.entity.LoginVo;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.PaymentVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.mengyun.tcctransaction.CancellingException;
import org.mengyun.tcctransaction.ConfirmingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.security.auth.login.AccountException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;


@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController extends BaseController {


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
	@UserCheckAndLimit(seconds = 5, maxCount = 5, needLogin = true)
	@ResponseBody
	public ResultGeekQ<String> pay(MiaoShaUser user, @RequestParam(value ="orderId") Long orderId,
								   @RequestParam(value ="token", required=false) String token) {
		PaymentVo paymentVo = new PaymentVo();
		paymentVo.setOrderId(orderId);
		paymentVo.setPayAmount(new BigDecimal("0.01"));
		ResultGeekQ<String> result = ResultGeekQ.build();
		if (user == null) {
			result.withError(ResultStatus.SESSION_ERROR);
			return result;
		}

		try {
			orderServiceCollector.makePayment(user, paymentVo);
			return result;
		} catch (ConfirmingException confirmingException) {
			log.error("支付失败 error:{}", confirmingException.getMessage());
			//exception throws with the tcc transaction status is CONFIRMING,
			//when tcc transaction is confirming status,
			// the tcc transaction recovery will try to confirm the whole transaction to ensure eventually consistent.
			return 	result.error(ResultStatus.SYSTEM_ERROR);
		} catch (CancellingException cancellingException) {
			log.error("支付失败 error:{}", cancellingException.getMessage());
			//exception throws with the tcc transaction status is CANCELLING,
			//when tcc transaction is under CANCELLING status,
			// the tcc transaction recovery will try to cancel the whole transaction to ensure eventually consistent.
			log.error("支付失败 error:{}", cancellingException.getMessage());
			return result.error(ResultStatus.SYSTEM_ERROR);
		} catch (Throwable e) {
			//other exceptions throws at TRYING stage.
			//you can retry or cancel the operation.
			log.error("支付失败 error:{}", e.getMessage());
			result.error(ResultStatus.SYSTEM_ERROR);
			return result;
		}
	}

	@RequestMapping(value = "/to_list")
	@UserCheckAndLimit(seconds = 5, maxCount = 5, needLogin = true)
	@ResponseBody
	public String ordersList(HttpServletRequest request, HttpServletResponse response,
							MiaoShaUser user, Model model) {
		model.addAttribute("user", user);
		ResultGeekQ<List<OrderDetailVo>> orderList = orderServiceCollector.getOrderList();
		model.addAttribute("ordersList", orderList.getData());
		model.addAttribute("count", orderList.getData().size());
		model.addAttribute("payCount", orderList.getData().stream()
				.filter(v -> 0 == v.getOrder().getStatus()).count());
		return render(request, response, model, "order_list");
	}

	@RequestMapping("/getOrderId")
	@ResponseBody
	public String getOrderId(LoginVo loginVo, HttpServletResponse response) throws AccountException {
		log.info(loginVo.toString());
		try {
			ResultGeekQ<Long> result = orderServiceCollector.getOrderIdByUserId(loginVo.getMobile());
			if(!AbstractResult.isSuccess(result)){
				throw new AccountException("user error");
			}
			return result.getData().toString();
		} catch (Exception e) {
			throw new AccountException("user error");
		}

	}
    
}
