package com.travel.order.providers.logic.impl;

import com.travel.common.config.redis.RedisServiceImpl;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.providers.config.redis.keysbean.MiaoShaUserKey;
import com.travel.order.providers.config.redis.keysbean.MiaoshaKey;
import com.travel.order.providers.entity.miaosha.MiaoShaOrder;
import com.travel.order.providers.entity.OrderInfo;
import com.travel.order.providers.logic.MiaoShaLogic;
import com.travel.order.providers.mapper.MiaoShaOrderDao;
import com.travel.order.providers.mapper.OrderInfoDao;
import com.travel.users.apis.entity.MiaoShaUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static com.travel.common.enums.CustomerConstant.COOKIE_NAME_TOKEN;

/**
 * @author luo
 */
@Slf4j
@Service
public class MiaoShaLogicImpl implements MiaoShaLogic {

    @Autowired
    private OrderInfoDao orderInfoDao;

    @Autowired
    private MiaoShaOrderDao miaoShaOrderDao;

    @Autowired
    private RedisServiceImpl redisClient;

    @Override
    public OrderInfo createOrder(MiaoShaUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderInfoDao.insertSelective(orderInfo);
        MiaoShaOrder miaoshaOrder = new MiaoShaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        miaoShaOrderDao.insertSelective(miaoshaOrder);
        return orderInfo;
    }

    @Override
    public int insertMiaoshaOrder(MiaoShaOrder miaoshaOrder) {
        return miaoShaOrderDao.insertSelective(miaoshaOrder);
    }

    @Override
    public MiaoShaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId) {
        return miaoShaOrderDao.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
    }

    @Override
    public boolean checkPath(MiaoShaUser user, long goodsId, String path) {
        if (user == null || path == null) {
            return false;
        }
        String pathOld = (String) redisClient.get(MiaoshaKey.getMiaoshaPath,
                "" + user.getNickname() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    @Override
    public List<MiaoShaOrder> listByGoodsId(Long goodsId) {
        return miaoShaOrderDao.listByGoodsId(goodsId);
    }

    // todo 去掉user相关
    @Override
    public MiaoShaUser getById(Long id) {
        //return mUserDao.selectByPrimaryKey(id);
        return new MiaoShaUser();
    }

    @Override
    public HttpServletResponse addCookie(HttpServletResponse response, String token, MiaoShaUser user) {
        redisClient.set(MiaoShaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        //设置有效期
        cookie.setMaxAge(MiaoShaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
        return response ;
    }

    @Override
    public Long insertSelective(OrderInfo record) {
        return orderInfoDao.insertSelective(record);
    }

    @Override
    public OrderInfo getOrderById(Long orderId) {
        return orderInfoDao.getOrderById(orderId);
    }

    @Override
    public MiaoShaUser getByToken(HttpServletResponse response , String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoShaUser user = (MiaoShaUser) redisClient.get(MiaoShaUserKey.token,token,MiaoShaUser.class);
        if(user!=null){
            addCookie(response,token,user);
        }
        return user;
    }

}
