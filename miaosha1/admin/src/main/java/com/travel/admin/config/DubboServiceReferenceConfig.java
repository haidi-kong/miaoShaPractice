package com.travel.admin.config;

import com.travel.order.apis.service.GoodsService;
import com.travel.order.apis.service.MiaoshaService;
import com.travel.order.apis.service.OrderService;
import com.travel.users.apis.service.MiaoShaUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboServiceReferenceConfig {

    @DubboReference(check = false)
    private MiaoshaService miaoshaService;

    @DubboReference(check = false)
    private MiaoShaUserService miaoShaUserService;

    @Bean
    public MiaoShaUserService miaoShaUserService(){
        return miaoShaUserService;
    }

    @Bean
    public MiaoshaService miaoshaService(){
        return miaoshaService;
    }
}
