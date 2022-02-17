package com.travel.order.providers.serviceImp;

import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.GoodsDetailVo;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.MiaoShaGoods;
import com.travel.order.apis.service.GoodsService;
import com.travel.order.providers.logic.GoodsLogic;
import com.travel.order.providers.utils.enums.MiaoShaStatus;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.MiaoShaUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@DubboService(timeout = 900000, cluster = "failfast")
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsLogic goodsLogic;

    @Override
    public ResultGeekQ<List<GoodsVo>> goodsVoList() {
        ResultGeekQ<List<GoodsVo>> resultGeekQ = ResultGeekQ.build();

        try{
            log.info("***goodsVoList查询***start!");
            resultGeekQ.setData(goodsLogic.goodsVoList());
        }catch(Exception e){
            log.error(" *****查询goodsvoList发生错误***** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.DATA_NOT_EXISTS);
            return resultGeekQ;
        }
        return resultGeekQ;
    }

    @Override
    public ResultGeekQ<GoodsVo> goodsVoByGoodId(Long goodId) {

        ResultGeekQ<GoodsVo> resultGeekQ = ResultGeekQ.build();
        try{
            log.info("***goodsVoByGoodId查询***start!");
            resultGeekQ.setData(goodsLogic.goodsVoByGoodsId(goodId));
        }catch(Exception e){
            log.error(" *****查询goodsVoByGoodId发生错误***** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.DATA_NOT_EXISTS);
            return resultGeekQ;
        }
        return resultGeekQ;
    }

    @Override
    public ResultGeekQ<Boolean> reduceStock(GoodsVo goods) {

        ResultGeekQ<Boolean> resultGeekQ = ResultGeekQ.build();
        try {
            log.info("***reduceStock***start!");
            MiaoShaGoods g = new MiaoShaGoods();
            g.setGoodsId(goods.getId());
            Boolean reduceSorF = goodsLogic.reduceStock(g)>0;
            if(reduceSorF==false){
                log.error(" *****reduceSorF扣减库存发生错误*****");
                resultGeekQ.withErrorCodeAndMessage(ResultStatus.DATA_NOT_EXISTS);
                return resultGeekQ;
            }
            resultGeekQ.setData(reduceSorF);
        } catch(Exception e){
            log.error(" *****reduceStock扣减库存发生错误***** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.DATA_NOT_EXISTS);
            return resultGeekQ;
        }
        return resultGeekQ;
    }

    @Override
    public ResultGeekQ<GoodsDetailVo> goodsDetail(MiaoShaUser user, String goodsId) {
        ResultGeekQ resultGeekQ = ResultGeekQ.build();
        try {
            ResultGeekQ<GoodsVo> goodR = goodsVoByGoodId(Long.valueOf(goodsId));
            if(!ResultGeekQ.isSuccess(goodR)){
                resultGeekQ.withError(goodR.getCode(),goodR.getMessage());
                return resultGeekQ;
            }
            GoodsVo goods = goodR.getData();
            long startAt = goods.getStartDate().getTime();
            long endAt = goods.getEndDate().getTime();
            long now = System.currentTimeMillis();
            int miaoshaStatus = 0;
            int remainSeconds = 0;
            //秒杀还没开始，倒计时
            if (now < startAt) {
                miaoshaStatus = MiaoShaStatus.MIAO_SHA_NOT_START.getCode();
                remainSeconds = (int) ((startAt - now) / 1000);
                //秒杀已经结束
            } else if (now > endAt) {
                miaoshaStatus = MiaoShaStatus.MIAO_SHA_END.getCode();
                remainSeconds = -1;
                //秒杀进行中
            } else {
                miaoshaStatus = MiaoShaStatus.MIAO_SHA_END.getCode();
                remainSeconds = 0;
            }
            GoodsDetailVo vo = new GoodsDetailVo();
            vo.setGoods(goods);
            MiaoShaUserVo userVo = new MiaoShaUserVo();
            BeanUtils.copyProperties(user,userVo);
            vo.setUser(userVo);
            vo.setRemainSeconds(remainSeconds);
            vo.setMiaoshaStatus(miaoshaStatus);
            resultGeekQ.setData(vo);
        } catch (Exception e) {
            log.error("秒杀明细请求失败 error:{}", e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            return resultGeekQ;
        }
        return resultGeekQ;
    }
}