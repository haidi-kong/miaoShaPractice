package com.travel.api.function.dao;

import com.travel.api.function.entity.CustomerInf;

public interface CustomerInfDao {
    int deleteByPrimaryKey(Integer customerInfId);

    int insertSelective(CustomerInf record);

    CustomerInf selectByPrimaryKey(Integer customerInfId);

    int updateByPrimaryKeySelective(CustomerInf record);

}