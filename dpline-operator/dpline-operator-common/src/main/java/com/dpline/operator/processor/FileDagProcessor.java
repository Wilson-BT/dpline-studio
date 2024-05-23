package com.dpline.operator.processor;

import com.dpline.common.request.FlinkDagRequest;
import com.dpline.common.request.FlinkDagResponse;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.JSONUtils;
import com.dpline.operator.service.FileDagService;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.command.FileDagCommand;
import com.dpline.remote.command.FileDagResponseCommand;
import com.dpline.remote.handle.NettyRequestProcessor;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileDagProcessor implements NettyRequestProcessor {

    @Autowired
    FileDagService fileDagService;

    private static final Logger logger = LoggerFactory.getLogger(FileDagProcessor.class);

    @Override
    public void process(Channel channel, Command command) throws Exception {
        Preconditions.checkArgument(CommandType.FILE_DAG_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        FileDagCommand fileDagCommand = JSONUtils.parseObject(command.getBody(), FileDagCommand.class);
        FlinkDagRequest flinkDagRequest = fileDagCommand.getFlinkDagRequest();
        if (Asserts.isNull(flinkDagRequest)){
            return;
        }
        logger.info("Request has been received,type:[{}], context:[{}]", command.getType(), flinkDagRequest);
        FlinkDagResponse flinkDagResponse = fileDagService.getFileDag(flinkDagRequest);
        FileDagResponseCommand fileDagResponseCommand = new FileDagResponseCommand(flinkDagResponse);
        channel.writeAndFlush(fileDagResponseCommand.convert2Command(command.getOpaque()));
    }
}
