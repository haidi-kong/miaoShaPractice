package com.travel.order.providers.utils;


import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.GoodsVo;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;

/**
 * @author luo
 */
@Slf4j
public class ValidMSTime {


    /**
     * 校验商品的秒杀时间
     */
    public static ResultGeekQ validMiaoshaTime(GoodsVo goodsVo){

        ResultGeekQ resultGeekQ = ResultGeekQ.build();
        try {
            long startAt = goodsVo.getStartDate().getTime();
            long endAt = goodsVo.getEndDate().getTime();
            long now = System.currentTimeMillis();
            //秒杀还没开始
            if (now < startAt) {
                resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_NOT_START);
                //秒杀已经结束
            } else if (now > endAt) {
                resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_END);
            }
            return resultGeekQ;
        } catch (Exception e) {
            log.error("***校验时间发生错误*** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.SYSTEM_ERROR);
            return resultGeekQ;
        }

    }

}
