package com.travel.users.providers.entity;

import com.travel.common.commonDomain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Data
public class MiaoshaUserAccount extends BaseDomain {
    private Integer id;
    private BigDecimal balanceAmount;
    private BigDecimal transferAmount;
    private Long userId;
    private Date createTime;
    private Date updateTime;
}
