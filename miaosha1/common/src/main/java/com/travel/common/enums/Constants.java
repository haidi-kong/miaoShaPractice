package com.travel.common.enums;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public final static Map<Integer, String> ZK_EVENT_MAP = new HashMap<>();

    static  {
        ZK_EVENT_MAP.put(1, "节点被创建");
        ZK_EVENT_MAP.put(2, "节点被删除");
        ZK_EVENT_MAP.put(3, "节点数据变化");
        ZK_EVENT_MAP.put(4, "节点子节点数据变化");
    }





}
