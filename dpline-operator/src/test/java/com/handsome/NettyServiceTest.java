package com.handsome;

import com.dpline.common.util.JSONUtils;
import com.dpline.operator.OperatorServer;
import com.dpline.remote.NettyRemoteClient;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.Host;
import com.dpline.remote.command.TestCommandReq;
import com.dpline.remote.command.TestCommandResp;
import com.dpline.remote.config.NettyClientConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = OperatorServer.class)
public class NettyServiceTest {

    @Test
    public void nettyServiceTest(){
        NettyClientConfig nettyClientConfig = new NettyClientConfig();
        NettyRemoteClient nettyRemoteClient = new NettyRemoteClient(nettyClientConfig);
        TestCommandReq testCommandReq = new TestCommandReq();
//        nettyRemoteClient.port = nettyServerConfig.getListenPort();
        final Host address = new Host("127.0.0.1", 50055);
        TestCommandResp deserialize = null;
        try {
            Command receivedCommand = nettyRemoteClient.sendSync(address, testCommandReq.convert2Command(), 200000L);
            deserialize = JSONUtils.parseObject(receivedCommand.getBody(), TestCommandResp.class);
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            nettyRemoteClient.closeChannel(address);
        }
        return;
    }

}
