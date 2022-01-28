package com.travel.order.apis.service;




import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.GoodsVo;

import java.util.List;

/**
 * @author lzj
 */
public interface GoodsService {

    public ResultGeekQ<List<GoodsVo>> goodsVoList();

    public ResultGeekQ<GoodsVo> goodsVoByGoodId(Long goodId);

    public ResultGeekQ<Boolean> reduceStock(GoodsVo goods);
}
