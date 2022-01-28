package com.travel.order.providers.logic.impl;

import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.MiaoShaGoods;
import com.travel.order.providers.logic.GoodsLogic;
import com.travel.order.providers.mapper.GoodsDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author
 */
@Slf4j
@Service
public class GoodsLogicImpl implements GoodsLogic {

    @Autowired
    private GoodsDao goodsDao;

    @Override
    public List<GoodsVo> goodsVoList() {
        return goodsDao.goodsVoList();
    }

    @Override
    public GoodsVo goodsVoByGoodsId(Long goodId) {
        return goodsDao.goodsVoByGoodsId(goodId);
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(Long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    public int reduceStock(MiaoShaGoods miaoShaGoods) {
        return goodsDao.reduceStock(miaoShaGoods);
    }
}
