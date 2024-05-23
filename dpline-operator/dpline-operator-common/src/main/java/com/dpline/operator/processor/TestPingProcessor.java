package com.dpline.operator.processor;

import com.dpline.remote.command.Command;
import com.dpline.remote.command.TestCommandResp;
import com.dpline.remote.handle.NettyRequestProcessor;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TestPingProcessor implements NettyRequestProcessor {

    private Logger logger = LoggerFactory.getLogger(TestPingProcessor.class);

    @Override
    public void process(Channel channel, Command command) throws Exception {
        logger.info("服务器接收到了");
        channel.writeAndFlush(new TestCommandResp().convert2Command(command.getOpaque()));
    }
}
