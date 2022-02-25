package com.travel.order.providers.serviceImp.miaosha;

import com.alibaba.fastjson.JSON;
import com.travel.common.config.redis.RedisServiceImpl;
import com.travel.common.config.zk.ZkApi;
import com.travel.common.enums.CustomerConstant;
import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.common.utils.CommonMethod;
import com.travel.common.utils.MD5Util;
import com.travel.common.utils.UUIDUtil;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.MiaoShaOrderVo;
import com.travel.order.apis.entity.OrderInfoVo;
import com.travel.order.apis.service.GoodsService;
import com.travel.order.apis.service.MiaoshaService;
import com.travel.order.apis.service.OrderService;
import com.travel.order.providers.config.Zookeeper.WatcherApi;
import com.travel.order.providers.config.rabbitMq.MQSender;
import com.travel.order.providers.config.redis.keysbean.GoodsKey;
import com.travel.order.providers.config.redis.keysbean.MiaoshaKey;
import com.travel.order.providers.entity.OrderInfo;
import com.travel.order.providers.entity.miaosha.MiaoShaMessage;
import com.travel.order.providers.entity.miaosha.MiaoShaOrder;
import com.travel.order.providers.logic.MiaoShaLogic;
import com.travel.order.providers.mapper.MiaoShaOrderDao;
import com.travel.order.providers.utils.ProductSoutOutMap;
import com.travel.order.providers.utils.RandomValidateCodeService;
import com.travel.order.providers.utils.ValidMSTime;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.MiaoShaUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author
 */
@Service
@Slf4j
@DubboService(timeout = 9000000, cluster = "failfast")
public class MiaoShaServiceImpl implements MiaoshaService {


    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoShaOrderDao miaoShaOrderDao;

    @Autowired
    RedisServiceImpl redisClient;

    @Autowired
    private MiaoShaLogic mSLogic;

    @Autowired
    private ZkApi zooKeeper;

    @Autowired
    MQSender sender;

