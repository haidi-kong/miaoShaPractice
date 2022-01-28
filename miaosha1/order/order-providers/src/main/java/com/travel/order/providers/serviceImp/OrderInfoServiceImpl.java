package com.travel.order.providers.serviceImp;

import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.OrderInfoVo;
import com.travel.order.apis.service.OrderInfoService;
import com.travel.order.providers.logic.MiaoShaLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private MiaoShaLogic mSLogic ;

    @Override
    public ResultGeekQ<Integer> insertSelective(OrderInfoVo record) {

        ResultGeekQ<Integer> resultGeekQ = ResultGeekQ.build();
        try{

//            OrderInfo info = new OrderInfo();
//            BeanUtils.copyProperties(record,info);
//            OrderInfo result = mSLogic.insertSelective(info);
//            if(result==null){
//                log.error("***insertSelective*** fail");
//                resultGeekQ.withErrorCodeAndMessage(ResultStatus.SYSTEM_ERROR);
//                return resultGeekQ;
//            }
//            resultGeekQ.setData(11);
            return resultGeekQ;
        }catch(Exception e){
            log.error("***查询失败insertSelective *** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            return resultGeekQ;
        }
    }

}