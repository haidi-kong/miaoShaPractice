package com.travel.order.providers.utils.enums;

public  enum OrderStatus {
    ORDER_NOT_PAY(0,"新建未支付"),
    ORDER_PAYING(6,"付款中"),
    ORDER_PYA_COMPLETE(1,"已支付"),
    ORDER_PYA_CANCEL(7,"已取消");

    private int code;
    private String message;

    private OrderStatus(int code, String message) {
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