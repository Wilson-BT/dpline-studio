package com.dpline.console.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.dpline.console.interceptor.CustomIdGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@MapperScan(value = {"com.dpline.dao.mapper"})
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean
    public IdentifierGenerator customIdGenerator(){
        return new CustomIdGenerator();
    }


}
