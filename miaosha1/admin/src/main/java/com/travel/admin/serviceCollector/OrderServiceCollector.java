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
import io.seata.saga.engine.StateMachineEngine;
import io.seata.saga.statelang.domain.ExecutionStatus;
import io.seata.saga.statelang.domain.StateMachineInstance;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.mengyun.tcctransaction.api.Compensable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class OrderServiceCollector {
    @DubboReference(check = false)
    OrderService orderService;

    @DubboReference(check = false)
    GoodsService goodsService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @Autowired
    StateMachineEngine stateMachineEngine;


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


//    @Compensable(confirmMethod = "confirmMakePayment", cancelMethod = "cancelMakePayment",
//            asyncConfirm = true, asyncCancel = true)
    //@GlobalTransactional(timeoutMills = 300000, name = "business-seata-example")
//    public void makePayment(MiaoShaUser user, PaymentVo paymentVo) {
//        log.info("start  transaction : {}", JSONObject.toJSONString(paymentVo));
//        // 扣减库存和更新订单
//        miaoshaService.completeOrder(user, paymentVo.getOrderId());
//        // 支付
//        miaoShaUserService.pay(user, paymentVo);
//
//        log.info("complete  transaction : {}", JSONObject.toJSONString(paymentVo));
//    }

    public void confirmMakePayment(MiaoShaUser user, PaymentVo paymentVo) {
        log.info("start tcc transaction confirm: {}", JSONObject.toJSONString(paymentVo));

        //check if the trade order status is PAYING, if no, means another call confirmMakePayment happened, return directly, ensure idempotency.
    }

    public void cancelMakePayment(MiaoShaUser user, PaymentVo paymentVo) {
        log.info("start tcc transaction cancel: {}", JSONObject.toJSONString(paymentVo));

    }

    public boolean pay(MiaoShaUser user, PaymentVo paymentVo) {
        miaoShaUserService.pay(user, paymentVo);
        return true;
        //check if the trade order status is PAYING, if no, means another call confirmMakePayment happened, return directly, ensure idempotency.
    }

    public boolean cancelPay(MiaoShaUser user, PaymentVo paymentVo) {
        log.info("start tcc transaction cancel: {}", JSONObject.toJSONString(paymentVo));
        return true;
    }

    public boolean completeOrder(MiaoShaUser user, long orderId) {
        miaoshaService.completeOrder(user, orderId);
        return true;
    }

    public boolean cancelCompleteOrder(MiaoShaUser user, long orderId) {
        miaoshaService.cancelCompleteOrder(user, orderId);
        return true;
    }




    public void makePayment(MiaoShaUser user, PaymentVo paymentVo) {
        log.info("start  transaction : {}", JSONObject.toJSONString(paymentVo));
        Map<String, Object> startParams = new HashMap<>(3);
        //唯一健
        String businessKey = String.valueOf(System.currentTimeMillis());
        startParams.put("user", user);
        startParams.put("orderId", paymentVo.getOrderId());
        startParams.put("paymentVo", paymentVo);

        //同步执行
        StateMachineInstance inst = stateMachineEngine.startWithBusinessKey("reduceInventoryAndBalance", null, businessKey, startParams);

        if(ExecutionStatus.SU.equals(inst.getStatus())){
            log.info("complete  transaction,saga transaction execute Succeed. XID: " + inst.getId());
        }else{
            log.error("failed  transaction ,saga transaction execute failed. XID: " + inst.getId());
        }

    }
}
