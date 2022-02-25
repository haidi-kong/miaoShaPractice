package com.travel.order.apis.entity;

import com.travel.common.commonDomain.BaseDomain;
import lombok.Data;

/**
 * @author
 */
@Data
public class GoodsDetailVo extends BaseDomain {

    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods ;
    private int isLogin = 0;
}
