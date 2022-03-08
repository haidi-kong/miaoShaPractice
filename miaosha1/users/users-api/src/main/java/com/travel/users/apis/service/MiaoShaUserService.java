package com.travel.users.apis.service;


import com.travel.common.resultbean.ResultGeekQ;
import com.travel.users.apis.entity.*;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

/**
 * @auther luo
 * @date 2019/11/9
 */
public interface MiaoShaUserService {

    ResultGeekQ<MiaoShaUserVo> getByPhoneId(Long id);

    ResultGeekQ<MiaoShaUser> login(LoginVo loginVo);

    ResultGeekQ<String> register(RegisterVo registerVo);

    ResultGeekQ<MiaoShaUserVo> getByName(String name);

    /**
     * 支付订单
     */
    @EnableTcc
    ResultGeekQ<MiaoShaUserVo> pay(MiaoShaUser user, PaymentVo paymentVo);
}
