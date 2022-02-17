package com.travel.order.providers.entity.miaosha;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MiaoShaGoodsVo {
    private Long id;

    private Long goodsId;

    private BigDecimal miaoshaPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

}