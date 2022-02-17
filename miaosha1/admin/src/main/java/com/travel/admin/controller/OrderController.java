package com.travel.admin.controller;

import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.OrderDetailVo;
import com.travel.order.apis.entity.OrderInfoVo;
import com.travel.order.apis.service.GoodsService;
import com.travel.order.apis.service.MiaoshaService;
import com.travel.order.apis.service.OrderService;
import com.travel.users.apis.entity.MiaoShaUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController {

	@DubboReference
	MiaoshaService miaoshaService;
	@Autowired
	OrderService orderService;
	@Autowired
	GoodsService goodsService;

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
			ResultGeekQ<OrderInfoVo> orderR = orderService.getOrderById(orderId);
			if(!ResultGeekQ.isSuccess(orderR)){
				result.withError(orderR.getCode(),orderR.getMessage());
				return result;
			}
			Long goodsId = orderR.getData().getGoodsId();
			ResultGeekQ<GoodsVo> goodsR = goodsService.goodsVoByGoodId(goodsId);
			if(!ResultGeekQ.isSuccess(goodsR)){
				result.withError(orderR.getCode(),orderR.getMessage());
				return result;
			}
			OrderDetailVo vo = new OrderDetailVo();
			vo.setOrder(orderR.getData());
			vo.setGoods(goodsR.getData());
			result.setData(vo);
		} catch (Exception e){
    		log.error("查询明细订单失败 error:{}",e);
			result.error(ResultStatus.SYSTEM_ERROR);
			return result;
		}
    	return result;
    }
    
}
