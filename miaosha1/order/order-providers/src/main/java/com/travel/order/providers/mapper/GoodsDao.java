package com.travel.order.providers.mapper;

import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.MiaoShaGoods;
import com.travel.order.providers.entity.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsDao {
    int deleteByPrimaryKey(Long id);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Goods record);

     List<GoodsVo> goodsVoList();

    GoodsVo goodsVoByGoodsId(Long goodId);

    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    public int reduceStock(MiaoShaGoods miaoShaGoods);
    public int reduceLockStock(MiaoShaGoods miaoShaGoods);
}