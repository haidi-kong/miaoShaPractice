package com.travel.users.providers.mapper;


import com.travel.users.providers.entity.MiaoshaPayment;

public interface MiaoshaPaymentDao {

    int insertSelective(MiaoshaPayment miaoshaPayment);

    MiaoshaPayment selectByOrderID(Long orderId);

    int updateByUserID(MiaoshaPayment miaoshaPayment);

}