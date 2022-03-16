package com.travel.order.providers;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
//import org.mengyun.tcctransaction.spring.annotation.EnableTccTransaction;
import org.mengyun.tcctransaction.spring.annotation.EnableTccTransaction;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableTccTransaction
@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.travel.order.providers.serviceImp")
@MapperScan("com.travel.order.providers.mapper")
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
