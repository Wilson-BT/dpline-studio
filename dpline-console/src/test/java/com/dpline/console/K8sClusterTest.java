package com.dpline.console;

import com.dpline.common.params.K8sClusterParams;
import com.dpline.common.util.JSONUtils;
import org.junit.Test;

public class K8sClusterTest {

    @Test
    public void serizedTest() {

        String orginStr = "{\"nameSpace\":\"flink\",\"serviceAccount\":\"xxx-sa\",\"ingressHost\":\"flink.dpline.test.com.cn\",\"ingressName\":\"dpline-flink-prd-ingress\",\"kubePath\":\"/home/dpline/.kube/config\",\"extraParam\":[{\"key\":\"k8s.wonhigh.cn/role\",\"value\":\"flink\"}]}";


        K8sClusterParams k8sClusterParams = JSONUtils.parseObject(orginStr, K8sClusterParams.class);

        System.out.println(k8sClusterParams.toString());

    }
}
