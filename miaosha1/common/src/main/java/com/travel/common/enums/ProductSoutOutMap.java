package com.travel.common.enums;

import java.util.concurrent.ConcurrentHashMap;


public class ProductSoutOutMap {


    //商品售完标记map，多线程操作不能用HashMap
    public static final ConcurrentHashMap<String, Boolean> productSoldOutMap = new ConcurrentHashMap<>();

    public static  ConcurrentHashMap<String, Boolean> getProductSoldOutMap() {
        return productSoldOutMap;
    }


}
