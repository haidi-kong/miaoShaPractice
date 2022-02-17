package com.travel.order.apis.service;

import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.GoodsDetailVo;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.users.apis.entity.MiaoShaUser;

import java.util.List;

/**
 * @author lzj
 */
public interface GoodsService {

    public ResultGeekQ<List<GoodsVo>> goodsVoList();

    public ResultGeekQ<GoodsVo> goodsVoByGoodId(Long goodId);

    public ResultGeekQ<Boolean> reduceStock(GoodsVo goods);

    public ResultGeekQ<GoodsDetailVo> goodsDetail(MiaoShaUser user, String goodsId);
}
