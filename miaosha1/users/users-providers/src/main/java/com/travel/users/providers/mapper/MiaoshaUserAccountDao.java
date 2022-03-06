package com.travel.users.providers.mapper;


import com.travel.users.providers.entity.MiaoshaUserAccount;

public interface MiaoshaUserAccountDao {

    int insertSelective(MiaoshaUserAccount miaoshaUserAccount);

    MiaoshaUserAccount selectByUserID(Long userId);

    int updateByUserID(MiaoshaUserAccount miaoshaUserAccount);


}