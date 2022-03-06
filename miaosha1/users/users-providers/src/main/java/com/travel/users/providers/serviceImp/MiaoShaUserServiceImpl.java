package com.travel.users.providers.serviceImp;

import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.common.utils.MD5Util;
import com.travel.common.utils.UUIDUtil;
import com.travel.users.apis.entity.*;
import com.travel.users.apis.service.MiaoShaUserService;
import com.travel.users.providers.entity.MiaoshaPayment;
import com.travel.users.providers.entity.MiaoshaUserAccount;
import com.travel.users.providers.exceptions.AccountException;
import com.travel.users.providers.logic.UserLogic;
import com.travel.users.providers.mapper.MiaoShaUserDao;
import com.travel.users.providers.mapper.MiaoshaPaymentDao;
import com.travel.users.providers.mapper.MiaoshaUserAccountDao;
import com.travel.users.providers.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @auther luo
 * @date 2019/11/9
 */
@Service
@Slf4j
@DubboService(timeout = 9000000, cluster = "failfast")
public class MiaoShaUserServiceImpl implements MiaoShaUserService {


    @Autowired
    private MiaoShaUserDao miaoShaUserDao;

    @Autowired
    private MiaoshaUserAccountDao miaoshaUserAccountDao;

    @Autowired
    private MiaoshaPaymentDao miaoshaPaymentDao;



    @Override
    public ResultGeekQ<MiaoShaUserVo> getByPhoneId(Long id) {
        ResultGeekQ<MiaoShaUserVo> resultGeekQ  = ResultGeekQ.build();
        try{
            MiaoShaUser user = miaoShaUserDao.selectByPrimaryKey(id);
            MiaoShaUserVo userVo = new MiaoShaUserVo();
            BeanUtils.copyProperties(user,userVo);
            resultGeekQ.setData(userVo);
            return resultGeekQ;
        }catch(Exception e){
            log.error("***获取秒杀用户对象失败！getById*** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            return resultGeekQ;
        }
    }

    @Override
    public ResultGeekQ<MiaoShaUser> login(LoginVo loginVo) {

        ResultGeekQ<MiaoShaUser> resultGeekQ = ResultGeekQ.build();
        try {
            if (loginVo == null) {
                resultGeekQ.withErrorCodeAndMessage(ResultStatus.SYSTEM_ERROR);
                return resultGeekQ;
            }
            String formPass = loginVo.getPassword();
            ResultGeekQ<MiaoShaUserVo> userVo = getByPhoneId(loginVo.getMobile());
            if (!ResultGeekQ.isSuccess(userVo)) {
                resultGeekQ.withErrorCodeAndMessage(ResultStatus.MOBILE_NOT_EXIST);
                return resultGeekQ;
            }
            MiaoShaUserVo user = userVo.getData();
            String dbPass = user.getPassword();
            String saltDB = user.getSalt();

            String calcPass = MD5Util.inputPassToDbPass(formPass, saltDB);
            if (!calcPass.equals(dbPass)) {
                resultGeekQ.withErrorCodeAndMessage(ResultStatus.PASSWORD_ERROR);
                return resultGeekQ;
            }
            //返回页面token
            String token = UUIDUtil.getUUid();
            MiaoShaUser mSuser = new MiaoShaUser();
            BeanUtils.copyProperties(user,mSuser);
            resultGeekQ.setData(mSuser);
        } catch (Exception e) {
            log.error("登陆发生错误 error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.SYSTEM_ERROR);
            return resultGeekQ;
        }
        return resultGeekQ;
    }

    @Override
    @Transactional
    public ResultGeekQ<String> register(RegisterVo registerVo) {
        ResultGeekQ resultGeekQ = ResultGeekQ.build();

        if (registerVo == null) {
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.SYSTEM_ERROR);
        }
        ResultGeekQ<MiaoShaUserVo> userVo = getByNameOrPhone(registerVo.getNickname(), registerVo.getMobile());
        // 存在相同手机号或者相同用户名
        if (ResultGeekQ.isSuccess(userVo)) {
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.USER_ALREADY_EXIST);
            return resultGeekQ;
        }
        // 加密存入密码
        registerVo.setSalt(MD5Util.getRandomSalt());
        registerVo.setPassword(MD5Util.inputPassToDbPass(registerVo.getPassword(), registerVo.getSalt()));
        MiaoShaUser miaoShaUser = new MiaoShaUser();
        BeanUtils.copyProperties(registerVo, miaoShaUser);
        miaoShaUser.setId(registerVo.getMobile());
        miaoShaUser.setRegisterDate(new Date());
        // 存入数据库
        int count =  miaoShaUserDao.insert(miaoShaUser);
        // 初始化存款账户
        MiaoshaUserAccount miaoshaUserAccount = new MiaoshaUserAccount();
        miaoshaUserAccount.setBalanceAmount(new BigDecimal(2000));
        miaoshaUserAccount.setUserId(miaoShaUser.getId());
        miaoshaUserAccount.setCreateTime(new Date());
        miaoshaUserAccount.setUpdateTime(new Date());
        miaoshaUserAccountDao.insertSelective(miaoshaUserAccount);
        if (count != 1) {
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.SYSTEM_ERROR);
            return resultGeekQ;
        }

