package com.dpline.yarn.operator.util;

import com.dpline.common.util.FileUtils;
import com.dpline.common.util.PropertyUtils;
import com.dpline.common.util.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import javax.xml.bind.PropertyException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class HadoopUtil {

    /**
     * HADOOP_CONF_DIR
     */
    private static String HADOOP_CONF_DIR = "HADOOP_CONF_DIR";

    /**
     * CONF_SUFFIX
     */
    private static String CONF_SUFFIX = "/etc/hadoop";

    /**
     * HADOOP_HOME key
     */
    private static String HADOOP_HOME = "HADOOP_HOME";

    private static final List<String> CONFIG_FILE_LIST = Arrays.asList("hdfs-default.xml", "core-site.xml", "hdfs-site.xml", "yarn-site.xml");

    /**
     * Get hadoop configuration
     * @return
     * @throws PropertyException
     */
    private static String getHadoopConfDir() throws PropertyException {
        String hadoopConfDir = PropertyUtils.getPathFromEnv(HADOOP_CONF_DIR);
        if(StringUtils.isEmpty(hadoopConfDir)){
            return FileUtils.resolvePath(PropertyUtils.getPathFromEnv(HADOOP_HOME),CONF_SUFFIX);
        }
        return hadoopConfDir;
    }

    /**
     * init hadoop config
     *
     * @param configDir
     * @return
     * @throws FileNotFoundException
     */
    public static Configuration initHadoopConfig(String configDir) throws FileNotFoundException {
        HadoopConfiguration hadoopConfig = new HadoopConfiguration();
        if(!FileUtils.checkDirExist(configDir)){
            throw new FileNotFoundException(String.format("Directory %s do not exist", configDir));
        }
        Arrays.stream(FileUtils.listFiles(configDir)).filter(file -> CONFIG_FILE_LIST.contains(file)).forEach(file -> {
            hadoopConfig.addResource(new Path(file.getAbsolutePath()));
        });
        if (StringUtils.isBlank(hadoopConfig.get("hadoop.tmp.dir"))) {
            hadoopConfig.set("hadoop.tmp.dir", "/tmp");
        }
        if (StringUtils.isBlank(hadoopConfig.get("hbase.fs.tmp.dir"))) {
            hadoopConfig.set("hbase.fs.tmp.dir", "/tmp");
        }
        // disable timeline service as we only query yarn app here.
        // Otherwise we may hit this kind of ERROR:
        // java.lang.ClassNotFoundException: com.sun.jersey.api.client.config.ClientConfig
        hadoopConfig.set("yarn.timeline-service.enabled", "false");
        hadoopConfig.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        hadoopConfig.set("fs.file.impl", LocalFileSystem.class.getName());
        hadoopConfig.set("fs.hdfs.impl.disable.cache", "true");
        return hadoopConfig;
    }

    /**
     * Get Hadoop FileSystem
     * @param configuration
     * @return
     * @throws IOException
     */
    public static FileSystem createFileSystem(Configuration configuration) throws IOException {
        return FileSystem.get(configuration);
    }

    /**
     * Get YarnClient
     * @param configuration
     * @return
     * @throws FileNotFoundException
     */
    public static YarnClient createYarnClient(Configuration configuration) throws FileNotFoundException {
        YarnClient yarnClient = YarnClient.createYarnClient();
        YarnConfiguration yarnConfiguration = new YarnConfiguration(configuration);
        yarnClient.init(yarnConfiguration);
        return yarnClient;
    }


    public static class HadoopConfiguration extends Configuration {

        private final List<String> rewriteNames = Arrays.asList(
                "dfs.blockreport.initialDelay",
                "dfs.datanode.directoryscan.interval",
                "dfs.heartbeat.interval",
                "dfs.namenode.decommission.interval",
                "dfs.namenode.replication.interval",
                "dfs.namenode.checkpoint.period",
                "dfs.namenode.checkpoint.check.period",
                "dfs.client.datanode-restart.timeout",
                "dfs.ha.log-roll.period",
                "dfs.ha.tail-edits.period",
                "dfs.datanode.bp-ready.timeout"
        );

        private String getHexDigits(String value) {
            boolean negative = false;
            String str = value;
            String hexString = null;
            if (value.startsWith("-")) {
                negative = true;
                str = value.substring(1);
            }
            if (str.startsWith("0x") || str.startsWith("0X")) {
                hexString = str.substring(2);
                if (negative) {
                    hexString = "-" + hexString;
                }
                return hexString;
            }
            return null;
        }

        private String getSafeValue(String name) {
            String value = getTrimmed(name);
            if (rewriteNames.contains(name)) {
                return value.replaceAll("s$", "");
            }
            return value;
        }

        @Override
        public long getLong(String name, long defaultValue) {
            String valueString = getSafeValue(name);
            if (valueString == null) {
                return defaultValue;
            }
            String hexString = getHexDigits(valueString);
            if (hexString != null) {
                return Long.parseLong(hexString, 16);
            }
            return Long.parseLong(valueString);
        }
    }

}
