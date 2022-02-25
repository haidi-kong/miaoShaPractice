package com.travel.order.providers.logic;



import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.MiaoShaGoods;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author
 */
@Service
public interface GoodsLogic {

    List<GoodsVo> goodsVoList();

    GoodsVo goodsVoByGoodsId(Long goodId);

    GoodsVo getGoodsVoByGoodsId(Long goodsId);

}
