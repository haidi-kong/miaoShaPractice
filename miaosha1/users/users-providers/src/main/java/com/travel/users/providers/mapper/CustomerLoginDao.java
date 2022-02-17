package com.travel.users.providers.mapper;


import com.travel.users.providers.entity.CustomerLogin;

public interface CustomerLoginDao {
    int deleteByPrimaryKey(Integer customerId);

    int insertSelective(CustomerLogin record);

    CustomerLogin selectByPrimaryKey(Integer customerId);

    int updateByPrimaryKeySelective(CustomerLogin record);
}