package com.travel.service;

import com.travel.commons.resultbean.ResultGeekQ;
import com.travel.vo.LoginVo;
import com.travel.vo.MiaoShaUserVo;
import com.travel.vo.RegisterVo;

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
