package com.handsome.operator;

import com.dpline.common.enums.RunModeType;
import com.dpline.common.params.FlinkHomeOptions;
import com.dpline.common.request.FlinkStopRequest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class StopTest {

    @Before
    public void exportAppHome(){
        System.setProperty("APP_HOME","/Users/wangchunshun/Documents/IdeaProjects/dpline-studio");
    }

    @Test
    public void stopApplication() throws IOException, URISyntaxException {
        FlinkStopRequest stopRequest = new FlinkStopRequest();

//        String jobId = JobID.fromHexString("00000000000000000000000000000000").toString();
        stopRequest.setClusterId("flink-sync-database-retail-pos");
        stopRequest.setWithSavePointAddress(false);
        stopRequest.setRunModeType(RunModeType.K8S_APPLICATION);
//        stopRequest.setKubePath("/Users/wangchunshun/.kube/config");
//        stopRequest.setNameSpace("ts-flink-prd");
        FlinkHomeOptions flinkHomeOptions = new FlinkHomeOptions();
        flinkHomeOptions.setFlinkPath("/Users/wangchunshun/Documents/IdeaProjects/flink-1.14.5");
        flinkHomeOptions.setRealVersion("1.14.5");
        stopRequest.setFlinkHomeOptions(flinkHomeOptions);

//        K8sApplicationStopper k8sApplicationStopper = new K8sApplicationStopper();
//        k8sApplicationStopper.stop(stopRequest);
    }
}
