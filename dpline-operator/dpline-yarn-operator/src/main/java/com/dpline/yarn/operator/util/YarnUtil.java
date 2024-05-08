package com.dpline.yarn.operator.util;

import com.dpline.common.Constants;
import com.dpline.common.util.HttpUtils;
import com.dpline.common.util.StringUtils;
import com.dpline.yarn.operator.HadoopManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.RMHAUtils;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class YarnUtil {

    /**
     * Get application state from yarn
     * @param appId
     * @param clusterId
     * @return
     */
    public YarnApplicationState getState(String appId,String clusterId) {
        ApplicationId applicationId = ApplicationId.fromString(appId);
        YarnApplicationState state = null;
        try {
            YarnClient yarnClient = HadoopManager.getHadoop(clusterId).map(x -> x.getYarnClient()).orElse(null);
            YarnApplicationState reportState = yarnClient.getApplicationReport(applicationId).getYarnApplicationState();
            state = reportState;
        } catch (YarnException | IOException e) {
            e.printStackTrace();
        }
        return state;
    }


    public static void killApplication(String appId,String clusterId) {
            ApplicationId applicationId = ApplicationId.fromString(appId);
            try {
                YarnClient yarnClient = HadoopManager.getHadoop(clusterId).map(x -> x.getYarnClient()).orElse(null);
                yarnClient.killApplication(applicationId);
            } catch (YarnException | IOException e) {
                e.printStackTrace();
            }
    }

    public static boolean isContains(String appName,String clusterId) {
        YarnClient yarnClient = HadoopManager.getHadoop(clusterId).map(x -> x.getYarnClient()).orElse(null);
        EnumSet<YarnApplicationState> runningStates = EnumSet.of(YarnApplicationState.RUNNING);
        List<ApplicationReport> runningApps = null;
        try {
            runningApps =  yarnClient.getApplications(runningStates);
        } catch (YarnException | IOException e) {
            throw new RuntimeException(e);
        }

        if (runningApps != null && !runningApps.isEmpty()) {
            for (ApplicationReport app : runningApps) {
                if (app.getName().equals(appName)) {
                    return true;
                }
            }
        }
        return false;
    }
    // 获取 url / 请求url

    public static synchronized String getYarnRestUrl(String clusterId) {
        // 获取yarn 的 rest url
        Configuration configuration = HadoopManager.getHadoop(clusterId).map(x -> x.getConfiguration()).get();
        boolean useHttps = YarnConfiguration.useHttps(configuration);
        String addressPrefix;
        int defaultPort;
        String protocol;
        String rmHttpURL = "";
        if (useHttps){
            addressPrefix = YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS;
            defaultPort = 8090;
            protocol = Constants.HTTPS_SCHEMA;
        } else {
            addressPrefix = YarnConfiguration.RM_WEBAPP_ADDRESS;
            defaultPort = 8088;
            protocol = Constants.HTTP_SCHEMA;
        }
        String proxy = configuration.get("yarn.web-proxy.address");
        if (proxy != null) {
            rmHttpURL = protocol + proxy;
        } else {
            rmHttpURL = HAUtil.isHAEnabled(configuration) ?
                    getRMHttpURLWithHA(configuration, addressPrefix, defaultPort, protocol) :
                    getRMHttpURLWithoutHA(configuration, addressPrefix, defaultPort, protocol);
        }

        return rmHttpURL;
    }

    /**
     * Get yarn http url without HA
     * @param configuration
     * @param addressPrefix
     * @param defaultPort
     * @param protocol
     * @return
     */
    private static String getRMHttpURLWithoutHA(Configuration configuration, String addressPrefix, int defaultPort, String protocol) {
        InetSocketAddress connectAddress = NetUtils.getConnectAddress(
                configuration.getSocketAddr(addressPrefix, "0.0.0.0:" + defaultPort, defaultPort)
        );
        InetAddress address = connectAddress.getAddress();
        StringBuilder sb = new StringBuilder(protocol);
        if(address == null || address.isAnyLocalAddress() || address.isLoopbackAddress()){
            try {
                String canonicalHostName = InetAddress.getLocalHost().getCanonicalHostName();
                sb.append(canonicalHostName);
            } catch (Exception e) {
                sb.append(address.getHostName());
            }
        } else {
            sb.append(address.getHostName());
        }
        return sb.append(":")
                .append(connectAddress.getPort())
                .toString();
    }

    /**
     * Get yarn http url with HA3
     * @param configuration
     * @param addressPrefix
     * @param defaultPort
     * @param protocol
     * @return
     */
    private static String getRMHttpURLWithHA(Configuration configuration, String addressPrefix, int defaultPort, String protocol) {
        YarnConfiguration yarnConfiguration = new YarnConfiguration(configuration);
        Optional<String> activeRMHAIdOptional = Optional.ofNullable(RMHAUtils.findActiveRMHAId(yarnConfiguration));
        String activeRMId = activeRMHAIdOptional.orElseGet(() -> {
            List<String> rmhaIds = HAUtil.getRMHAIds(configuration).stream().collect(Collectors.toList());
            HashMap<String, String> idUrlMap = new HashMap<>();
            rmhaIds.stream().forEach(id -> {
                    String address = configuration.get(HAUtil.addSuffix(addressPrefix, id));
                    // 如果是空
                    if (StringUtils.isBlank(address)) {
                        String hostname = configuration.get(HAUtil.addSuffix("yarn.resourcemanager.hostname", id));
                        address = hostname + Constants.COLON + defaultPort;
                    }
                    // key：url
                    // value ：resourceManagerId
                    idUrlMap.put(protocol + address, id);
            });
            int rpcTimeoutForChecks = yarnConfiguration.getInt(
                    CommonConfigurationKeys.HA_FC_CLI_CHECK_TIMEOUT_KEY,
                    CommonConfigurationKeys.HA_FC_CLI_CHECK_TIMEOUT_DEFAULT);
            AtomicReference<String> activeRmId = null;
            idUrlMap.forEach((key, value) -> {
                String active = httpTestYarnRMUrl(key, rpcTimeoutForChecks);
                if (!StringUtils.isBlank(active)) {
                    activeRmId.set(idUrlMap.get(key));
                }
            });
            return activeRmId.get();
        });
        return activeRMId;
    }

    private static String httpTestYarnRMUrl(String url, int rpcTimeoutForChecks) {
        RequestConfig config = RequestConfig
                .custom()
                .setConnectTimeout(rpcTimeoutForChecks,TimeUnit.MILLISECONDS)
                .build();
        try {
            return HttpUtils.doGetWithConfig(new URIBuilder(url).build(),config);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }



}
