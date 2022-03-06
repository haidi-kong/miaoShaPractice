package com.travel.users.apis.entity;

import com.travel.common.commonDomain.BaseDomain;
import com.travel.users.apis.valiadator.CheckMobile;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@Data
public class PaymentVo extends BaseDomain {

    /**
     * 支付金额
     */
    @NotNull
    private BigDecimal payAmount;

    /**
     * 订单号 每发起一次需要重新生成
     */
    @NotNull
    private long orderId;

}
