package com.travel.users.apis.service;


import com.travel.common.resultbean.ResultGeekQ;
import com.travel.users.apis.entity.*;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @auther luo
 * @date 2019/11/9
 */
@LocalTCC
public interface MiaoShaUserService {

    ResultGeekQ<MiaoShaUserVo> getByPhoneId(Long id);

    ResultGeekQ<MiaoShaUser> login(LoginVo loginVo);

    ResultGeekQ<String> register(RegisterVo registerVo);

    ResultGeekQ<MiaoShaUserVo> getByName(String name);

    /**
     * 支付订单
     */
    //@EnableTcc
    @TwoPhaseBusinessAction(name = "DubboTccActionTwo", commitMethod  = "confirmPay", rollbackMethod  = "cancelPay")
    ResultGeekQ<MiaoShaUserVo> pay(@BusinessActionContextParameter(paramName = "user") MiaoShaUser user,
                                   @BusinessActionContextParameter(paramName = "paymentVo")PaymentVo paymentVo);

    /**
     * 生成登录token
     */
    String createToken(LoginVo loginVo);

    boolean  confirmPay(BusinessActionContext actionContext);

    boolean  cancelPay(BusinessActionContext actionContext);
}
