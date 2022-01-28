package com.travel.order.apis.service;



import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.MiaoShaOrderVo;
import com.travel.order.apis.entity.MiaoShaUserVo;
import com.travel.order.apis.entity.OrderInfoVo;

/**
 * @author
 */
public interface OrderService {

    public ResultGeekQ<MiaoShaOrderVo> getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId);

    //todo 用户信息不应该传过来，改成从用户服务中获取
    public ResultGeekQ<OrderInfoVo> createOrder(MiaoShaUserVo user, GoodsVo goods);

    public ResultGeekQ<OrderInfoVo> getOrderById(Long orderId);

}
