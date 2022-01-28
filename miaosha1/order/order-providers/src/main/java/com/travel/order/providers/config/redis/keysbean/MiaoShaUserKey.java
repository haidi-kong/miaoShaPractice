package com.travel.order.providers.config.redis.keysbean;


import com.travel.common.config.redis.BasePrefix;

/**
 * @auther 邱润泽 bullock
 * @date 2019/11/10
 */
public class MiaoShaUserKey extends BasePrefix {
    //12分钟
    public static final int TOKEN_EXPIRE = 360 *2;


    public static MiaoShaUserKey token = new MiaoShaUserKey(TOKEN_EXPIRE,"tk") ;
    public static MiaoShaUserKey getByNickName = new MiaoShaUserKey(0, "nickName");

    public MiaoShaUserKey(int expireSeconds ,String prefix) {
        super(expireSeconds,prefix);
    }
}