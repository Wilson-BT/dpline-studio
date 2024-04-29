package com.handsome;

import com.dpline.common.request.FlinkK8sRemoteSubmitRequest;
import com.dpline.common.util.JSONUtils;
import com.dpline.k8s.operator.OperatorServer;
import com.dpline.k8s.operator.service.TaskOperateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OperatorServer.class)
public class TaskRunTest {

    @Autowired
    TaskOperateService taskOperateService;

    @Test
    public void runProcessor(){
        String submitRequest = "{\"runMotorType\":\"FLINK\",\"flinkHomeOptions\":{\"flinkPath\":\"/Users/wangchunshun/Documents/IdeaProjects/flink-1.14.5\",\"realVersion\":\"1.14.5\"},\"runModeType\":\"K8S_APPLICATION\",\"runtimeOptions\":{\"parallelism\":3,\"jobManagerCpu\":2.0,\"taskManagerCpu\":2.0,\"jobManagerMem\":\"2GB\",\"taskManagerMem\":\"2GB\",\"taskManagerNum\":null,\"otherParams\":{}},\"k8sOptions\":{\"imageAddress\":\"harbor.bjm6v.belle.lan/data/flink:habyk8s_metric_1.14.5_1\",\"nameSpace\":\"ts-flink-prd\",\"selectNode\":null,\"podFilePath\":null,\"exposedType\":\"CLUSTER_IP\",\"clusterId\":\"tidb-doris-retail-sync-retail-mps\",\"kubePath\":\"/Users/wangchunshun/.kube/config\",\"sessionName\":null},\"jobDefinitionOptions\":{\"mainClass\":\"com.handbinlog.SyncDataBaseTopicDriver\",\"appArgs\":[\"--app.name\",\"flink-sync-database-retail-mps\",\"--consumer.topic\",\"tidb_cdc_retail_public\",\"--mysql.username\",\"retail_rd\",\"--mysql.database\",\"retail_mps\",\"--mysql.port\",\"4000\",\"--mysql.password\",\"retail_rd\",\"--mysql.ip\",\"10.123.20.250\",\"--doris.username\",\"retail_writer\",\"--doris.database\",\"retail_mps_ods\",\"--doris.port\",\"9030\",\"--doris.password\",\"retail_writer\",\"--doris.ip\",\"10.250.148.233\",\"--time.stamp\",\"1692233511000\",\"--open.time.stamp\",\"true\",\"--consumer.groupId\",\"flink-streamx-sync-database-retail-mps\"],\"projectId\":9640278031136,\"jobId\":10668357750176,\"runJobId\":\"a08dacebb30900000000000000000000\",\"fileType\":\"DATA_STREAM\",\"jobName\":\"tidb-doris-retail-sync-retail-mps\",\"deployAddress\":\"task/9640278031136/10668357750176\",\"jarPath\":\"local:///opt/flink/main/binlog-topic-database-sync-1.1.jar\"}}";
        FlinkK8sRemoteSubmitRequest flinkSubmitRequest = JSONUtils.parseObject(submitRequest, FlinkK8sRemoteSubmitRequest.class);
        taskOperateService.submitAndWatch(flinkSubmitRequest);
    }
}
