package com.travel.order.apis.entity;


import lombok.Data;

@Data
public class OrderDetailVo {
	private GoodsVo goods;
	private OrderInfoVo order;
}
