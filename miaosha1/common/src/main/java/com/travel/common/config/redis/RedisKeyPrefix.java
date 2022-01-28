package com.travel.common.config.redis;

public interface RedisKeyPrefix {

    public int expireSeconds() ;

    public String getPrefix() ;

}
