package com.travel.order.providers.utils;

import com.travel.common.config.redis.RedisServiceImpl;
import com.travel.order.providers.config.redis.keysbean.MiaoshaKey;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.MiaoShaUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author luo
 */
@Slf4j
@Service
public class RandomValidateCodeService {

    //随机产生只有数字的字符串 private String
    private String randString = "0123456789";

    // 随机产生字符数量
    private int stringNum = 4;

    private Random random = new Random();

    @Autowired
    private RedisServiceImpl redisClient;


    /**
     * 生成随机图片
     */
    public String getRandcode(MiaoShaUser user, long goodsId) {

        // 绘制随机字符
        String randomString = "";
        for (int i = 1; i <= stringNum; i++) {
            String rand = String.valueOf(getRandomString(random.nextInt(randString
                    .length())));
            randomString += rand;
        }
        log.info(randomString);
        redisClient.set(MiaoshaKey.getMiaoshaVerifyCode, user.getNickname() + ":" + goodsId, randomString);
        return randomString;
    }

    /**
     * 获取随机的字符
     */
    public String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }

    public boolean checkVerifyCode(MiaoShaUserVo user, long goodsId, String verifyCode) {
        if (user == null || goodsId <= 0) {
            return false;
        }
        String codeOld = (String) redisClient.get(MiaoshaKey.getMiaoshaVerifyCode,
                user.getNickname() + ":" + goodsId, String.class);
        //验证验证码 是否 正确
        if(StringUtils.isEmpty(codeOld) || !codeOld.equals(verifyCode)){
            return false;
        }
        redisClient.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getNickname() + "," + goodsId);
        return true;
    }

}
