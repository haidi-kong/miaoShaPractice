package com.travel.users.apis.service;


import com.travel.common.resultbean.ResultGeekQ;
import com.travel.users.apis.entity.LoginVo;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.MiaoShaUserVo;
import com.travel.users.apis.entity.RegisterVo;

import javax.servlet.http.HttpServletResponse;

/**
 * @auther luo
 * @date 2019/11/9
 */
public interface MiaoShaUserService {

    ResultGeekQ<MiaoShaUserVo> getByPhoneId(Long id);

    ResultGeekQ<MiaoShaUser> login(LoginVo loginVo);

    ResultGeekQ<String> register(RegisterVo registerVo);

    ResultGeekQ<MiaoShaUserVo> getByName(String name);
}
