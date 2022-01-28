package com.travel.order.providers.mapper;

import com.travel.order.apis.entity.MiaoShaGoods;

public interface MiaoShaGoodsDao {
    int deleteByPrimaryKey(Long id);

    int insert(MiaoShaGoods record);

    int insertSelective(MiaoShaGoods record);

    MiaoShaGoods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MiaoShaGoods record);

    int updateByPrimaryKey(MiaoShaGoods record);
}