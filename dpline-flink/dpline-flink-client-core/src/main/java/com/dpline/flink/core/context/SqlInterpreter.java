package com.dpline.flink.core.context;

import com.dpline.common.request.FlinkDagResponse;
import com.dpline.common.request.FlinkSqlExplainResponse;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 解释器
 */
public interface SqlInterpreter extends Serializable {

    public FlinkSqlExplainResponse explain(List<String> sqlStatement);

    public FlinkSqlExplainResponse reCheck(List<String> sqlStatement) throws Exception;

    public String runSqlListOnCluster(List<String> sqlStatement) throws Exception;

    public Map extractTableConfigOptions() throws Exception;

}
