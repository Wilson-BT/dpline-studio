package com.dpline.k8s.operator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.*;

/**
 * application http visit configuration
 */
@Configuration
public class OperatorWebAppConfig implements WebMvcConfigurer {

    public static final String PATH_PATTERN = "/**";


    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 支持所有域名访问
//        config.addAllowedOrigin("*");
        config.addAllowedOriginPattern("*");
        // 允许任何header访问
        config.addAllowedMethod("*");
        // 允许任何方法访问
        config.addAllowedHeader("*");
        //支持安全证书
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration(PATH_PATTERN, config);
        return new CorsFilter(configSource);
    }

    /**
     * 注册登录拦截器
     *
     * @return
     */
    @Bean
    public RequestHandlerInterceptor requestInterceptor() {
        return new RequestHandlerInterceptor();
    }

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor())
            .addPathPatterns("/task-watcher");
    }

}
