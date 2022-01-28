package com.travel.order.apis.service;

import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.MiaoShaOrderVo;
import com.travel.order.apis.entity.MiaoShaUserVo;
import com.travel.order.apis.entity.OrderInfoVo;

public interface MiaoshaService {
    public ResultGeekQ<OrderInfoVo> miaosha(MiaoShaUserVo user, GoodsVo goods);

    public ResultGeekQ<Integer> insertMiaoshaOrder(MiaoShaOrderVo miaoshaOrder);

    public ResultGeekQ<MiaoShaOrderVo> getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId);

    public ResultGeekQ<String> createMiaoshaPath(MiaoShaUserVo user, Long goodsId);

    public ResultGeekQ<Long> getMiaoshaResult(Long userId, Long goodsId);

    public ResultGeekQ<Boolean> checkPath(MiaoShaUserVo user, long goodsId, String path);
}
