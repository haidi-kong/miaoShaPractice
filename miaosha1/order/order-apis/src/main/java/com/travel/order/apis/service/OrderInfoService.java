package com.travel.order.apis.service;


import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.OrderInfoVo;

/**
 * @author lzj
 */
public interface OrderInfoService {
    ResultGeekQ<Integer> insertSelective(OrderInfoVo record);
}
