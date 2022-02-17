package com.travel.admin.config.mvc;

import com.travel.admin.config.redis.keysbean.MiaoShaUserKey;
import com.travel.admin.exception.UserException;
import com.travel.admin.utils.CookiesUtilService;
import com.travel.common.config.redis.RedisServiceImpl;
import com.travel.common.enums.ResultStatus;
import com.travel.users.apis.entity.MiaoShaUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.travel.common.enums.CustomerConstant.COOKIE_NAME_TOKEN;
/**
 * @author luo
 */
@Service
@Slf4j
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private RedisServiceImpl redisClient;

    @Autowired
    private CookiesUtilService cookiesUtilService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //获取参数类型
      Class<?> clazz =    methodParameter.getParameterType() ;
      return clazz == MiaoShaUser.class ;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        String cookieToken = CookiesUtilService.getCookieValue(request,COOKIE_NAME_TOKEN);
        String paramToken = request.getParameter(COOKIE_NAME_TOKEN);

        if(StringUtils.isEmpty(cookieToken)&& StringUtils.isEmpty(paramToken)){
            log.info("***resolveArgument token为空请登录!***");
            throw new UserException(ResultStatus.USER_NOT_EXIST);
        }

        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        MiaoShaUser user = cookiesUtilService.getByToken(response,token);
        return user;
    }


}
