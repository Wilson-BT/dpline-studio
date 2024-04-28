package com.dpline.flink.explain;

import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.enums.SqlExplainType;
import com.dpline.common.request.FlinkSqlExplainResponse;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.flink.core.context.*;
import com.dpline.flink.local.SessionContext;
import com.dpline.common.params.SqlResult;
import com.dpline.common.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;
import org.apache.flink.table.api.config.ExecutionConfigOptions;
import org.apache.flink.table.api.config.OptimizerConfigOptions;
import org.apache.flink.table.api.config.TableConfigOptions;
import org.apache.flink.table.api.internal.TableEnvironmentInternal;
import org.apache.flink.table.operations.*;
import org.apache.flink.table.operations.command.SetOperation;
import org.apache.flink.table.operations.ddl.*;

import java.lang.reflect.Field;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FlinkSqlInterpreter implements SqlInterpreter {

    private static Logger logger = LoggerFactory.getLogger(FlinkSqlInterpreter.class);

    /**
     * 会话同 解释器 绑定，一个解释器 对应 一个会话
     * 会话同 执行器 绑定，一个会话 对应 多个 执行器
     */
    private final SessionStudio sessionStudio;

    private final Map<String,? extends ConfigOption> tableConfigOptions;

    private TableEnvironmentInternal tableEnv;


    public FlinkSqlInterpreter(SessionStudio sessionStudio) {
        this.tableConfigOptions = extractTableConfigOptions();
        // open session
        this.sessionStudio = sessionStudio;
        // init tableEnv
        this.tableEnv = initCreateTableEnv();
    }

    /**
     * verify multi sql,session Id is not null
     * @return
     */
    public SqlResult explainSql(List<String> sqlState) {
        // sql is empty
        SqlResult.SqlResultBuilder builder = SqlResult.builder();
        if (CollectionUtils.isEmpty(sqlState)) {
            return builder.sqlExplainType(SqlExplainType.VERIFY_FAILED)
                            .message(SqlExplainType.VERIFY_FAILED.message)
                            .sql(sqlState.toString())
                            .build();
        }
        SqlOperationGroup sqlOperationGroup = new SqlOperationGroup();
        // 同一段file 里面不能有多个 select 语句， 也不能存在 多个
        for (String sql : sqlState) {
            try {
                parseSql(sql,sqlOperationGroup);
                // 如果 开始是 BeginStatementSetOperation 下面应该要是 多个 CatalogSinkModifyOperation 语句 + EndStatementSetOperation
            } catch (Exception exception) {
                return builder.sqlExplainType(SqlExplainType.SYNTAX_ERROR)
                        .sql(sql)
                        .build();
            }
        }
        return builder.sqlExplainType(SqlExplainType.SQL_RIGHT)
            .message(SqlExplainType.SQL_RIGHT.message)
            .streamGraphJson(getStreamGraphFromModifyOperation(sqlOperationGroup.getModifyOperations()))
            .build();
    }

    public FlinkSqlExplainResponse explain(List<String> sqlState) {

        SqlResult sqlResult = explainSql(sqlState);

        return FlinkSqlExplainResponse.builder()
            .sqlResult(sqlResult)
            .responseStatus(SqlExplainType.SQL_RIGHT.equals(sqlResult.getSqlExplainType()) ? ResponseStatus.SUCCESS: ResponseStatus.FAIL)
            .msg(sqlResult.getMessage())
            .build();
    }


    /**
     *
     * 暂时不支持 Reset 、AddJar 等
     * explain all, but not exec all
     * @param sql
     * @param sqlOperationGroup
     * @throws Exception
     */
    private void parseSql(String sql,SqlOperationGroup sqlOperationGroup) throws Exception {
        // 解析语法, 需要先创建 ddl ，如果有 select 和 insert 同时存在
        Operation operation = parseSingleSql(sql);
        // env change
        if (operation instanceof DropOperation
            || operation instanceof AlterOperation
            || operation instanceof UseOperation
        ) {
            this.tableEnv.executeInternal(operation);
            return;
        } else if(operation instanceof SetOperation) {
            setOperationExecute((SetOperation) operation);
        } else if(operation instanceof CreateOperation){
            if(!(operation instanceof CreateTableASOperation)){
                this.tableEnv.executeInternal(operation);
                return;
            }
            // ctas , should use it`s ddl and insert
            CreateTableASOperation ctasOperation = (CreateTableASOperation) operation;
            this.tableEnv.executeInternal(ctasOperation.getCreateTableOperation());
            // explain insert
            this.tableEnv.explainInternal(Collections.singletonList(ctasOperation.getInsertOperation()));
            sqlOperationGroup.addModifyOperation(ctasOperation.getInsertOperation());
            sqlOperationGroup.addCatalogSinkModifyOperation(ctasOperation.getInsertOperation());
            return;
        } else if(
            operation instanceof QueryOperation ||
            operation instanceof ExplainOperation
        ){
            // other execute
            this.tableEnv.explainInternal(Collections.singletonList(operation));
            sqlOperationGroup.addUnExecOperation(operation);
            return;
        } else if(operation instanceof ModifyOperation){
            // insert 、select 、update 、delete
            // collect ModifyOperation
            this.tableEnv.explainInternal(Collections.singletonList(operation));
            // only ModifyOperation can
            sqlOperationGroup.addModifyOperation((ModifyOperation) operation);
            if(operation instanceof CatalogSinkModifyOperation){
                sqlOperationGroup.addCatalogSinkModifyOperation((CatalogSinkModifyOperation)  operation);
            }
            return;
        }
        sqlOperationGroup.addUnExecOperation(operation);
        throw new TableException("Unsupported SQL query! only accepts a single SQL query.");
    }


    /**
     *
     * @param sqlState
     * @return
     */
    public Operation parseSingleSql(String sqlState) throws Exception {
        List<Operation> operations = this.tableEnv.getParser().parse(sqlState);
        if(operations.size() != 1){
            throw new TableException(
                "Unsupported SQL query! explainSql() only accepts a single SQL query.");
        }
        return operations.get(0);
    }

    /**
     * parse sql by parser
     *
     * @param sqlState
     * @return List of Operation
     */
    public List<Operation> parseSqlToOperation(String sqlState) throws Exception {
        List<Operation> operations;
        try {
            operations = this.tableEnv.getParser().parse(sqlState);

        } catch (Throwable e) {
            throw new Exception("Invalidate SQL statement.", e);
        }
        if (operations.size() != 1) {
            throw new Exception("Only single statement is supported now.");
        }
        return operations;
    }

    /**
     * 获取执行计划
     *
     * @return
     * @throws Exception
     */
    public String getStreamGraphFromModifyOperation(List<ModifyOperation> modifyOperations) {
        try {
            if (CollectionUtils.isEmpty(modifyOperations)) {
                return "";
            }
            StreamTableEnvironmentImpl streamTabEnv = (StreamTableEnvironmentImpl) this.tableEnv;
            List<Transformation<?>> transformations = streamTabEnv
                .getPlanner()
                .translate(modifyOperations);
            StreamGraph streamGraph = streamTabEnv
                .execEnv()
                .generateStreamGraph(transformations);
            return streamGraph.getStreamingPlanAsJSON();
        } catch (Exception ex) {
            logger.error("GetStreamGraph error: {}", ExceptionUtil.exceptionToString(ex));
        }
        return "";
    }


    /**
     * sql 上线校验机制
     * get exe StreamGraph by ModifyOperation
     * need execute sql ,not support show、explain and describe operation
     *
     * @return
     */
    public SqlResult reCheckSql(List<String> sqlState) throws Exception {
        SqlResult.SqlResultBuilder builder = SqlResult.builder();
        if (CollectionUtils.isEmpty(sqlState)) {
            return builder.sqlExplainType(SqlExplainType.VERIFY_FAILED)
                .message(SqlExplainType.VERIFY_FAILED.message)
                .sql(sqlState.toString())
                .build();
        }
        SqlOperationGroup sqlOperationGroup = new SqlOperationGroup();
        for (String statement : sqlState) {
            try {
                parseSql(statement,sqlOperationGroup);
            } catch (Exception exception) {
                return builder.sqlExplainType(SqlExplainType.SYNTAX_ERROR)
                    .message(ExceptionUtil.exceptionToString(exception))
                    .sql(statement)
                    .build();
            }
            if(CollectionUtils.isNotEmpty(sqlOperationGroup.getUnExecOperations())){
                return builder.sqlExplainType(SqlExplainType.UNSUPPORTED_SQL)
                    .message(SqlExplainType.UNSUPPORTED_SQL.message)
                    .sql(statement)
                    .build();
            }
        }
        return builder.sqlExplainType(SqlExplainType.SQL_RIGHT)
            .message(SqlExplainType.SQL_RIGHT.message)
            .build();
    }

    public FlinkSqlExplainResponse reCheck(List<String> sqlState) throws Exception {
        SqlResult sqlResult = reCheckSql(sqlState);
        return FlinkSqlExplainResponse.builder()
            .sqlResult(sqlResult)
            .responseStatus(SqlExplainType.SQL_RIGHT.equals(sqlResult.getSqlExplainType()) ? ResponseStatus.SUCCESS: ResponseStatus.FAIL)
            .msg(sqlResult.getMessage())
            .build();
    }


    private void setOperationExecute(SetOperation setOperation){
        // 只要有一个为空，则不执行
        if(!setOperation.getKey().isPresent()){
            return;
        }
        if(!setOperation.getValue().isPresent()){
            return;
        }
        if(this.tableConfigOptions.containsKey(setOperation.getKey().get())){
            logger.info("Execute Set key : [{}], value : [{}]",setOperation.getKey().get(),setOperation.getValue().get());
            if (TableConfigOptions.TABLE_SQL_DIALECT.key().equalsIgnoreCase(setOperation.getKey().get())) {
                // remove
                tableEnv.getConfig().setSqlDialect(SqlDialect.valueOf(setOperation.getValue().get().toUpperCase()));
            } else {
                tableEnv.getConfig()
                        .getConfiguration()
                        .setString(setOperation.getKey().get(),
                            setOperation.getValue().get());
            }
        } else {
            logger.warn("UnSupport set key : [{}]",setOperation.getKey().get());
        }
    }

    /**
     * run sql on cluster
     * @param sqls sql list
     * @throws Exception
     */
    @Override
    public String runSqlListOnCluster(List<String> sqls) throws Exception {
        StatementSet statementSet = tableEnv.createStatementSet();
        for (String statement : sqls) {
            List<Operation> operations = parseSqlToOperation(statement);
            Operation operation = operations.get(0);
            if (operation instanceof CatalogSinkModifyOperation) {
                statementSet.addInsertSql(statement);
            } else if (operation instanceof QueryOperation) {
                throw new RuntimeException("UnSupport select DML");
            } else if (operation instanceof ShowOperation || operation instanceof DescribeTableOperation
                    || operation instanceof ExplainOperation) {
                logger.warn("UnSupport [show]/[desc]/[explain] sql");
            } else if (operation instanceof SetOperation) {
                // if set，key、value
                Optional<String> key = ((SetOperation) operation).getKey();
                Optional<String> value = ((SetOperation) operation).getValue();
                key.ifPresent(x->{
                    if (TableConfigOptions.TABLE_SQL_DIALECT.key().equalsIgnoreCase(key.get())) {
                        // remove
                        tableEnv.getConfig().setSqlDialect(SqlDialect.valueOf(key.get()));
                    } else {
                        value.ifPresent(v -> tableEnv.getConfig()
                                .getConfiguration()
                                .setString(key.get(),value.get()));
                    }
                });
            } else {
                execute(operation);
                logger.info("Execute sql : [{}]", statement);
            }
        }
        TableResult execute = statementSet.execute();
        Optional<JobClient> jobClient = execute.getJobClient();
        if (jobClient.isPresent()) {
            logger.info("JobId: {}. run status: {}", jobClient.get().getJobID(), jobClient.get().getJobStatus());
            return jobClient.get().getJobID().toString();
        }
        return StringUtils.EMPTY;
    }


    /**
     * create and alter table / database
     *
     * // CREATE TABLE,
     * // DROP TABLE,
     * // ALTER TABLE,
     * // SHOW TABLES
     *
     * // CREATE DATABASE,
     * // DROP DATABASE,
     * // ALTER DATABASE,
     * // USE [CATALOG.]DATABASE,
     * // SHOW DATABASES
     *
     * // CREATE FUNCTION,
     * // DROP FUNCTION,
     * // ALTER FUNCTION,
     * // SHOW [USER] FUNCTIONS,
     * // SHOW PARTITIONS
     *
     * // CREATE CATALOG,
     * // DROP CATALOG,
     * // USE CATALOG,
     * // SHOW CATALOGS
     *
     * // CREATE VIEW,
     * // DROP VIEW,
     * // SHOW VIEWS
     *
     * // LOAD MODULE,
     * // UNLOAD MODULE,
     * // USE MODULES,
     * // SHOW [FULL] MODULES
     *
     * // INSERT,
     * // DESCRIBE
     *
     * @param operation
     * @return
     */
    protected TableResult execute(Operation operation) {
        return tableEnv.executeInternal(operation);
    }

    /**
     * 获取到所有有效的 Options 参数
     * @return 返回 Maps
     */
    public Map<String,? extends ConfigOption> extractTableConfigOptions(){
        Map<String, ConfigOption> configOptions = new HashMap<>();
        configOptions.putAll(extractConfigOptions(ExecutionConfigOptions.class));
        configOptions.putAll(extractConfigOptions(OptimizerConfigOptions.class));
        configOptions.putAll(extractConfigOptions(TableConfigOptions.class));
        return configOptions;

    }

    private Map<String, ? extends ConfigOption> extractConfigOptions(Class clazz) {
        Map<String, ConfigOption> configOptions = new HashMap();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(ConfigOption.class)) {
                try {
                    ConfigOption configOption = (ConfigOption) field.get(ConfigOption.class);
                    configOptions.put(configOption.key(), configOption);
                } catch (Throwable e) {
                    logger.warn("Fail to get ConfigOption", e);
                }
            }
        }
        return configOptions;

    }

    /**
     * 根据 sessionId 获取 TableEnvironment，session Id is
     * @return
     */
    public TableEnvironmentInternal initCreateTableEnv(){
        if(Asserts.isNotNull(this.tableEnv)){
            return this.tableEnv;

        }

        if(Asserts.isNull(this.sessionStudio)){
            throw new RuntimeException("Session is not exist,you must create before.");
        }
        SessionContext sessionContext = (SessionContext) this.sessionStudio.getSessionContext();
        return (TableEnvironmentInternal) sessionContext.createTableEnvironment();
    }

}
