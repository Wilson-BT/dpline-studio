package com.handsome.common.utiltest;


import com.handsome.common.util.MinioUtils;
import com.handsome.console.ApiApplicationServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplicationServer.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckPointAddressTest {
    // 首先需要将所有的病

    @Test
    public void getLastCheckPointAddress(){
        MinioUtils minio = MinioUtils.getInstance();
        try {
            String flinkSqlTest = minio.getLastCheckPointAddress(5483260672288L, "flinkSqlTest", "00000000000000000000000000000000");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
