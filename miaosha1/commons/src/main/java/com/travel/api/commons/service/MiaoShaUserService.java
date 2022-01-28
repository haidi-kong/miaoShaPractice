package com.travel.api.commons.service;

import com.travel.api.commons.vo.LoginVo;
import com.travel.api.commons.vo.MiaoShaUserVo;
import com.travel.api.commons.vo.RegisterVo;
import com.travel.common.resultbean.ResultGeekQ;

import javax.servlet.http.HttpServletResponse;

/**
 * @auther 邱润泽 bullock
 * @date 2019/11/9
 */
public interface MiaoShaUserService {

    public ResultGeekQ<MiaoShaUserVo> getById(Long id);

    public ResultGeekQ<String> login(HttpServletResponse response, LoginVo loginVo);

    public ResultGeekQ<String> register(RegisterVo registerVo);

    ResultGeekQ<MiaoShaUserVo> getByName(String name);
}
