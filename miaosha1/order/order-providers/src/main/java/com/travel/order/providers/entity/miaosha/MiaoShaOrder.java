package com.travel.order.providers.entity.miaosha;

import com.travel.common.commonDomain.BaseDomain;
import lombok.Data;

@Data
public class MiaoShaOrder extends BaseDomain {
    private Long id;

    private Long userId;

    private Long orderId;

    private Long goodsId;

    private Long status;

}