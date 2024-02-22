package com.handsome.common.options;

import com.handsome.common.enums.RunModeType;
import com.handsome.common.enums.TaskType;
import com.handsome.common.util.JobId;
import com.handsome.common.util.StringUtils;
import com.handsome.common.util.TaskPath;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TaskDefinitionOptions {

    private String mainClass;

    private String jarPath;

    private List<String> appArgs;

    private List<Integer> dependenceResourceIds;

    private TaskType taskType;

    private long projectCode;

    private long taskId;

    private String jobId;

    public static Builder builder(){
        return new Builder();
    }
    public static class Builder {
        private final TaskDefinitionOptions taskDefinitionOptions = new TaskDefinitionOptions();

        public Builder mainClass(String mainClass){
            this.taskDefinitionOptions.setMainClass(mainClass);
            return this;
        }
        public Builder jarPath(String jarPath, RunModeType runModeType){
            if (runModeType.equals(RunModeType.K8S_APPLICATION)) {
                this.taskDefinitionOptions.setJarPath(TaskPath.getAppModeRunJarPath(jarPath));
            } else {
                this.taskDefinitionOptions.setJarPath(jarPath);
            }

            return this;
        }
        public Builder appArgs(String params){
            if(StringUtils.isNotEmpty(params)){
                this.taskDefinitionOptions.setAppArgs(Arrays.stream(params.split(" ")).collect(Collectors.toList()));
            }
            return this;
        }
        public Builder dependenceResourceIds(List<Integer> dependenceResourceIds){
            this.taskDefinitionOptions.setDependenceResourceIds(dependenceResourceIds);
            return this;
        }
        public Builder taskType(TaskType taskType){
            this.taskDefinitionOptions.setTaskType(taskType);
            return this;
        }

        public Builder projectCode(long projectCode){
            this.taskDefinitionOptions.setProjectCode(projectCode);
            return this;
        }

        public Builder taskId(long taskId){
            this.taskDefinitionOptions.setTaskId(taskId);
            this.taskDefinitionOptions.setJobId(JobId.fromHexString(Long.toString(taskId)).toHexString());
            return this;
        }



        public TaskDefinitionOptions build(){
            return this.taskDefinitionOptions;
        }

    }
}
