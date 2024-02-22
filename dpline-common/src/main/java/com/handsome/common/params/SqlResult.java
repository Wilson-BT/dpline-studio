package com.handsome.common.params;

import com.handsome.common.enums.SqlErrorType;
import lombok.Data;

@Data
public class SqlResult {

    SqlErrorType errorType;

    String message;

    String sql;

    String jobId;

    public SqlResult(SqlErrorType errorType,String message,String sql){
        this.errorType = errorType;
        this.message = message;
        this.sql=sql;
    }

}
