package com.travel.admin.serviceCollector;


import com.alibaba.fastjson.JSONObject;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.OrderDetailVo;
import com.travel.order.apis.entity.OrderInfoVo;
import com.travel.order.apis.service.GoodsService;
import com.travel.order.apis.service.MiaoshaService;
import com.travel.order.apis.service.OrderService;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.PaymentVo;
import com.travel.users.apis.service.MiaoShaUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.mengyun.tcctransaction.api.Compensable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
public class OrderServiceCollector {
    @DubboReference(check = false)
    OrderService orderService;

    @DubboReference(check = false)
    GoodsService goodsService;

    @DubboReference(check = false)
    MiaoshaService miaoshaService;

    @DubboReference(check = false)
    private MiaoShaUserService miaoShaUserService;


    public  ResultGeekQ<List<OrderDetailVo>> getOrderList() {
        ResultGeekQ result = ResultGeekQ.build();
        ResultGeekQ<List<OrderInfoVo>> orderR = orderService.getOrderList();
        if(!ResultGeekQ.isSuccess(orderR)){
            result.withError(orderR.getCode(),orderR.getMessage());
            return result;
        }
        List<OrderInfoVo> orderList = orderR.getData();
        List<OrderDetailVo> voList = new ArrayList<>();
        for (OrderInfoVo orderInfoVo : orderList) {
            OrderDetailVo vo = new OrderDetailVo();
            Long goodsId = orderInfoVo.getGoodsId();
            ResultGeekQ<GoodsVo> goodsR = goodsService.goodsVoByGoodId(goodsId);
            if(!ResultGeekQ.isSuccess(goodsR)){
                result.withError(orderR.getCode(),orderR.getMessage());
                return result;
            }
            vo.setOrder(orderInfoVo);
            vo.setGoods(goodsR.getData());
            voList.add(vo);
        }
        result.setData(voList);
        return result;
    }

    public  ResultGeekQ<Long> getOrderIdByUserId(Long userId) {
        ResultGeekQ<Long> result = ResultGeekQ.build();
        ResultGeekQ<OrderInfoVo> orderR = orderService.getOrderByUserId(userId);
        if(!ResultGeekQ.isSuccess(orderR)){
            result.withError(orderR.getCode(),orderR.getMessage());
            return result;
        }
        result.setData(orderR.getData().getId());
        return result;
    }

    public  ResultGeekQ<OrderDetailVo> getOrderInfo(long orderId) {
        ResultGeekQ result = ResultGeekQ.build();
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
        return result;
    }


    //@Compensable(confirmMethod = "confirmMakePayment", cancelMethod = "cancelMakePayment", asyncConfirm = false)
    public void makePayment(MiaoShaUser user, PaymentVo paymentVo) {
        log.info("start tcc transaction try: {}", JSONObject.toJSONString(paymentVo));
        // 支付
        miaoShaUserService.pay(user, paymentVo);
        // 扣减库存和更新订单
        miaoshaService.completeOrder(user, paymentVo.getOrderId());
    }

    public void confirmMakePayment(MiaoShaUser user, PaymentVo paymentVo) {
        log.info("start tcc transaction confirm: {}", JSONObject.toJSONString(paymentVo));

        //check if the trade order status is PAYING, if no, means another call confirmMakePayment happened, return directly, ensure idempotency.
    }

    public void cancelMakePayment(MiaoShaUser user, PaymentVo paymentVo) {
        log.info("start tcc transaction cancel: {}", JSONObject.toJSONString(paymentVo));

    }
}
