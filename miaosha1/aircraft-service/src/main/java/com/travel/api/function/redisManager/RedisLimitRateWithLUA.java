package com.travel.api.function.redisManager;

import org.apache.commons.io.FileUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author 邱润泽 bullock
 */
public class RedisLimitRateWithLUA {

    public static void main(String[] args) {
        final CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        latch.await();
                        System.out.println("请求是否被执行："+accquire());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

        latch.countDown();
    }

    public static boolean accquire() throws IOException, URISyntaxException {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(50);
        config.setMaxWaitMillis(3000);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        // 集群
        JedisShardInfo jedisShardInfo1 = new JedisShardInfo("81.69.254.72", 26379);
        jedisShardInfo1.setPassword("luozijing@2021");
        Jedis jedis = new Jedis(jedisShardInfo1);
        File luaFile = new File(RedisLimitRateWithLUA.class.getResource("/").toURI().getPath() + "limit.lua");
        String luaScript = FileUtils.readFileToString(luaFile);
        String key = "ip:" + System.currentTimeMillis()/1000; // 当前秒
        String limit = "5"; // 最大限制
        String limitTime = "1";
        List<String> keys = new ArrayList<String>();
        keys.add(key);
        List<String> args = new ArrayList<String>();
        args.add(limit);
        args.add(limitTime);
        System.out.println(key);
        Long result = (Long)(jedis.eval(luaScript, keys, args)); // 执行lua脚本，传入参数
        return result == 1;
    }
}
