package com.dpline.datasource.api.client;

import com.dpline.common.enums.DataSourceType;
import com.dpline.datasource.api.utils.JDBCDataSourceUtil;
import com.dpline.datasource.api.common.CommonConnectionParam;
import com.google.common.base.Stopwatch;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * 默认通用客户端jdbc链接
 */
@Slf4j
public class CommonDataSourceClient implements DataSourceClient {

    private static final Logger logger = LoggerFactory.getLogger(CommonDataSourceClient.class);

    public static final String COMMON_VALIDATION_QUERY = "select 1";

    protected final CommonConnectionParam commonConnectionParam;
    protected HikariDataSource dataSource;
    protected JdbcTemplate jdbcTemplate;

    public CommonDataSourceClient(CommonConnectionParam commonConnectionParam, DataSourceType dataSourceType) {
        this.commonConnectionParam = commonConnectionParam;
        this.commonConnectionParam.setValidationQuery(COMMON_VALIDATION_QUERY);
        initClient(commonConnectionParam, dataSourceType);
    }


    private void initClient(CommonConnectionParam commonConnectionParam, DataSourceType dataSourceType) {
        this.dataSource = JDBCDataSourceUtil.createJdbcDataSource(commonConnectionParam, dataSourceType);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public String getName() {
        return DataSourceType.MYSQL.getName();
    }

    @Override
    public Boolean checkClient() {
        Boolean flag = false;
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            this.jdbcTemplate.execute(this.commonConnectionParam.getValidationQuery());
            flag = true;
        } catch (Exception e) {
            throw new RuntimeException("JDBC 连接失败", e);
        } finally {
            logger.info("测试sql {} 用时 {} ms ", this.commonConnectionParam.getValidationQuery(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
        return flag;
    }

    @Override
    public void close() {

        logger.info("关闭数据连接 {}.", commonConnectionParam.getDatabase());
        try (HikariDataSource closedDatasource = dataSource) {
        }
        this.jdbcTemplate = null;
    }

    @Override
    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("获取连接异常: {}", e.getMessage(), e);
            return null;
        }
    }
}
