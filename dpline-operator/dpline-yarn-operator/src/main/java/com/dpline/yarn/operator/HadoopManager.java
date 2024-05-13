package com.dpline.yarn.operator;

import com.dpline.common.util.FileUtils;
import com.dpline.common.util.HadoopUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class HadoopManager {

    private static Logger logger = LoggerFactory.getLogger(HadoopManager.class);
    private static ConcurrentHashMap<String,Hadoop> hadoopMap = new ConcurrentHashMap<String,Hadoop>();

    /**
     * create hadoop(configuration/fs/yarnClient)
     *
     * @param clusterID
     * @param homeDir
     * @throws IOException
     */
    public void createHadoop(String clusterID, String homeDir) throws IOException {
        if(hadoopMap.contains(clusterID)){
            logger.info("hadoop already exist, clusterID:{}",clusterID);
            return;
        }
        // check is exits
        if (FileUtils.checkDirExist(homeDir)){
            logger.error("Hadoop home dir [{}] is not exists.",homeDir);
            throw new FileNotFoundException(String.format("Hadoop home dir %s is not exists.",homeDir));
        }
        // homeDir
        Optional<String> hadoopConfDir = null;
        try {
            hadoopConfDir = HadoopUtil.getHadoopConfDir(homeDir);
            hadoopConfDir.ifPresent(configDir -> {
                Hadoop hadoop = new Hadoop();
                Configuration configuration = null;
                try {
                    configuration = HadoopUtil.initHadoopConfig(configDir);
                    hadoop.setConfiguration(configuration);
                    hadoop.setFs(HadoopUtil.createFileSystem(configuration));
                    YarnClient yarnClient = HadoopUtil.createYarnClient(configuration);
                    hadoop.setYarnClient(yarnClient);
                    yarnClient.start();
                    hadoopMap.putIfAbsent(clusterID,hadoop);
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * update hadoop env
     *
     * @param clusterId
     * @param configDir
     * @throws IOException
     */
    public void updateHadoop(String clusterId,String configDir) throws IOException {
        if(hadoopMap.contains(clusterId)){
           closeHadoop(clusterId);
           hadoopMap.remove(clusterId);
        }
        createHadoop(clusterId,configDir);
    }

    /**
     * close hadoop
     * @param clusterId
     */
    public void closeHadoop(String clusterId){
        // 关闭hadoop
        Optional.ofNullable(hadoopMap.get(clusterId)).ifPresent(hadoop -> {
            try {
                hadoop.getFs().close();
                hadoop.getYarnClient().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        hadoopMap.remove(clusterId);
    }

    public static Optional<Hadoop> getHadoop(String clusterId){
        return Optional.ofNullable(hadoopMap.get(clusterId));
    }

}
