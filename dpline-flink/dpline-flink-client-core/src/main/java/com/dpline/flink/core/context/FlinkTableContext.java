//package com.dpline.flink.core.context;
//
//import com.dpline.common.enums.SqlExplainType;
//import com.dpline.common.request.*;
//import com.dpline.common.enums.ExecStatus;
//import com.dpline.common.enums.SqlErrorType;
//import com.dpline.common.params.SqlResult;
//import com.dpline.common.util.Asserts;
//import com.dpline.common.enums.StreamType;
//import lombok.Data;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
///**
// * Flink sql context,use for create flink env
// * could be cache
// */
//@Data
//public class FlinkTableContext {
//
//    public static ConcurrentHashMap<String,AbstractFlinkInterpreter> contextMaps = new ConcurrentHashMap<String,AbstractFlinkInterpreter>();
//
//    public static Logger logger = LoggerFactory.getLogger(FlinkTableContext.class);
//
//    /**
//     * flink sql interpreter for verify / execute and submit sql
//     */
//    private AbstractFlinkInterpreter flinkSqlInterpreter;
//
//
//
//    private ReentrantReadWriteLock.WriteLock lock = new ReentrantReadWriteLock().writeLock();
//
//    /**
//     * create env
//     *
//     * @param
//     */
//    public void open(FlinkRequest request) throws Exception {
//        runAsClassLoader(request);
//    }
//
//    public void close() {
//        this.flinkSqlInterpreter = null;
//    }
//
//    public <T> void registerUdfFunction(String udfName, Class<T> clazz) {
//
//    }
//
//    /**
//     * 获取到
//     * @param sql
//     * @return
//     */
//    public String getStreamGraph(List<String> sql,boolean statement) throws Exception {
//        return this.flinkSqlInterpreter.getStreamGraph(sql,statement);
//    }
//
//    /**
//     * verifySql
//     *
//     * @param sql
//     * @return
//     */
//    public SqlResult verifySql(List<String> sql,StreamType streamType) {
//        lock.lock();
//        SqlResult sqlResult = new SqlResult(SqlExplainType.SQL_RIGHT,"success","");
//        try {
//            sqlResult = flinkSqlInterpreter.execute(sql,streamType);
//        } catch (Exception e) {
//            logger.error("Verify sql failed : {}",e.toString());
//        }finally {
//            lock.unlock();
//        }
//        return sqlResult;
//    }
//
//    public ExecStatus execSql(List<String> sql,StreamType streamType) {
//        lock.lock();
//        SqlResult sqlResult;
//        try {
//            // base Sql verify
//            sqlResult = flinkSqlInterpreter.verifySql(sql,streamType);
//            logger.info("Sql verifySql succeed");
//            // if sql right
//            if (sqlResult.getErrorType().equals(SqlErrorType.SQL_RIGHT)){
//                flinkSqlInterpreter.runSqlListOnCluster(sql,streamType);
//            }
//        } catch (Exception e) {
//            logger.error("exec sql failed : {}",e.toString());
//        }finally {
//            lock.unlock();
//        }
//        return null;
//    }
//
//    public void stop(StreamType streamType) {
//
//    }
//
//    public void cancel(StreamType streamType) {
//
//    }
//
//    /**
//     * 仅支持本地模式
//     *
//     * @param targetClassLoader
//     * @return
//     */
//    @Override
//    public Response execFunc(ClassLoader targetClassLoader, Request request) throws Exception{
//        // TODO 判断动作 Request 的 动作是什么，不支持提交动作，只支持本地加载类，并执行各项操作
//        if(request instanceof FlinkSubmitRequest){
//            logger.error("Only support localRequest,but enter FlinkSubmitRequest");
//            return null;
//        }
//        if (request instanceof FlinkLocalRequest){
//            FlinkLocalRequest flinkLocalRequest = (FlinkLocalRequest) request;
//            switch (flinkLocalRequest.getOperatorType()){
//                case SQL_VERIFY:
//                    this.verifySql(flinkLocalRequest.getSqls(), flinkLocalRequest.getStreamType());
//                    break;
//                case SQL_RUN:
//                    this.execSql(flinkLocalRequest.getSqls(), flinkLocalRequest.getStreamType());
//                    break;
//                case SQL_STOP:
//                    // 任务停止
//                    this.stop(flinkLocalRequest.getStreamType());
//                    break;
//                case UPDATE_CONFIG:
//                    // 更新 config
//                    this.updateConfig(flinkLocalRequest.getStreamType());
//                    break;
//                case TASK_GRAPH:
////                    this.getStreamGraph(flinkLocalRequest.getSqls(), flinkLocalRequest.getStreamType());
//                default:
//                    throw new UnsupportedOperationException("Unsupport this com.handsome.operator");
//            }
//        }
//        if (Asserts.isNull(this.flinkSqlInterpreter)){
//            Class<?> flinkSqlShimInterpreter = targetClassLoader.loadClass("com.handsome.flink.local.FlinkSqlInterpreter");
//            this.flinkSqlInterpreter = (AbstractFlinkInterpreter) flinkSqlShimInterpreter.getConstructor().newInstance();
//        }
////        this.flinkSqlInterpreter.createEnv(flag);
//        return null;
//    }
//
//    private void updateConfig(StreamType streamType) {
//
//    }
//
//}
