package com.travel.admin.controller;

import com.alibaba.fastjson.JSON;
import com.travel.admin.utils.CookiesUtilService;
import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.AbstractResult;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.common.utils.UUIDUtil;
import com.travel.users.apis.entity.LoginVo;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.RegisterVo;
import com.travel.users.apis.service.MiaoShaUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.security.auth.login.AccountException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author luo
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    private CookiesUtilService cookiesUtilService;


    @DubboReference(check = false)
    private MiaoShaUserService miaoShaUserService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/to_register")
    public String toRegister() {
        return "register";
    }



    @RequestMapping("/do_login")
    @ResponseBody
    public ResultGeekQ<String> tologin(@Valid LoginVo loginVo, HttpServletResponse response) {
        log.info("登录开始 start! loginvo:{}", JSON.toJSON(loginVo));
        ResultGeekQ resultGeekQ = ResultGeekQ.build();
        try {
            ResultGeekQ<MiaoShaUser> result = miaoShaUserService.login(loginVo);
            if(!AbstractResult.isSuccess(result)){
                resultGeekQ.withError(result.getCode(),result.getMessage());
                return resultGeekQ;
            }
            // 返回页面Cookie
            String token = UUIDUtil.getUUid();
            cookiesUtilService.addCookie(response, token, result.getData());
        } catch (Exception e) {
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.SYSTEM_ERROR);
        }
        return resultGeekQ;
    }

    @PostMapping("/do_register")
    @ResponseBody
    public ResultGeekQ<String> register(@Valid RegisterVo registerVo) {
        log.info("开始注册 registerVo:{}", JSON.toJSON(registerVo));
        ResultGeekQ resultGeekQ = ResultGeekQ.build();
        try {
            ResultGeekQ result = miaoShaUserService.register(registerVo);
            if(!AbstractResult.isSuccess(result)){
                resultGeekQ.withError(result.getCode(),result.getMessage());
                return resultGeekQ;
            }
        } catch (Exception e) {
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.SYSTEM_ERROR);
        }
        return resultGeekQ;
    }

    @GetMapping("/getWebStatistic")
    @ResponseBody
    public ResultGeekQ<Integer> getWebStatistic() {
        ResultGeekQ resultGeekQ = ResultGeekQ.build();
        try {
            resultGeekQ.setData(cookiesUtilService.getWebStatistic());
        } catch (Exception e) {
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.SYSTEM_ERROR);
        }
        return resultGeekQ;
    }

    @RequestMapping("/create_token")
    @ResponseBody
    public String createToken(LoginVo loginVo, HttpServletResponse response) throws AccountException {
        log.info(loginVo.toString());
        try {
            ResultGeekQ<MiaoShaUser> result = miaoShaUserService.login(loginVo);
            if(!AbstractResult.isSuccess(result)){
                throw new AccountException("user error");
            }
            // 返回页面Cookie
            String token = UUIDUtil.getUUid();
            cookiesUtilService.addCookie(response, token, result.getData());
            return token;
        } catch (Exception e) {
            throw new AccountException("user error");
        }

    }

}
