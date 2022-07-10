package com.travel.order.providers.mapper;


import com.travel.order.providers.entity.OrderInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderInfoDao {
    int deleteByPrimaryKey(Long id);

    int insert(OrderInfo record);

    Long insertSelective(OrderInfo record);

    OrderInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderInfo record);

    int updateByPrimaryKey(OrderInfo record);

    OrderInfo getOrderById(@Param("orderId")long orderId);

    OrderInfo getOrderByUserId(@Param("userId")long userId);

    List<OrderInfo> getOrderList();
}