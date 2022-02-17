package com.travel.users.providers.mapper;


import com.travel.users.providers.entity.CustomerInf;

public interface CustomerInfDao {
    int deleteByPrimaryKey(Integer customerInfId);

    int insertSelective(CustomerInf record);

    CustomerInf selectByPrimaryKey(Integer customerInfId);

    int updateByPrimaryKeySelective(CustomerInf record);

}