    @Autowired
    private RandomValidateCodeService randomValidateCodeService;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @PostConstruct
    public void init() throws Exception {
        ResultGeekQ<List<GoodsVo>> goodsListR = goodsService.goodsVoList();
        if (!ResultGeekQ.isSuccess(goodsListR)) {
            log.error("***系统初始化商品预热失败***");
            return;
        }

        List<GoodsVo> goodsList = goodsListR.getData();
        for (GoodsVo goods : goodsList) {
            log.info("product {}", goods);
            redisClient.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultGeekQ<OrderInfoVo> miaosha(MiaoShaUserVo user, GoodsVo goods) {

        ResultGeekQ<OrderInfoVo> resultGeekQ = ResultGeekQ.build();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // explicitly setting the transaction name is something that can only be done programmatically
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        // 手动回滚
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //减库存 下订单 写入秒杀订单
            ResultGeekQ<Boolean> result = goodsService.reduceStock(goods);
            if(!ResultGeekQ.isSuccess(result)){
                resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_REDUCE_FAIL);
                return resultGeekQ;
            }
            MiaoShaUser Muser =  new MiaoShaUser();
            BeanUtils.copyProperties(user,Muser);
            OrderInfo orderInfo = mSLogic.createOrder(Muser, goods);
            OrderInfoVo orderInfoVo = new OrderInfoVo();
            BeanUtils.copyProperties(orderInfo,orderInfoVo);
            resultGeekQ.setData(orderInfoVo);
            return resultGeekQ;
        }catch(Exception e){
            log.error("***秒杀下订单失败，回滚事务*** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            transactionManager.rollback(status);
            return resultGeekQ;
        }
    }

    @Override
    public ResultGeekQ<Integer> insertMiaoshaOrder(MiaoShaOrderVo vo) {
        ResultGeekQ<Integer> resultGeekQ = ResultGeekQ.build();
        try{
            MiaoShaOrder mOrder =  new MiaoShaOrder();
            BeanUtils.copyProperties(vo,mOrder);
            int result = mSLogic.insertMiaoshaOrder(mOrder);
            if(result<=0){
                log.error("***插入订单失败insertMiaoshaOrder*** error:{}", JSON.toJSON(vo));
                resultGeekQ.withErrorCodeAndMessage(ResultStatus.FAILD);
                return resultGeekQ;
            }
            resultGeekQ.setData(result);
            return resultGeekQ;
        }catch(Exception e){
            log.error("***秒杀下订单失败*** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            return resultGeekQ;
        }
    }

    @Override
    public ResultGeekQ<MiaoShaOrderVo> getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId) {

        ResultGeekQ<MiaoShaOrderVo> resultGeekQ = ResultGeekQ.build();
        try{
            MiaoShaOrderVo mOrder =  new MiaoShaOrderVo();
            MiaoShaOrder mSorder = mSLogic.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
            BeanUtils.copyProperties(mSorder,mOrder);
            resultGeekQ.setData(mOrder);
            return resultGeekQ;
        }catch(Exception e){
            log.error("***查询失败getMiaoshaOrderByUserIdGoodsId *** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            return resultGeekQ;
        }
    }

    @Override
    public ResultGeekQ<String> createMiaoshaPath(MiaoShaUserVo user, Long goodsId) {
        ResultGeekQ<String> resultGeekQ = ResultGeekQ.build();
        try{
            if (user == null || goodsId <= 0) {
                return null;
            }
            String str = MD5Util.md5(UUIDUtil.getUUid() + "123456");
            log.info("createMiaoShaPath str:{}", str);
            redisClient.set(MiaoshaKey.getMiaoshaPath, "" + user.getNickname() + "_" + goodsId, str);
            resultGeekQ.setData(str);
            return resultGeekQ;
        }catch(Exception e){
            log.error("***查询失败getMiaoshaOrderByUserIdGoodsId *** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            return resultGeekQ;
        }
    }


    @Override
    public ResultGeekQ<Long> getMiaoshaResult(Long userId, Long goodsId) {
        ResultGeekQ<Long> resultGeekQ = ResultGeekQ.build();
        try{
            MiaoShaOrder order = mSLogic.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
            //秒杀成功
            if (order != null) {
                log.info("***getMiaoshaResult返回结果成功***");
                resultGeekQ.setData(order.getId());
                return resultGeekQ;
            } else {
                //此商品的秒杀已经结束，但是可能订单还在生成中
                //获取所有的秒杀订单, 判断订单数量和参与秒杀的商品数量
                List<MiaoShaOrder> orders = mSLogic.listByGoodsId(goodsId);
                if (orders == null) {
                    //订单还在生成中
                    resultGeekQ.setData(Long.valueOf(CustomerConstant.MS_ING));
                    return  resultGeekQ;
                } else {
                    //判断是否有此用户的订单
                    MiaoShaOrder orderIsGet = get(orders, userId);
                    //如果有，则说明秒杀成功
                    if (orderIsGet != null) {
                        resultGeekQ.setData( orderIsGet.getOrderId() );
                        return resultGeekQ;
                    } else {
                        //秒杀失败
                        resultGeekQ.setData(Long.valueOf(CustomerConstant.MS_F));
                        return resultGeekQ;
                    }
                }
            }
        }catch(Exception e){
            log.error("***getMiaoshaResult *** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_RESULT_FAIL);
            return resultGeekQ;
        }
    }

    @Override
    public ResultGeekQ<Boolean> checkPath(MiaoShaUserVo user, long goodsId, String path) {
        ResultGeekQ<Boolean> resultGeekQ = ResultGeekQ.build();
        try{
            MiaoShaUser mSuser = new MiaoShaUser();
            BeanUtils.copyProperties(user,mSuser);
            Boolean  checkPathR= mSLogic.checkPath(mSuser, goodsId, path);
            resultGeekQ.setData(checkPathR);
            return resultGeekQ;
        }catch(Exception e){
            log.error("***查询失败 checkPath *** error:{}",e);
            resultGeekQ.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            return resultGeekQ;
        }
    }

    @Override
    public String getRandcode(MiaoShaUser user, long goodsId) {
        return randomValidateCodeService.getRandcode(user, goodsId);
    }

    @Override
    public boolean checkVerifyCode(MiaoShaUserVo user, long goodsId, String verifyCode) {
        return randomValidateCodeService.checkVerifyCode(user, goodsId, verifyCode);
    }

    @Override
    public ResultGeekQ<Integer> miaoshaComfirm(MiaoShaUser user, long goodsId) {
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        try {
            //zk 内存标记 相比用redis里的库存来判断减少了与redis的交互次数
            if (ProductSoutOutMap.productSoldOutMap.get(goodsId) != null) {
                result.withError(ResultStatus.MIAOSHA_LOCAL_GOODS_NO.getCode(), ResultStatus.MIAOSHA_LOCAL_GOODS_NO.getMessage());
                return result;
            }
            //*********************getMiaoshaPath***********设置排队标记，超时时间根据业务情况决定，类似分布式锁 返回排队中   ************************
            String redisK =  CommonMethod.getMiaoshaOrderWaitFlagRedisKey(String.valueOf(user.getId()), String.valueOf(goodsId));
            if (!redisClient.set(redisK,String.valueOf(goodsId), "NX", "EX", 5)) {
                result.withError(ResultStatus.MIAOSHA_QUEUE_ING.getCode(), ResultStatus.MIAOSHA_QUEUE_ING.getMessage());
                return result;
            }

            //校验时间 防止刷时间
            ResultGeekQ<GoodsVo> goodR = goodsService.goodsVoByGoodId(Long.valueOf(goodsId));
            if (!ResultGeekQ.isSuccess(goodR)) {
                result.withError(ResultStatus.PRODUCT_NOT_EXIST.getCode(), ResultStatus.PRODUCT_NOT_EXIST.getMessage());
                return result;
            }
            ResultGeekQ validR = ValidMSTime.validMiaoshaTime(goodR.getData());
            if (!ResultGeekQ.isSuccess(validR)) {
                result.withError(validR.getCode(), validR.getMessage());
                return result;
            }

            //是否已经秒杀到
            ResultGeekQ<MiaoShaOrderVo> order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
            if (!ResultGeekQ.isSuccess(order)) {
                result.withError(ResultStatus.REPEATE_MIAOSHA.getCode(), ResultStatus.REPEATE_MIAOSHA.getMessage());
                return result;
            }

            //扣减库存 +  ZK 内存级别标识
            ResultGeekQ<Boolean> deductR = deductStockCache(goodsId+"");
            if(!ResultGeekQ.isSuccess(deductR)){
                result.withError(deductR.getCode(), deductR.getMessage());
                return result;
            }

            //入队
            MiaoShaMessage mm = new MiaoShaMessage();
            mm.setUser(user);
            mm.setGoodsId(goodsId);
            sender.sendMiaoshaMessage(mm);
            result.setData(CustomerConstant.MS_ING);
            return result;
        } catch (AmqpException amqpE){
            log.error("创建订单失败", amqpE);
            String goodsIdZ =  String.valueOf(goodsId);
            redisClient.incr(GoodsKey.getMiaoshaGoodsStock, "" + goodsIdZ);
            ProductSoutOutMap.productSoldOutMap.remove(goodsIdZ);
            //修改zk的商品售完标记为false
            if (zooKeeper.exists(CustomerConstant.ZookeeperPathPrefix.getZKSoldOutProductPath(goodsIdZ), true) != null) {
                zooKeeper.updateNode(CustomerConstant.ZookeeperPathPrefix.getZKSoldOutProductPath(goodsIdZ), "false");
            }
            result.withErrorCodeAndMessage(ResultStatus.MIAOSHA_MQ_SEND_FAIL);
            return result ;
        }catch (Exception e) {
            result.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            return result;
        }
    }

    @Override
    public ResultGeekQ<Long> miaoshaResult(MiaoShaUser user, long goodsId) {
        ResultGeekQ result = ResultGeekQ.build();
        try {
            String redisK =  CommonMethod.getMiaoshaOrderWaitFlagRedisKey(String.valueOf(user.getId()), String.valueOf(goodsId));
            //判断redis里的排队标记，排队标记不为空返回还在排队中
            //一定要先判断排队标记再判断是否已生成订单，不然又会存在并发的时间差问题
            if (redisClient.get(redisK+ goodsId,String.class) != null) {
                result.withError(ResultStatus.MIAOSHA_QUEUE_ING.getCode(),ResultStatus.MIAOSHA_QUEUE_ING.getMessage());
                return result;
            }
            String redisMr = CommonMethod.getMiaoshaOrderRedisKey(String.valueOf(user.getId()), String.valueOf(goodsId));
            //查询用户秒杀商品订单是否创建成功
            Object order = redisClient.get(redisMr, OrderInfo.class);
            //秒杀成功
            if(order != null) {
                OrderInfo info = (OrderInfo)order;
                result.setData(info.getId());
                return result;
            }
            result.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            return result;
        } catch (Exception e) {
            result.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            return result;
        }
    }

    private MiaoShaOrder get(List<MiaoShaOrder> orders, Long userId) {
        if (orders == null || orders.size() <= 0) {
            return null;
        }
        for (MiaoShaOrder order : orders) {
            if (order.getUserId().equals(userId)) {
                return order;
            }
        }
        return null;
    }

    public ResultGeekQ<Boolean> deductStockCache(String goodsId) {

        ResultGeekQ<Boolean> resultGeekQ = ResultGeekQ.build();
        try {
            Long stock = redisClient.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
            if (stock == null) {
                log.error("***数据还未准备好***");
                resultGeekQ.withError(ResultStatus.MIAOSHA_DEDUCT_FAIL.getCode(), ResultStatus.MIAOSHA_DEDUCT_FAIL.getMessage());
                return resultGeekQ;
            }
            if (stock < 0) {
                log.info("***stock 扣减减少*** stock:{}",stock);
                redisClient.incr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
                ProductSoutOutMap.productSoldOutMap.put(goodsId, true);
                //写zk的商品售完标记true
                if (zooKeeper.exists(CustomerConstant.ZookeeperPathPrefix.PRODUCT_SOLD_OUT, false) == null) {
                    zooKeeper.createNode(CustomerConstant.ZookeeperPathPrefix.PRODUCT_SOLD_OUT,"");
                }

                if (zooKeeper.exists(CustomerConstant.ZookeeperPathPrefix.getZKSoldOutProductPath(goodsId), true) == null) {
                    zooKeeper.createNode(CustomerConstant.ZookeeperPathPrefix.getZKSoldOutProductPath(goodsId), "true");
                }
                if ("false".equals(new String(zooKeeper.getData(CustomerConstant.
                        ZookeeperPathPrefix.getZKSoldOutProductPath(goodsId), new WatcherApi())))) {
                    zooKeeper.updateNode(CustomerConstant.ZookeeperPathPrefix.getZKSoldOutProductPath(goodsId), "true");
                    //监听zk售完标记节点
                    zooKeeper.exists(CustomerConstant.ZookeeperPathPrefix.getZKSoldOutProductPath(goodsId), true);
                }
                resultGeekQ.withError(ResultStatus.MIAO_SHA_OVER.getCode(), ResultStatus.MIAO_SHA_OVER.getMessage());
                return resultGeekQ;
            }
        } catch (Exception e) {
            log.error("***deductStockCache error***");
            resultGeekQ.withError(ResultStatus.MIAO_SHA_OVER.getCode(), ResultStatus.MIAO_SHA_OVER.getMessage());
            return resultGeekQ;
        }
        return resultGeekQ;
    }
}
