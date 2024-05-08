package com.dpline.flink;


import com.dpline.common.util.HttpUtils;
import com.dpline.common.util.JSONUtils;
import com.dpline.common.util.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlinkTest {

    @Test
    public void stop(){
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("cancel-job","false");
        String savePointPath = "s3://flink/savepoint/1111";
        if (StringUtils.isNotEmpty(savePointPath)){
            bodyMap.put("target-directory",savePointPath);
        }
        try {
            String content = HttpUtils.doStringBodyPost("http://flink.dpline.com.cn/tidb-doris-retail-sync-retail-mps-rest/jobs/a08dacebb30900000000000000000000/savepoints", JSONUtils.toJsonString(bodyMap));
            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
