package com.dpline.console.config;

import com.dpline.common.util.StringUtils;
import com.dpline.common.util.TaskPathResolver;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.dpline.console.interceptor.LoginHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * application http visit configuration
 */
@Configuration
public class ApiWebAppConfig implements WebMvcConfigurer {

    public static final String LOGIN_INTERCEPTOR_PATH_PATTERN = "/**/*";

    //用户登录时和用户注册不拦截
    public static final String LOGIN_PATH_PATTERN = "/auth/login";

    public static final String WEBSOCKET_PATH_PATTERN = "/websocket/**";

    public static final String PATH_PATTERN = "/**";
    public static final String LOCALE_LANGUAGE_COOKIE = "language";


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


    @Bean
    public LoginHandlerInterceptor loginInterceptor() {
        return new LoginHandlerInterceptor();
    }

//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        factory.setMaxFileSize(DataSize.ofMegabytes(100));
//        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
//        return factory.createMultipartConfig();
//    }

    /**
     * Cookie
     * @return local resolver
     */
    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieName(LOCALE_LANGUAGE_COOKIE);
        // set default locale
        localeResolver.setDefaultLocale(Locale.CHINESE);
        // set language tag compliant
        localeResolver.setLanguageTagCompliant(false);
        return localeResolver;
    }

    /**
     * jackson到 http消息转化，主要为日期转换和long类型转换
     *
     * @return
     */
    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();

        // 日期格式转换
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // Long类型转String类型
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        mapper.registerModule(simpleModule);
        converter.setObjectMapper(mapper);
        return converter;
    }
//    暂时不支持国际化
//    @Bean
//    public LocaleChangeInterceptor localeChangeInterceptor() {
//        return new LocaleChangeInterceptor();
//    }

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns(LOGIN_INTERCEPTOR_PATH_PATTERN)
                .excludePathPatterns(LOGIN_PATH_PATTERN,
                    WEBSOCKET_PATH_PATTERN,
                        "/swagger-resources/**", "/webjars/**", "/v2/**","/web/**",
                        "/doc.html", "/swagger-ui.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String appHomePath = TaskPathResolver.getAppHomePath();
        if (StringUtils.isEmpty(appHomePath)) {
            throw new NullPointerException("The variable of APP_HOME is not exist.");
        }
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        // web 路径下请求的所有资源都在外部web路径下寻找
        registry.addResourceHandler("/web/**").addResourceLocations(String.format("file:%s/web/",appHomePath));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 1. /dpline/ 路径重定向到 /dpline/web
        registry.addViewController("/").setViewName("redirect:/web/");
        registry.addViewController("/web").setViewName("redirect:/web/");
        // 2. /dpline/web 重定向到 /dpline/web/index.html
        registry.addViewController("/web/").setViewName("forward:/web/index.html");
    }

    /**
     * Turn off suffix-based content negotiation
     *
     * @param configurer configurer
     */
    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        converters.add(jackson2HttpMessageConverter());
    }

    /**
     * 添加枚举类参数的转换工厂
     *
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new IdCodeToEnumConverterFactory());
    }

}
