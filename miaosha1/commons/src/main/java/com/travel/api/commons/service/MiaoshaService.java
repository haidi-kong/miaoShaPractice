package com.travel.api.commons.service;


import com.travel.api.commons.vo.GoodsVo;
import com.travel.api.commons.vo.MiaoShaOrderVo;
import com.travel.api.commons.vo.MiaoShaUserVo;
import com.travel.api.commons.vo.OrderInfoVo;
import com.travel.common.resultbean.ResultGeekQ;

/**
 * @author 邱润泽 bullock
 */
public interface MiaoshaService {

    public ResultGeekQ<OrderInfoVo> miaosha(MiaoShaUserVo user, GoodsVo goods);

    public ResultGeekQ<Integer> insertMiaoshaOrder(MiaoShaOrderVo miaoshaOrder);

    public ResultGeekQ<MiaoShaOrderVo> getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId);

    public ResultGeekQ<String> createMiaoshaPath(MiaoShaUserVo user, Long goodsId);

    public ResultGeekQ<Long> getMiaoshaResult(Long userId, Long goodsId);

    public ResultGeekQ<Boolean> checkPath(MiaoShaUserVo user, long goodsId, String path);

}
