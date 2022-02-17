package com.travel.users.providers.mapper;


import com.travel.users.apis.entity.MiaoShaUser;
import org.apache.ibatis.annotations.Param;

public interface MiaoShaUserDao {
    int deleteByPrimaryKey(Long id);

    int insert(MiaoShaUser record);

    int insertSelective(MiaoShaUser record);

    MiaoShaUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MiaoShaUser record);

    int updateByPrimaryKey(MiaoShaUser record);

    MiaoShaUser getByName(String name);

    MiaoShaUser getByNameOrPhone(@Param("nickName") String name, @Param("mobileId") Long id);

}