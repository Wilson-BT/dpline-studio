package com.handsome.console.handler;

import com.handsome.dao.entity.FlinkRunTaskInstance;
import com.handsome.dao.entity.FlinkTaskTagLog;

import java.io.IOException;
import java.net.URISyntaxException;

public interface DeployHandler {

    String deploy(FlinkTaskTagLog flinkTaskTagLog, FlinkRunTaskInstance flinkRunTaskInstance) throws URISyntaxException, IOException;

//    String createTemPodFile(FlinkRunTaskInstance flinkRunTaskInstance) throws IOException;

    void clear(FlinkRunTaskInstance flinkRunTaskInstance);

}
