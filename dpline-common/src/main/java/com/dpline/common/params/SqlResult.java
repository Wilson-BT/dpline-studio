package com.dpline.common.params;

import com.dpline.common.enums.SqlExplainType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SqlResult implements Serializable {

    SqlExplainType sqlExplainType;

    String sql;

    String message;

    String streamGraphJson;

}
