package com.travel.users.providers.config.tccTransactionConfig;

import org.mengyun.tcctransaction.repository.RedisTransactionRepository;
import org.mengyun.tcctransaction.repository.TransactionRepository;
import org.mengyun.tcctransaction.serializer.KryoTransactionSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class TccTransactionConfig {
    @Bean("transactionRepository")
    public TransactionRepository memoryStoreTransactionRepository2(@Autowired JedisPool jedisPool) {
        RedisTransactionRepository repository = new RedisTransactionRepository();
        repository.setDomain("TCC:DUBBO:USER:");
        repository.setSerializer(new KryoTransactionSerializer());
        repository.setJedisPool(jedisPool);
        return repository;
    }
}
