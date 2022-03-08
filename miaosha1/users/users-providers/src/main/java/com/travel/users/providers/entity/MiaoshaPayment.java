package com.travel.users.providers.entity;

import com.travel.common.commonDomain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;


@EqualsAndHashCode(callSuper = false)
@Data
public class MiaoshaPayment extends BaseDomain {
    private Integer id;
    private Long userId;
    private Long miaoshaOrderId;
    private BigDecimal amount;
    private String status;
    private Date createTime;
    private Date updateTime;
    private Integer version;
}
