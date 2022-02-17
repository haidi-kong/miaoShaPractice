package com.travel.users.providers.logic.impl;

import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.providers.logic.UserLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author luo
 */
@Slf4j
@Service
public class UserLogicImpl implements UserLogic {

    @Override
    public MiaoShaUser getById(Long id) {
        //return mUserDao.selectByPrimaryKey(id);
        return new MiaoShaUser();
    }

}
