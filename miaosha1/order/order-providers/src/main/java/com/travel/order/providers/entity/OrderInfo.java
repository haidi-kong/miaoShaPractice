package com.travel.order.providers.entity;

import com.travel.common.commonDomain.BaseDomain;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderInfo extends BaseDomain {
    private Long id;

    private Long userId;

    private Long goodsId;

    private Long deliveryAddrId;

    private String goodsName;

    private Integer goodsCount;

    private BigDecimal goodsPrice;

    private Integer orderChannel;

    private Integer status;

    private Date createDate;

    private Date payDate;
}