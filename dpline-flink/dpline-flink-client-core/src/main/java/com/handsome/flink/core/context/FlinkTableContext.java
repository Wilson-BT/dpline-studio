package com.handsome.flink.core.context;

import com.handsome.common.enums.ExecStatus;
import com.handsome.common.enums.SqlErrorType;
import com.handsome.common.params.SqlResult;
import com.handsome.common.request.LocalRequest;
import com.handsome.common.request.RemoteRequest;
import com.handsome.common.request.Request;
import com.handsome.common.request.Response;
import com.handsome.common.util.Asserts;
import com.handsome.flink.core.*;
import com.handsome.common.enums.StreamType;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Flink sql context,use for create flink env
 * could be cache
 */
@Data
public class FlinkTableContext extends AbstractTaskRunDynamicHandler {

    public static ConcurrentHashMap<String,AbstractFlinkSqlInterpreter> contextMaps = new ConcurrentHashMap<String,AbstractFlinkSqlInterpreter>();

    public static Logger logger = LoggerFactory.getLogger(FlinkTableContext.class);

    /**
     * flink sql interpreter for verify / execute and submit sql
     */
    private AbstractFlinkSqlInterpreter flinkSqlInterpreter;



    private ReentrantReadWriteLock.WriteLock lock = new ReentrantReadWriteLock().writeLock();

    /**
     * create env
     *
     * @param
     */
    public void open(Request request) throws Exception {
        runAsClassLoader(request);
    }

    public void close() {
        this.flinkSqlInterpreter = null;
    }

    public <T> void registerUdfFunction(String udfName, Class<T> clazz) {

    }

    /**
     * 获取到
     * @param sql
     * @return
     */
    public String getStreamGraph(List<String> sql, StreamType streamType) throws Exception {
        return this.flinkSqlInterpreter.getStreamGraph(sql,streamType);
    }

    /**
     * verifySql
     *
     * @param sql
     * @return
     */
    public SqlResult verifySql(List<String> sql,StreamType streamType) {
        lock.lock();
        SqlResult sqlResult = new SqlResult(SqlErrorType.SQL_RIGHT,"success","");
        try {
            sqlResult = flinkSqlInterpreter.verifySql(sql,streamType);
        } catch (Exception e) {
            logger.error("Verify sql failed : {}",e.toString());
        }finally {
            lock.unlock();
        }
        return sqlResult;
    }

    public ExecStatus execSql(List<String> sql,StreamType streamType) {
        lock.lock();
        SqlResult sqlResult;
        try {
            // base Sql verify
            sqlResult = flinkSqlInterpreter.verifySql(sql,streamType);
            logger.info("Sql verifySql succeed");
            // if sql right
            if (sqlResult.getErrorType().equals(SqlErrorType.SQL_RIGHT)){
                flinkSqlInterpreter.runSqlListOnCluster(sql,streamType);
            }
        } catch (Exception e) {
            logger.error("exec sql failed : {}",e.toString());
        }finally {
            lock.unlock();
        }
        return null;
    }

    public void stop(StreamType streamType) {

    }

    public void cancel(StreamType streamType) {

    }

    /**
     * 仅支持本地模式
     *
     * @param targetClassLoader
     * @return
     */
    @Override
    public Response execFunc(ClassLoader targetClassLoader, Request request) throws Exception{
        // TODO 判断动作 Request 的 动作是什么，不支持提交动作，只支持本地加载类，并执行各项操作
        if(request instanceof RemoteRequest){
            logger.error("Only support localRequest,but enter RemoteRequest");
            return null;
        }
        if (request instanceof LocalRequest){
            LocalRequest localRequest = (LocalRequest) request;
            switch (localRequest.getOperatorType()){
                case SQL_VERIFY:
                    this.verifySql(localRequest.getSqls(),localRequest.getStreamType());
                    break;
                case SQL_RUN:
                    this.execSql(localRequest.getSqls(),localRequest.getStreamType());
                    break;
                case SQL_STOP:
                    // 任务停止
                    this.stop(localRequest.getStreamType());
                    break;
                case UPDATE_CONFIG:
                    // 更新 config
                    this.updateConfig(localRequest.getStreamType());
                    break;
                case TASK_GRAPH:
                    this.getStreamGraph(localRequest.getSqls(),localRequest.getStreamType());
                default:
                    throw new UnsupportedOperationException("Unsupport this com.handsome.operator");
            }
        }
        if (Asserts.isNull(this.flinkSqlInterpreter)){
            Class<?> flinkSqlShimInterpreter = targetClassLoader.loadClass("com.handsome.flink.local.FlinkSqlInterpreter");
            this.flinkSqlInterpreter = (AbstractFlinkSqlInterpreter) flinkSqlShimInterpreter.getConstructor().newInstance();
        }
//        this.flinkSqlInterpreter.createEnv(flag);
        return null;
    }

    private void updateConfig(StreamType streamType) {

    }

}
