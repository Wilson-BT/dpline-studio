//package com.dpline.flink.explain;
//
//import com.dpline.flink.core.context.AbstractFlinkInterpreter;
//import com.dpline.flink.core.context.SqlCommandParser;
//import com.dpline.common.params.SqlResult;
//import com.dpline.common.util.StringUtils;
//import com.dpline.common.enums.StreamType;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.flink.configuration.ConfigOption;
//import org.apache.flink.core.execution.JobClient;
//import org.apache.flink.table.api.SqlDialect;
//import org.apache.flink.table.api.StatementSet;
//import org.apache.flink.table.api.TableException;
//import org.apache.flink.table.api.TableResult;
//import org.apache.flink.table.api.config.ExecutionConfigOptions;
//import org.apache.flink.table.api.config.OptimizerConfigOptions;
//import org.apache.flink.table.api.config.TableConfigOptions;
//import org.apache.flink.table.api.internal.TableEnvironmentImpl;
//import org.apache.flink.table.api.internal.TableEnvironmentInternal;
//import org.apache.flink.table.operations.*;
//import org.apache.flink.table.operations.command.SetOperation;
//import org.apache.flink.table.operations.ddl.*;
//
//import java.lang.reflect.Field;
//import java.util.*;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class FlinkSqlInterpreter extends AbstractFlinkInterpreter {
//
//    private static Logger logger = LoggerFactory.getLogger(FlinkSqlInterpreter.class);
//
//    private final FlinkTableEnv flinkTableEnv;
//
//    private final Map tableConfigOptions;
//
//    public FlinkSqlInterpreter() {
//        this.flinkTableEnv = new FlinkTableEnv();
//        this.tableConfigOptions = extractTableConfigOptions();
//    }
//
//    public void createEnv(StreamType streamType){
//        this.flinkTableEnv.createStreamTableEnv(streamType, false);
//    }
//
//    public void updateEnvConfig(Map<String,String> envConfig,StreamType streamType) {
//        TableEnvironmentInternal tableEnv = getTableEnv(streamType);
//        envConfig.forEach((key,value)->{
//            if(this.tableConfigOptions.containsKey(key)){
//                logger.info("Execute Set key : [{}], value : [{}]",key,value);
//                if (TableConfigOptions.TABLE_SQL_DIALECT.key().equalsIgnoreCase(value)) {
//                    // remove
//                    tableEnv.getConfig().setSqlDialect(SqlDialect.valueOf(value));
//                } else {
//                    tableEnv.getConfig()
//                        .getConfiguration()
//                        .setString(key,value);
//                }
//            } else {
//                logger.warn("UnSupport set key : [{}]",key);
//            }
//        });
//    }
//
//
//    /**
//     * verify multi sql
//     *
//     * @return
//     */
//    public SqlResult explain(List<String> sqlState,String streamType) {
//        TableEnvironmentInternal effectiveEnv = getTableEnv();
//        if (CollectionUtils.isEmpty(sqlState)) {
//            return new SqlResult(SqlErrorType.VERIFY_FAILED, "sql is empty", sqlState.toString());
//        }
//        for (String sql : sqlState) {
//            Optional<SqlCommandParser.SqlCommandCall> sqlCommandCall = null;
//            try {
//                sqlCommandCall = parseSingleSql(effectiveEnv, sql);
//            } catch (Exception e) {
//                return new SqlResult(SqlErrorType.SYNTAX_ERROR, e.toString(), sql);
//            }
//            if (!sqlCommandCall.isPresent()) {
//                return new SqlResult(SqlErrorType.UNSUPPORTED_SQL, "UnSupport sql error", sql);
//            }
//        }
//        return new SqlResult(SqlErrorType.SQL_RIGHT, "success", sqlState.toString());
//    }
//
//    /**
//     * @param flinkTabEnv
//     * @param sqlState
//     * @return
//     */
//    public Optional<SqlCommandParser.SqlCommandCall> parseSingleSql(TableEnvironmentInternal flinkTabEnv, String sqlState) throws Exception {
//        SqlCommandParser.SqlCommandCall sqlCommandCall = null;
//        Optional<SqlCommandParser.SqlCommandCall> callOpt = parseByRegexMatching(sqlState, null);
//        if (callOpt.isPresent()) {
//            sqlCommandCall = callOpt.get();
//        } else {
//            sqlCommandCall = convertToSqlCommandCall(flinkTabEnv, sqlState);
//        }
//        return Optional.ofNullable(sqlCommandCall);
//    }
//
//    /**
//     * parse sql by parser
//     *
//     * @param sqlState
//     * @return List of Operation
//     */
//    public List<Operation> parseSqlByEnvParserToOperation(TableEnvironmentInternal env, String sqlState) throws Exception {
//        List<Operation> operations;
//        try {
//            operations = env.getParser().parse(sqlState);
//        } catch (Throwable e) {
//            throw new Exception("Invalidate SQL statement.", e);
//        }
//        if (operations.size() != 1) {
//            throw new Exception("Only single statement is supported now.");
//        }
//        return operations;
//    }
//
//    /**
//     * convert to sqlCommandCall
//     *
//     * @param env
//     * @param sqlState
//     * @return
//     * @throws Exception
//     */
//    public SqlCommandParser.SqlCommandCall convertToSqlCommandCall(TableEnvironmentInternal env, String sqlState) throws Exception {
//        List<Operation> operations = parseSqlByEnvParserToOperation(env, sqlState);
//        final SqlCommandParser.SqlCommand cmd;
//        String[] operands = new String[]{sqlState};
//        Operation operation = operations.get(0);
//        if (operation instanceof SinkModifyOperation) {
//            boolean overwrite = ((SinkModifyOperation) operation).isOverwrite();
//            cmd = overwrite ? SqlCommandParser.SqlCommand.INSERT_OVERWRITE : SqlCommandParser.SqlCommand.INSERT_INTO;
//        } else if (operation instanceof CreateTableOperation) {
//            cmd = SqlCommandParser.SqlCommand.CREATE_TABLE;
//        } else if (operation instanceof DropTableOperation) {
//            cmd = SqlCommandParser.SqlCommand.DROP_TABLE;
//        } else if (operation instanceof AlterTableOperation) {
//            cmd = SqlCommandParser.SqlCommand.ALTER_TABLE;
//        } else if (operation instanceof CreateViewOperation) {
//            cmd = SqlCommandParser.SqlCommand.CREATE_VIEW;
//        } else if (operation instanceof DropViewOperation) {
//            cmd = SqlCommandParser.SqlCommand.DROP_VIEW;
//        } else if (operation instanceof CreateDatabaseOperation) {
//            cmd = SqlCommandParser.SqlCommand.CREATE_DATABASE;
//        } else if (operation instanceof DropDatabaseOperation) {
//            cmd = SqlCommandParser.SqlCommand.DROP_DATABASE;
//        } else if (operation instanceof AlterDatabaseOperation) {
//            cmd = SqlCommandParser.SqlCommand.ALTER_DATABASE;
//        } else if (operation instanceof CreateCatalogOperation) {
//            cmd = SqlCommandParser.SqlCommand.CREATE_CATALOG;
//        } else if (operation instanceof DropCatalogOperation) {
//            cmd = SqlCommandParser.SqlCommand.DROP_CATALOG;
//        } else if (operation instanceof UseCatalogOperation) {
//            cmd = SqlCommandParser.SqlCommand.USE_CATALOG;
//            operands = new String[]{((UseCatalogOperation) operation).getCatalogName()};
//        } else if (operation instanceof UseDatabaseOperation) {
//            cmd = SqlCommandParser.SqlCommand.USE;
//            operands = new String[]{((UseDatabaseOperation) operation).getDatabaseName()};
//        } else if (operation instanceof ShowCatalogsOperation) {
//            cmd = SqlCommandParser.SqlCommand.SHOW_CATALOGS;
//            operands = new String[0];
//        } else if (operation instanceof ShowDatabasesOperation) {
//            cmd = SqlCommandParser.SqlCommand.SHOW_DATABASES;
//            operands = new String[0];
//        } else if (operation instanceof ShowTablesOperation) {
//            cmd = SqlCommandParser.SqlCommand.SHOW_TABLES;
//            operands = new String[0];
//        } else if (operation instanceof ShowFunctionsOperation) {
//            cmd = SqlCommandParser.SqlCommand.SHOW_FUNCTIONS;
//            operands = new String[0];
//        } else if (operation instanceof CreateCatalogFunctionOperation ||
//                operation instanceof CreateTempSystemFunctionOperation) {
//            cmd = SqlCommandParser.SqlCommand.CREATE_FUNCTION;
//        } else if (operation instanceof DropCatalogFunctionOperation ||
//                operation instanceof DropTempSystemFunctionOperation) {
//            cmd = SqlCommandParser.SqlCommand.DROP_FUNCTION;
//        } else if (operation instanceof AlterCatalogFunctionOperation) {
//            cmd = SqlCommandParser.SqlCommand.ALTER_FUNCTION;
//        } else if (operation instanceof ExplainOperation) {
//            cmd = SqlCommandParser.SqlCommand.EXPLAIN;
//        } else if (operation instanceof DescribeTableOperation) {
//            cmd = SqlCommandParser.SqlCommand.DESCRIBE;
//            operands = new String[]{((DescribeTableOperation) operation).getSqlIdentifier().asSerializableString()};
//        } else if (operation instanceof QueryOperation) {
//            cmd = SqlCommandParser.SqlCommand.SELECT;
//        } else {
//            throw new Exception("Unknown operation: " + operation.asSummaryString());
//        }
//
//        return new SqlCommandParser.SqlCommandCall(cmd, operands, sqlState);
//    }
//
//    /**
//     * get exe StreamGraph by ModifyOperation
//     * need execute sql ,not support show、explain and describe operation
//     *
//     * @return
//     */
//    @Override
//    public String getStreamGraph(List<String> sqls,StreamType streamType) throws Exception {
//        List<ModifyOperation> modifyOperations = new ArrayList<>();
//        TableEnvironmentImpl tableEnv =
//                (TableEnvironmentImpl) getTableEnv(streamType);
//        for (String statement : sqls) {
//            List<Operation> operations = parseSqlByEnvParserToOperation(tableEnv, statement);
//            if (operations.size() != 1) {
//                throw new TableException("Sql Query is not supported.");
//            } else {
//                Operation operation = operations.get(0);
//                if (operation instanceof ModifyOperation) {
//                    modifyOperations.add((ModifyOperation) operation);
//                } else if (operation instanceof ShowOperation
//                        || operation instanceof ExplainOperation
//                        || operation instanceof DescribeTableOperation) {
//                    // if show 、ExplainOperation、DescribeTableOperation，not execute
//                    continue;
//                } else if (operation instanceof SetOperation){
//                    setOperationExecute(tableEnv,statement);
//                } else {
//                    this.execute(tableEnv, operation);
//                }
//            }
//        }
//        return tableEnv.compilePlan(modifyOperations).asJsonString();
//    }
//    private void setOperationExecute(TableEnvironmentImpl tableEnv,String statement){
//        Optional<SqlCommandParser.SqlCommandCall> sqlCommandCall = parseByRegexMatching(statement, SqlCommandParser.SqlCommand.SET);
//        sqlCommandCall.ifPresent(x -> {
//            if(this.tableConfigOptions.containsKey(x.operands[0])){
//                logger.info("Execute Set key : [{}], value : [{}]",x.operands[0],x.operands[1]);
//                if (TableConfigOptions.TABLE_SQL_DIALECT.key().equalsIgnoreCase(x.operands[0])) {
//                    // remove
//                    tableEnv.getConfig().setSqlDialect(SqlDialect.valueOf(x.operands[1].toUpperCase()));
//                } else {
//                    tableEnv.getConfig()
//                            .getConfiguration()
//                            .setString(x.operands[0],x.operands[1]);
//                }
//            } else {
//                logger.warn("UnSupport set key : [{}]",x.operands[0]);
//            }
//        });
//    }
//
//    /**
//     * run sql on cluster
//     * @param sqls sql list
//     * @throws Exception
//     */
//    @Override
//    public String runSqlListOnCluster(List<String> sqls,StreamType streamType) throws Exception {
//        TableEnvironmentImpl tableEnv =
//                (TableEnvironmentImpl) getTableEnv(streamType);
//        StatementSet statementSet = tableEnv.createStatementSet();
//        for (String statement : sqls) {
//            List<Operation> operations = parseSqlByEnvParserToOperation(tableEnv, statement);
//            Operation operation = operations.get(0);
//            if (operation instanceof SinkModifyOperation) {
//                statementSet.addInsertSql(statement);
//            } else if (operation instanceof QueryOperation) {
//                throw new RuntimeException("UnSupport select DML");
//            } else if (operation instanceof ShowOperation || operation instanceof DescribeTableOperation
//                    || operation instanceof ExplainOperation) {
//                logger.warn("UnSupport [show]/[desc]/[explain] sql");
//            } else if (operation instanceof SetOperation) {
//                // if set，key、value
//                Optional<String> key = ((SetOperation) operation).getKey();
//                Optional<String> value = ((SetOperation) operation).getValue();
//                key.ifPresent(x->{
//                    if (TableConfigOptions.TABLE_SQL_DIALECT.key().equalsIgnoreCase(key.get())) {
//                        // remove
//                        tableEnv.getConfig().setSqlDialect(SqlDialect.valueOf(key.get()));
//                    } else {
//                        value.ifPresent(v -> tableEnv.getConfig()
//                                .getConfiguration()
//                                .setString(key.get(),value.get()));
//                    }
//                });
//            } else {
//                execute(tableEnv,operation);
//                logger.info("Execute sql : [{}]", statement);
//            }
//        }
//        TableResult execute = statementSet.execute();
//        Optional<JobClient> jobClient = execute.getJobClient();
//        if (jobClient.isPresent()) {
//            logger.info("JobId: {}. run status: {}", jobClient.get().getJobID(), jobClient.get().getJobStatus());
//            return jobClient.get().getJobID().toString();
//        }
//        return StringUtils.EMPTY;
//    }
//
//
//    /**
//     * create and alter table / database
//     *
//     * // CREATE TABLE,
//     * // DROP TABLE,
//     * // ALTER TABLE,
//     * // SHOW TABLES
//     *
//     * // CREATE DATABASE,
//     * // DROP DATABASE,
//     * // ALTER DATABASE,
//     * // USE [CATALOG.]DATABASE,
//     * // SHOW DATABASES
//     *
//     * // CREATE FUNCTION,
//     * // DROP FUNCTION,
//     * // ALTER FUNCTION,
//     * // SHOW [USER] FUNCTIONS,
//     * // SHOW PARTITIONS
//     *
//     * // CREATE CATALOG,
//     * // DROP CATALOG,
//     * // USE CATALOG,
//     * // SHOW CATALOGS
//     *
//     * // CREATE VIEW,
//     * // DROP VIEW,
//     * // SHOW VIEWS
//     *
//     * // LOAD MODULE,
//     * // UNLOAD MODULE,
//     * // USE MODULES,
//     * // SHOW [FULL] MODULES
//     *
//     * // INSERT,
//     * // DESCRIBE
//     *
//     * @param tableEnv
//     * @param operation
//     * @return
//     */
//    protected TableResult execute(TableEnvironmentImpl tableEnv, Operation operation) {
//        return tableEnv.executeInternal(operation);
//    }
//
//    /**
//     * 获取到所有有效的 Options 参数
//     * @return 返回 Maps
//     */
//    public Map extractTableConfigOptions(){
//        Map<String, ConfigOption> configOptions = new HashMap<>();
//        configOptions.putAll(extractConfigOptions(ExecutionConfigOptions.class));
//        configOptions.putAll(extractConfigOptions(OptimizerConfigOptions.class));
//        configOptions.putAll(extractConfigOptions(TableConfigOptions.class));
//        return configOptions;
//
//    }
//
//    private Map<String, ? extends ConfigOption> extractConfigOptions(Class clazz) {
//        Map<String, ConfigOption> configOptions = new HashMap();
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            if (field.getType().isAssignableFrom(ConfigOption.class)) {
//                try {
//                    ConfigOption configOption = (ConfigOption) field.get(ConfigOption.class);
//                    configOptions.put(configOption.key(), configOption);
//                } catch (Throwable e) {
//                    logger.warn("Fail to get ConfigOption", e);
//                }
//            }
//        }
//        return configOptions;
//
//    }
//
//    public FlinkTableEnv getFlinkTableEnv() {
//        return flinkTableEnv;
//    }
//    public TableEnvironmentInternal getTableEnv(StreamType streamType){
//        return (TableEnvironmentInternal) this.flinkTableEnv.getObjectEnv(streamType);
//    }
//
//}
