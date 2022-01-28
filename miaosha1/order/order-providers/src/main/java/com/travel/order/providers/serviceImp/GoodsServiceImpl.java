package com.travel.order.providers.serviceImp;

import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.MiaoShaGoods;
import com.travel.order.apis.service.GoodsService;
import com.travel.order.providers.logic.GoodsLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
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
}