package com.dpline.datasource.api.utils;

import cn.hutool.core.util.StrUtil;
import com.dpline.common.DataSourceConstants;
import com.dpline.common.enums.DataSourceType;
import com.dpline.common.util.PropertyUtils;
import com.dpline.datasource.api.common.CommonConnectionParam;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Driver;

public class JDBCDataSourceUtil {

    private static final Logger logger = LoggerFactory.getLogger(JDBCDataSourceUtil.class);

    public static HikariDataSource createJdbcDataSource(CommonConnectionParam properties, DataSourceType dataSourceType) {
        logger.info("创建Hikari数据库连接池maxActive:{}",
                PropertyUtils.getInt(DataSourceConstants.SPRING_DATASOURCE_MAX_ACTIVE, 50));
        HikariDataSource dataSource = new HikariDataSource();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (StrUtil.isNotBlank(properties.getDataSourceUrl()) && !properties.getDataSourceUrl().startsWith("jdbc:mysql")) {
            properties.setDataSourceUrl("jdbc:mysql://" + properties.getDataSourceUrl());
        }
        properties.setDriverClassName(DataSourceConstants.COM_MYSQL_CJ_JDBC_DRIVER);
        loaderJdbcDriver(classLoader, properties, dataSourceType);

        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setJdbcUrl(properties.getDataSourceUrl());
        dataSource.setUsername(properties.getUserName());
        dataSource.setPassword(properties.getPassword());

        dataSource.setMinimumIdle(PropertyUtils.getInt(DataSourceConstants.SPRING_DATASOURCE_MIN_IDLE, 5));
        dataSource.setMaximumPoolSize(PropertyUtils.getInt(DataSourceConstants.SPRING_DATASOURCE_MAX_ACTIVE, 50));
        dataSource.setConnectionTestQuery(properties.getValidationQuery());

        if (properties.getProps() != null) {
            properties.getProps().forEach(dataSource::addDataSourceProperty);
        }

        logger.info("Creating HikariDataSource pool success.");
        return dataSource;
    }

    /**
     * @return One Session Jdbc DataSource
     */
    public static HikariDataSource createOneSessionJdbcDataSource(CommonConnectionParam properties, DataSourceType dbType) {
        logger.info("Creating OneSession HikariDataSource pool for maxActive:{}",
                PropertyUtils.getInt(DataSourceConstants.SPRING_DATASOURCE_MAX_ACTIVE, 50));

        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setJdbcUrl(properties.getDataSourceUrl());
        dataSource.setUsername(properties.getUserName());
        dataSource.setPassword(properties.getPassword());

        Boolean isOneSession = false;
        dataSource.setMinimumIdle(
                isOneSession ? 1 : PropertyUtils.getInt(DataSourceConstants.SPRING_DATASOURCE_MIN_IDLE, 5));
        dataSource.setMaximumPoolSize(
                isOneSession ? 1 : PropertyUtils.getInt(DataSourceConstants.SPRING_DATASOURCE_MAX_ACTIVE, 50));
        dataSource.setConnectionTestQuery(properties.getValidationQuery());

        if (properties.getProps() != null) {
            properties.getProps().forEach(dataSource::addDataSourceProperty);
        }

        logger.info("Creating OneSession HikariDataSource pool success.");
        return dataSource;
    }

    protected static void loaderJdbcDriver(ClassLoader classLoader, CommonConnectionParam properties, DataSourceType dataSourceType) {
        String driverStr = properties.getDriverClassName();
        if (StrUtil.isBlank(driverStr)) {
            throw new IllegalArgumentException("driver缺少必要信息");
        }
        try {
            final Class<?> clazz = Class.forName(driverStr, true, classLoader);
            final Driver driver = (Driver) clazz.newInstance();
            if (!driver.acceptsURL(properties.getDataSourceUrl())) {
                logger.warn("Jdbc driver装载失败. Driver {}.", driverStr);
                throw new RuntimeException("Jdbc driver装载失败.");
            }
            if (dataSourceType.equals(dataSourceType.MYSQL)) {
                if (driver.getMajorVersion() >= 8) {
                    properties.setDriverClassName(driverStr);
                } else {
                    properties.setDriverClassName(DataSourceConstants.COM_MYSQL_JDBC_DRIVER);
                }
            }
        } catch (final Exception e) {
            logger.warn("装载jdbc驱动失败.");
        }
    }
}
