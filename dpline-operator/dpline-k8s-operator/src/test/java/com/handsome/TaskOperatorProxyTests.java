package com.handsome;

import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.request.FlinkDagRequest;
import com.dpline.common.request.FlinkStopRequest;
import com.dpline.common.request.FlinkTriggerRequest;
import com.dpline.common.request.Response;
import com.dpline.common.util.JSONUtils;
import com.dpline.flink.api.TaskOperateProxy;
import org.junit.Test;

public class TaskOperatorProxyTests {

    @Test
    public void fileTagTest(){
        System.setProperty("APP_HOME","/Users/wangchunshun/Documents/IdeaProjects/dpline-studio");
        String str = "{\"runMotorType\":\"FLINK\",\"flinkHomeOptions\":{\"flinkPath\":\"/Users/wangchunshun/Documents/IdeaProjects/flink-1.14.5\",\"realVersion\":\"1.14.5\"},\"runModeType\":null,\"fileType\":\"DATA_STREAM\",\"extendJars\":null,\"className\":\"com.dpline.flink.log.Application\",\"mainJarResource\":{\"remotePath\":\"upload/jar/public/11373499538848/11375542012704/dpline-flink-log-kafka2doris-1.14-0.0.2.jar\",\"localParentPath\":\"/Users/wangchunshun/Desktop\",\"jarName\":\"dpline-flink-log-kafka2doris-1.14-0.0.2.jar\"},\"args\":\"\",\"extendedJarResources\":[{\"remotePath\":null,\"localParentPath\":\"/Users/wangchunshun/Documents/IdeaProjects/flink-1.14.5\",\"jarName\":\"flink-sql-connector-kafka_2.11-1.14.5.jar\"}]}";
        FlinkDagRequest flinkDagRequest = JSONUtils.parseObject(str, FlinkDagRequest.class);
        Response apply = TaskOperateProxy.execute(OperationsEnum.EXPLAIN,flinkDagRequest);
    }

    @Test
    public void triggerTest(){
        System.setProperty("APP_HOME","/Users/wangchunshun/Documents/IdeaProjects/dpline-studio");
        String str="{\"runMotorType\":\"FLINK\",\"flinkHomeOptions\":{\"flinkPath\":\"/data/flink-1.14.5\",\"realVersion\":\"1.14.5\"},\"runModeType\":\"K8S_APPLICATION\",\"fileType\":null,\"extendJars\":[],\"clusterId\":\"flink-sync-database-retail-mps\",\"runJobId\":\"d3a3e1d5995fc3da457bab8befa0ab25\",\"savePointAddress\":\"s3://flink/savepoint/9640278031136/11042017247904/d3a3e1d5995fc3da457bab8befa0ab25\",\"nameSpace\":\"ts-flink-prd\",\"jobId\":11042017247904}";
        FlinkTriggerRequest flinkDagRequest = JSONUtils.parseObject(str, FlinkTriggerRequest.class);
        Response apply = TaskOperateProxy.execute(OperationsEnum.TRIGGER,flinkDagRequest);
    }

    @Test
    public void stopTest(){
        System.setProperty("APP_HOME","/Users/wangchunshun/Documents/IdeaProjects/dpline-studio");
        String str="{\"runMotorType\":\"FLINK\",\"flinkHomeOptions\":{\"flinkPath\":\"/data/flink-1.14.5\",\"realVersion\":\"1.14.5\"},\"runModeType\":\"K8S_APPLICATION\",\"fileType\":null,\"extendJars\":[],\"clusterId\":\"flink-sync-database-retail-mps\",\"runJobId\":\"d3a3e1d5995fc3da457bab8befa0ab25\",\"withSavePointAddress\":false,\"savePointAddress\":null,\"nameSpace\":\"ts-flink-prd\",\"kubePath\":\"/home/dolph/.kube/config\",\"jobId\":11042017247904}";
        FlinkStopRequest flinkDagRequest = JSONUtils.parseObject(str, FlinkStopRequest.class);
        Response apply = TaskOperateProxy.execute(OperationsEnum.STOP,flinkDagRequest);
    }
}
