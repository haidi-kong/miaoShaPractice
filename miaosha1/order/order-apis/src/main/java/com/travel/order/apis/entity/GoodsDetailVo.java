package com.travel.order.apis.entity;

import com.travel.users.apis.entity.MiaoShaUserVo;
import lombok.Data;

/**
 * @author
 */
@Data
public class GoodsDetailVo {

    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods ;
    private MiaoShaUserVo user;
}
