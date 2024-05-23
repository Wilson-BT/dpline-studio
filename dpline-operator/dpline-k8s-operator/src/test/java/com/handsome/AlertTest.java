package com.handsome;

import com.dpline.common.enums.AlertMode;
import com.dpline.common.enums.ExecStatus;
import com.dpline.k8s.operator.OperatorServer;
import com.dpline.operator.entity.TaskFlushEntity;
import com.dpline.k8s.operator.watcher.TaskStatusManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OperatorServer.class)
public class AlertTest {

    @Autowired
    TaskStatusManager taskStatusManager;

    @Test
    public void callAlert(){
        TaskFlushEntity taskFlushEntity = new TaskFlushEntity(
            10899074224032L,
            "HELLO-WORLD",
            000000000000L,
            ExecStatus.SUBMITTING,
            AlertMode.ALL,
            10953116657312L,
            "000000000000000000"
        );

        this.taskStatusManager.callAlert(taskFlushEntity,ExecStatus.RUNNING);
        try {
            Thread.sleep(100000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
