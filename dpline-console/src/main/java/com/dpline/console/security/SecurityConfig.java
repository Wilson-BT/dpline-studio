package com.dpline.console.security;

import com.dpline.console.security.impl.ldap.LdapAuthenticator;
import com.dpline.console.security.impl.pwd.PasswordAuthenticator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${security.authentication.type:PASSWORD}")
    private String type;

    private AutowireCapableBeanFactory beanFactory;

    private AuthenticationType authenticationType;

    /**
     * 构造器自动注入AutowireCapableBeanFactory实体类
     * Java变量的初始化顺序为：静态变量或静态语句块 –> 实例变量或初始化语句块 –> 构造方法 –> @Autowired
     * @param beanFactory
     */
    @Autowired
    public SecurityConfig(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    private void setAuthenticationType(String type) {
        if (StringUtils.isBlank(type)) {
            logger.info("security.authentication.type configuration is empty, the default value 'PASSWORD'");
            this.authenticationType = AuthenticationType.PASSWORD;
            return;
        }

        this.authenticationType = AuthenticationType.valueOf(type);
    }

    @Bean(name = "authenticator")
    public Authenticator authenticator() {
        setAuthenticationType(type);
        Authenticator authenticator;
        switch (authenticationType) {
            case PASSWORD:
                authenticator = new PasswordAuthenticator();
                break;
            case LDAP:
                authenticator = new LdapAuthenticator();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + authenticationType);
        }
        beanFactory.autowireBean(authenticator);
        return authenticator;
    }
}