        return resultGeekQ;
    }


    @Override
    public ResultGeekQ<MiaoShaUserVo> getByName(String name) {
        ResultGeekQ<MiaoShaUserVo> resultGeekQ  = ResultGeekQ.build();
        try{
            MiaoShaUser user = miaoShaUserDao.getByName(name);;
            MiaoShaUserVo userVo = new MiaoShaUserVo();
            BeanUtils.copyProperties(user,userVo);
            resultGeekQ.setData(userVo);
            return resultGeekQ;
        }catch(Exception e){
            log.error("***获取秒杀用户对象失败！getByName*** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            return resultGeekQ;
        }
    }

    @Override
    @Compensable(confirmMethod = "confirmPay", cancelMethod = "cancelPay")
    @Transactional
    /**
     * 支付订单 预留扣款资源
     */
    public ResultGeekQ<MiaoShaUserVo> pay(MiaoShaUser user, PaymentVo paymentVo) {
        ResultGeekQ<MiaoShaUserVo> resultGeekQ  = ResultGeekQ.build();
        log.info("start tcc pay try, user:{}, paymentVo:{}", user, paymentVo);
        MiaoshaPayment miaoshaPaymentDb = miaoshaPaymentDao.selectByOrderID(paymentVo.getOrderId());
        MiaoshaUserAccount miaoshaUserAccountDb = miaoshaUserAccountDao.selectByUserID(user.getId());
        // 金额是否足够用
        if (miaoshaUserAccountDb.getBalanceAmount() == null
                || miaoshaUserAccountDb.getBalanceAmount().compareTo(paymentVo.getPayAmount()) < 0) {
            throw new AccountException("支付金额不足");
        }
        // 判断支付记录是否存在，try具有重试机制，需要幂等性
        if (miaoshaPaymentDb != null) {
            // 账户欲扣款
            MiaoshaUserAccount miaoshaUserAccount = new MiaoshaUserAccount();
            miaoshaUserAccount.setUserId(user.getId());
            miaoshaUserAccount.setTransferAmount(paymentVo.getPayAmount());
            miaoshaUserAccountDao.updateByUserID(miaoshaUserAccount);
            // 插入欲扣款记录
            MiaoshaPayment miaoshaPayment = new MiaoshaPayment();
            miaoshaPayment.setAmount(paymentVo.getPayAmount());
            miaoshaPayment.setMiaoshaOrderId(paymentVo.getOrderId());
            miaoshaPayment.setUserId(user.getId());
            miaoshaPayment.setCreateTime(new Date());
            miaoshaPayment.setUpdateTime(new Date());
            miaoshaPayment.setStatus(Constants.PAY_DEALING);
            miaoshaPayment.setVersion(1);
            miaoshaPaymentDao.insertSelective(miaoshaPayment);
        }

        return resultGeekQ;
    }


    @Transactional
    /**
     * 支付订单 预留扣款资源
     */
    public ResultGeekQ<MiaoShaUserVo> confirmPay(MiaoShaUser user, PaymentVo paymentVo) {
        ResultGeekQ<MiaoShaUserVo> resultGeekQ  = ResultGeekQ.build();
        log.info("start tcc pay confirm, user:{}, paymentVo:{}, ", user, paymentVo);
        MiaoshaPayment miaoshaPaymentDb = miaoshaPaymentDao.selectByOrderID(paymentVo.getOrderId());
        // 防止超时等导致try事务悬挂  以及幂等
        if (miaoshaPaymentDb != null && Constants.PAY_DEALING.equals(miaoshaPaymentDb.getStatus())) {
            //账户确认扣款
            MiaoshaUserAccount miaoshaUserAccount = new MiaoshaUserAccount();
            miaoshaUserAccount.setUserId(user.getId());
            miaoshaUserAccount.setBalanceAmount(paymentVo.getPayAmount().negate());
            miaoshaUserAccount.setTransferAmount(paymentVo.getPayAmount().negate());
            miaoshaUserAccount.setUpdateTime(new Date());
            miaoshaUserAccountDao.updateByUserID(miaoshaUserAccount);
            // 更新扣款记录为成功 todo: 版本号控制 防止并发事务 具体根据事务隔离级别
            MiaoshaPayment miaoshaPayment = new MiaoshaPayment();
            miaoshaPayment.setMiaoshaOrderId(paymentVo.getOrderId());
            miaoshaPayment.setUserId(user.getId());
            miaoshaPayment.setUpdateTime(new Date());
            miaoshaPayment.setStatus(Constants.PAY_SUCCESS);
            miaoshaPaymentDao.updateByUserID(miaoshaPayment);
        }

        return resultGeekQ;
    }

    @Transactional
    /**
     * 支付订单 预留扣款资源
     */
    public ResultGeekQ<MiaoShaUserVo> cancelPay(MiaoShaUser user, PaymentVo paymentVo) {
        ResultGeekQ<MiaoShaUserVo> resultGeekQ  = ResultGeekQ.build();
        log.info("start tcc pay cancel, user:{}, paymentVo:{}, ", user, paymentVo);
        MiaoshaPayment miaoshaPaymentDb = miaoshaPaymentDao.selectByOrderID(paymentVo.getOrderId());
        // 防止超时等导致try事务悬挂 以及幂等
        if (miaoshaPaymentDb != null && Constants.PAY_DEALING.equals(miaoshaPaymentDb.getStatus())) {
            // 账户扣款
            MiaoshaUserAccount miaoshaUserAccount = new MiaoshaUserAccount();
            miaoshaUserAccount.setUserId(user.getId());
            miaoshaUserAccount.setTransferAmount(paymentVo.getPayAmount().negate());
            miaoshaUserAccount.setUpdateTime(new Date());
            miaoshaUserAccountDao.updateByUserID(miaoshaUserAccount);
            // 更新扣款记录为成功 todo: 版本号控制 防止并发事务 具体根据事务隔离级别
            MiaoshaPayment miaoshaPayment = new MiaoshaPayment();
            miaoshaPayment.setMiaoshaOrderId(paymentVo.getOrderId());
            miaoshaPayment.setUserId(user.getId());
            miaoshaPayment.setUpdateTime(new Date());
            miaoshaPayment.setStatus(Constants.PAY_FAILED);
            miaoshaPaymentDao.updateByUserID(miaoshaPayment);
        }
        return resultGeekQ;
    }

    private ResultGeekQ<MiaoShaUserVo> getByNameOrPhone(String name, Long id) {
        ResultGeekQ<MiaoShaUserVo> resultGeekQ  = ResultGeekQ.build();
        try{
            MiaoShaUser user = miaoShaUserDao.getByNameOrPhone(name, id);
            if (user == null) {
                resultGeekQ.withErrorCodeAndMessage(ResultStatus.USER_ALREADY_EXIST);
            }
            return resultGeekQ;
        }catch(Exception e){
            log.error("***判断注册对象失败！getByNameOrId*** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.USER_ALREADY_EXIST);
            return resultGeekQ;
        }
    }



//    // http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
//    public boolean updatePassword(String token, String nickName, String formPass) {
//        //取user
//        MiaoShaUser user = getByName(nickName);
//        if(user == null) {
//            throw new GlobleException(MOBILE_NOT_EXIST);
//        }
//        //更新数据库
//        MiaoShaUser toBeUpdate = new MiaoShaUser();
//        toBeUpdate.setNickname(nickName);
//        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
//        miaoShaUserDao.updateByPrimaryKeySelective(toBeUpdate);
//        //处理缓存
//        redisClient.delete(MiaoShaUserKey.getByNickName, ""+nickName);
//        user.setPassword(toBeUpdate.getPassword());
//        redisClient.set(MiaoShaUserKey.token, token, user);
//        return true;
//    }

}
