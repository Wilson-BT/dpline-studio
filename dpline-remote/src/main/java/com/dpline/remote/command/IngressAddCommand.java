package com.dpline.remote.command;

import com.dpline.common.util.JSONUtils;

import java.io.Serializable;

/**
 * 删除 ingress，在删除 Session 的时候需要删除 Ingress
 */
public class IngressAddCommand extends AbstractOperatorCommand implements Serializable {

    /**
     * cluster
     */
    String clusterId;

    String nameSpace;

    String kubePath;

    Integer port;

    public IngressAddCommand() {
        this.commandType = CommandType.INGRESS_ADD_REQUEST;
    }

    /**
     * package response command
     *
     * @return command
     */
    public Command convert2Command() {
        Command command = new Command();
        command.setType(commandType);
        byte[] body = JSONUtils.toJsonByteArray(this);
        command.setBody(body);
        return command;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getKubePath() {
        return kubePath;
    }

    public void setKubePath(String kubePath) {
        this.kubePath = kubePath;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
