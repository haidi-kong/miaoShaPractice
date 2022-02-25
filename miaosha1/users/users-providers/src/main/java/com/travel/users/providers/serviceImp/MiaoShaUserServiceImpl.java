package com.travel.users.providers.serviceImp;

import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.common.utils.MD5Util;
import com.travel.common.utils.UUIDUtil;
import com.travel.users.apis.entity.LoginVo;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.MiaoShaUserVo;
import com.travel.users.apis.entity.RegisterVo;
import com.travel.users.apis.service.MiaoShaUserService;
import com.travel.users.providers.logic.UserLogic;
import com.travel.users.providers.mapper.MiaoShaUserDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

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
    public ResultGeekQ<String> register(RegisterVo registerVo) {
        ResultGeekQ resultGeekQ = ResultGeekQ.build();
        try {
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
            // 存入数据库
            int count =  miaoShaUserDao.insert(miaoShaUser);
            if (count != 1) {
                resultGeekQ.withErrorCodeAndMessage(ResultStatus.SYSTEM_ERROR);
                return resultGeekQ;
            }
        } catch (Exception e) {
            log.error("注册发生错误 error:{}",e);
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
