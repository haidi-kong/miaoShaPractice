package com.travel.admin.utils;

import com.travel.admin.config.redis.keysbean.MiaoShaUserKey;
import com.travel.admin.config.redis.keysbean.WebStatisticKey;
import com.travel.common.config.redis.RedisServiceImpl;
import com.travel.users.apis.entity.MiaoShaUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.travel.common.enums.CustomerConstant.COOKIE_NAME_TOKEN;

/**
 * @author luo
 */
@Slf4j
@Service
public class CookiesUtilService {

    @Autowired
    private RedisServiceImpl redisClient;

    public static String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        Optional cookiesV =  Optional.ofNullable(cookies);
        cookiesV.orElseGet(()->{
            log.error(" ***cookies 为null! 请登录***");
            return null;
        });

        if(!cookiesV.isPresent()){
            return null;
        }
        List<String> tokens =  Arrays.asList(cookies).stream().
                filter(cookie -> cookie.getName().equals(cookieNameToken))
                .map(cookie -> cookie.getValue())
                .collect(Collectors.toList());
        return CollectionUtils.isEmpty(tokens)?"":tokens.get(0);
    };

    public HttpServletResponse addCookie(HttpServletResponse response, String token, MiaoShaUser user) {
        redisClient.set(MiaoShaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        //设置有效期
        cookie.setMaxAge(MiaoShaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
        return response ;
    }

    public MiaoShaUser getByToken(HttpServletResponse response , String token) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        MiaoShaUser user = (MiaoShaUser) redisClient.get(MiaoShaUserKey.token,token, MiaoShaUser.class);

        if(user!=null){
            addCookie(response,token,user);
        }
        return user;
    }

    public Integer getWebStatistic() {
        WebStatisticKey webStatisticKey = WebStatisticKey.withExpire(100);
        Integer count = (Integer) redisClient.get(webStatisticKey, "_data", Integer.class);
        return count != null ?  count : 0;
    }

}
