package com.travel.api.function.dao;

import com.travel.api.function.entity.CustomerLogin;

public interface CustomerLoginDao {
    int deleteByPrimaryKey(Integer customerId);

    int insertSelective(CustomerLogin record);

    CustomerLogin selectByPrimaryKey(Integer customerId);

    int updateByPrimaryKeySelective(CustomerLogin record);
}