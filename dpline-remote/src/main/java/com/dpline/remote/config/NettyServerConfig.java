
package com.dpline.remote.config;

import com.dpline.common.Constants;
import com.dpline.common.enums.ClusterType;
import com.dpline.common.params.CommonProperties;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.PropertyUtils;
import com.dpline.common.util.StringUtils;
import com.dpline.remote.util.RpcAddress;

import javax.xml.bind.PropertyException;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  netty server config
 */
public class NettyServerConfig {

    /**
     * init the server connectable queue
     */
    private int soBacklog = 1024;

    /**
     *  whether tpc delay
     */
    private boolean tcpNoDelay = true;

    /**
     *  whether keep alive
     */
    private boolean soKeepalive = true;

    /**
     *  send buffer size
     */
    private int sendBufferSize = 65535;

    /**
     *  receive buffer size
     */
    private int receiveBufferSize = 65535;

    /**
     *  listen port
     */
//    private int listenPort = 50055;

    private ConcurrentHashMap<String, RpcAddress> rpcAddressMap = new ConcurrentHashMap<String, RpcAddress>();

    public NettyServerConfig() {
        RpcAddress yarnRpcAddress = new RpcAddress(CommonProperties.getYarnOperatorListenHost(),
                CommonProperties.getYarnOperatorListenPort());
        RpcAddress K8sRpcAddress = new RpcAddress(CommonProperties.getK8sOperatorListenHost(),
                CommonProperties.getK8sOperatorListenPort());
        rpcAddressMap.put(ClusterType.YARN.getValue(),yarnRpcAddress);
        rpcAddressMap.put(ClusterType.KUBERNETES.getValue(),K8sRpcAddress);
    }

    public String getServerHost(ClusterType clusterType) {
        return rpcAddressMap.get(clusterType.getValue()).getIp();
    }

    public int getListenPort(ClusterType clusterType){
        return rpcAddressMap.get(clusterType.getValue()).getPort();
    }

//    public void setListenPort(int listenPort) {
//        this.listenPort = listenPort;
//    }

    public int getSoBacklog() {
        return soBacklog;
    }

    public void setSoBacklog(int soBacklog) {
        this.soBacklog = soBacklog;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public boolean isSoKeepalive() {
        return soKeepalive;
    }

    public void setSoKeepalive(boolean soKeepalive) {
        this.soKeepalive = soKeepalive;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }
    public int getWorkerThread() {
        return Constants.CPUS;
    }

}
