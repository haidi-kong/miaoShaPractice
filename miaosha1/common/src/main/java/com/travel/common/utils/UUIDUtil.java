package com.travel.common.utils;

import java.util.UUID;

/**
 * @auther luo
 * @date 2019/11/10
 */
public class UUIDUtil {

    public static String getUUid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
