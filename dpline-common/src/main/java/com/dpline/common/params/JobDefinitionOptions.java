package com.dpline.common.params;

import com.dpline.common.enums.FileType;
import com.dpline.common.util.ParamsUtil;
import com.dpline.common.util.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class JobDefinitionOptions implements Serializable {

    private String mainClass;

    private List<String> appArgs;

    private Long projectId;

    private Long jobId;

    private String runJobId;

    private FileType fileType;

    private String jobName;

    private String deployAddress;

    private String jarPath;

    private String defaultCheckPointDir;

    public static Builder builder(){
        return new Builder();
    }
    public static class Builder {
        private final JobDefinitionOptions jobDefinitionOptions = new JobDefinitionOptions();

        public Builder mainClass(String mainClass){
            this.jobDefinitionOptions.setMainClass(mainClass);
            return this;
        }

        public Builder deployAddress(String deployAddress){
            this.jobDefinitionOptions.setDeployAddress(deployAddress);
            return this;
        }

        public Builder appArgs(String params){
            if(StringUtils.isNotEmpty(params)){
                this.jobDefinitionOptions.setAppArgs(ParamsUtil.getParams(params));
            }
            return this;
        }

        public Builder projectId(long projectId){
            this.jobDefinitionOptions.setProjectId(projectId);
            return this;
        }

        public Builder jobId(long jobId){
            this.jobDefinitionOptions.setJobId(jobId);
            return this;
        }
        public Builder runJobId(String runJobId){
            this.jobDefinitionOptions.setRunJobId(runJobId);
            return this;
        }

        public Builder fileType(FileType fileType){
            this.jobDefinitionOptions.setFileType(fileType);
            return this;
        }

        public Builder jobName(String jobName){
            this.jobDefinitionOptions.setJobName(jobName);
            return this;
        }
        public Builder jarPath(String jarPath){
            this.jobDefinitionOptions.setJarPath(jarPath);
            return this;
        }

        public Builder defaultCheckPointDir(String jarPath){
            this.jobDefinitionOptions.setDefaultCheckPointDir(jarPath);
            return this;
        }

        public JobDefinitionOptions build(){
            return this.jobDefinitionOptions;
        }

    }
}
