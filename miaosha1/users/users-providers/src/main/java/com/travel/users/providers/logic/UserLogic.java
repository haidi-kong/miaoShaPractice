package com.travel.users.providers.logic;

import com.travel.users.apis.entity.MiaoShaUser;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public interface UserLogic {

    public MiaoShaUser getById(Long id);

}
