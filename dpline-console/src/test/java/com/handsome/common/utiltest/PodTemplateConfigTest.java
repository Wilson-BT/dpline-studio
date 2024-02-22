package com.handsome.common.utiltest;

import com.handsome.console.ApiApplicationServer;
import com.handsome.console.config.PodTemplateConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplicationServer.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PodTemplateConfigTest {
    @Autowired
    PodTemplateConfig podTemplateConfig;
    @Test
    public void getPodTemplateConfig(){
        System.out.println(podTemplateConfig.toString());
    }

}
