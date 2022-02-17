package com.travel.order.providers.utils.enums;

public  enum  MiaoShaStatus {
    MIAO_SHA_NOT_START(0,"未开始秒杀"),
    MIAO_SHA_START(1,"开始秒杀"),
    MIAO_SHA_END(2,"秒杀结束");

    private int code;
    private String message;

    private MiaoShaStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}