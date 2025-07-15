package com.hmall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * 优惠券服务启动类
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.hmall.api.client")
@MapperScan("com.hmall.coupon.mapper")
@EnableScheduling
public class CouponApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CouponApplication.class, args);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

