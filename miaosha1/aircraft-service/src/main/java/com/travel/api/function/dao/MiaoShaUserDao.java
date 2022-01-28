package com.travel.api.function.dao;

import com.travel.api.function.entity.MiaoShaUser;
import org.apache.ibatis.annotations.Param;

public interface MiaoShaUserDao {
    int deleteByPrimaryKey(Long id);

    int insert(MiaoShaUser record);

    int insertSelective(MiaoShaUser record);

    MiaoShaUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MiaoShaUser record);

    int updateByPrimaryKey(MiaoShaUser record);

    MiaoShaUser getByName(String name);

    MiaoShaUser getByNameOrId(@Param("nickName") String name, @Param("mobileId") Long id);

}