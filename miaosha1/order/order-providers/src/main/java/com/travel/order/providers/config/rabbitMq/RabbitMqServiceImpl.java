package com.travel.order.providers.config.rabbitMq;


import com.travel.common.config.redis.RedisServiceImpl;
import com.travel.common.config.zk.ZkApi;
import com.travel.common.enums.CustomerConstant;
import com.travel.order.providers.utils.ProductSoutOutMap;
import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.common.utils.CommonMethod;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.OrderInfoVo;
import com.travel.order.apis.service.MiaoshaService;
import com.travel.order.providers.Exception.MqOrderException;
import com.travel.order.providers.config.redis.keysbean.GoodsKey;
import com.travel.order.providers.entity.miaosha.MiaoShaMessage;
import com.travel.order.providers.entity.miaosha.MiaoShaOrder;
import com.travel.order.providers.logic.GoodsLogic;
import com.travel.order.providers.logic.MiaoShaLogic;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.MiaoShaUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author luo
 */
@Service
@Slf4j
public class RabbitMqServiceImpl{
    @Autowired
    GoodsLogic goodsLogic;
    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    private MiaoShaLogic mSLogic;
    @Autowired
    private RedisServiceImpl redisService;
    @Autowired
    private ZkApi zkApi;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message) {
        try {
            log.info("receive message:" + message);
            MiaoShaMessage mm = redisService.stringToBean(message, MiaoShaMessage.class);
            MiaoShaUser user = mm.getUser();
            long goodsId = mm.getGoodsId();
            GoodsVo goods = goodsLogic.getGoodsVoByGoodsId(goodsId);
            int stock = goods.getStockCount();
            if (stock <= 0) {
                return;
            }
            //判断是否已经秒杀到了
            MiaoShaOrder order = mSLogic.getMiaoshaOrderByUserIdGoodsId(user.getId(),
                    goodsId);
            if (order != null) {
                throw new MqOrderException(ResultStatus.GOOD_EXIST);
            }
            //减库存 下订单 写入秒杀订单
            MiaoShaUserVo userVo = new MiaoShaUserVo();
            BeanUtils.copyProperties(user, userVo);
            //秒杀失败
            ResultGeekQ<OrderInfoVo> msR = miaoshaService.miaosha(userVo, goods);
            if(!ResultGeekQ.isSuccess(msR)){
                //************************ 秒杀失败 回退操作 **************************************
                redisService.incr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
                if (ProductSoutOutMap.productSoldOutMap.get(goodsId) != null) {
                    ProductSoutOutMap.productSoldOutMap.remove(goodsId);
                }
                //修改zk的商品售完标记为false
                try {
                    if (zkApi.exists(CustomerConstant.ZookeeperPathPrefix.getZKSoldOutProductPath(String.valueOf(goodsId)), true) != null) {
                        zkApi.updateNode(CustomerConstant.ZookeeperPathPrefix.getZKSoldOutProductPath(String.valueOf(goodsId)), "false");
                    }
                } catch (Exception e1) {
                    log.error("修改zk商品售完标记异常", e1);
                }
                return;
            }
            OrderInfoVo orderInfo = msR.getData();
            //******************  如果成功则进行保存redis + flag ****************************
            String msKey  = CommonMethod.getMiaoshaOrderRedisKey(String.valueOf(orderInfo.getUserId()), String.valueOf(goodsId));
            redisService.set(msKey, msR.getData());
        } catch (Exception e) {
            log.error("receive message 处理异常", e);
        }


    }
}
