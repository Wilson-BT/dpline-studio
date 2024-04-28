package com.dpline.common.request;

import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.params.SqlResult;

public class FlinkSqlExplainResponse extends Response {

    SqlResult sqlResult;

    public FlinkSqlExplainResponse(SqlResult sqlResult,ResponseStatus responseStatus,String msg) {
        this.sqlResult = sqlResult;
        this.responseStatus = responseStatus;
        this.msg = msg;
    }

    public FlinkSqlExplainResponse() {
    }

    public static FlinkSqlExplainResponse.Builder builder() {
        return new FlinkSqlExplainResponse.Builder();
    }

    public static class Builder {

        ResponseStatus responseStatus;

        String msg;

        SqlResult sqlResult;


        public FlinkSqlExplainResponse.Builder responseStatus(ResponseStatus responseStatus) {
            this.responseStatus = responseStatus;
            return this;
        }

        public FlinkSqlExplainResponse.Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public FlinkSqlExplainResponse.Builder sqlResult(SqlResult sqlResult) {
            this.sqlResult = sqlResult;
            return this;
        }

        public FlinkSqlExplainResponse build() {
            return new FlinkSqlExplainResponse(sqlResult, responseStatus, msg);
        }
    }

}
