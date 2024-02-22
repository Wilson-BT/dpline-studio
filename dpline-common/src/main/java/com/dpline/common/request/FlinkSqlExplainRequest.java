package com.dpline.common.request;

import com.dpline.common.enums.FileType;
import com.dpline.common.enums.RunModeType;
import com.dpline.common.params.FlinkHomeOptions;
import com.dpline.common.params.RuntimeOptions;
import lombok.Data;

import java.util.List;


@Data
public class FlinkSqlExplainRequest extends FlinkRequest {

    /**
     * config
     */
    RuntimeOptions runtimeOptions;

    /**
     * sql 集合
     */
    List<String> sqlState;


    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {

        private List<String> sqlState;

        private FlinkHomeOptions flinkHomeOptions;

        private RuntimeOptions runtimeOptions;

        private List<JarResource> extendedJarResources;

        public Builder flinkHomeOptions(FlinkHomeOptions flinkHomeOptions){
            this.flinkHomeOptions = flinkHomeOptions;
            return this;
        }

        public Builder runtimeOptions(RuntimeOptions runtimeOptions){
            this.runtimeOptions = runtimeOptions;
            return this;
        }

        public Builder extendedJarResources(List<JarResource> extendedJarResources){
            this.extendedJarResources = extendedJarResources;
            return this;
        }

        public Builder sqlState(List<String> sqlState){
            this.sqlState = sqlState;
            return this;
        }

        public FlinkSqlExplainRequest build(){
            FlinkSqlExplainRequest explainRequest = new FlinkSqlExplainRequest();
            // 版本信息
            explainRequest.setFlinkHomeOptions(flinkHomeOptions);
            explainRequest.setRuntimeOptions(runtimeOptions);

            explainRequest.setFileType(FileType.SQL_STREAM);
            explainRequest.setExtendedJarResources(extendedJarResources);
            // 对外 exposedType 类型
            explainRequest.setSqlState(sqlState);
            return explainRequest;
        }

    }


}
