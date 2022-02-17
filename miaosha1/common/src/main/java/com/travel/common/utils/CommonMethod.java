package com.travel.common.utils;

import com.travel.common.enums.CustomerConstant;

/**
 * @author luo
 */
public class CommonMethod {

    public static String getMiaoshaOrderRedisKey(String accountId, String productId) {
        return CustomerConstant.RedisKeyPrefix.MIAOSHA_ORDER + "_" + accountId + "_" + productId;
    }

    public static String getMiaoshaOrderWaitFlagRedisKey(String accountId, String productId) {
        return CustomerConstant.RedisKeyPrefix.MIAOSHA_ORDER_WAIT + "_" + accountId + "_" + productId;
    }

    public static String getMiaoshaTokenRedisKey(String accountId, String productId) {
        return CustomerConstant.RedisKeyPrefix.MIAOSHA_ORDER_TOKEN + "_" + accountId + "_" + productId;
    }

    public static String getMiaoshaVerifyCodeRedisKey(String accountId, String productId) {
        return CustomerConstant.RedisKeyPrefix.MIAOSHA_VERIFY_CODE + "_" + accountId + "_" + productId;
    }
}
