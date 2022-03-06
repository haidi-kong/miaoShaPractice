package com.travel.users.providers;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mengyun.tcctransaction.spring.annotation.EnableTccTransaction;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import org.mengyun.tcctransaction.spring.annotation.EnableTccTransaction;

@SpringBootApplication
@EnableTccTransaction
@EnableDubbo(scanBasePackages = "com.travel.users.providers.serviceImp")
@MapperScan("com.travel.users.providers.mapper")
public class UsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, args);
    }

}
