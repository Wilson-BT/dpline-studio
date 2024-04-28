package com.dpline.flink.core.util;

import com.dpline.common.request.FlinkRequest;
import com.dpline.common.request.JarResource;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.CollectionUtils;
import com.dpline.common.util.StringUtils;
import com.dpline.common.util.TaskPathResolver;
import com.dpline.flink.core.ChildFirstClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FlinkVersionClassLoader {


//    private static final ConcurrentHashMap<String, ChildFirstClassLoader> clientClassLoaderCache = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(FlinkVersionClassLoader.class);

    private final Pattern flinkVersionPattern = Pattern.compile("^(\\d+\\.\\d+)(\\.)?.*$");

    private static final String FLINK_VERSION_CLIENT_JAR = "dpline-flink-client-%s";

    /**
     * 创建flink 版本的 Class loader
     * @param flinkRequest
     * @return ClassLoader
     */
    public ChildFirstClassLoader getFlinkClientClassLoader(FlinkRequest flinkRequest) {
        // 获取flink home 下的 所有jar包
        List<URL> flinkHomeJars = getFlinkJars(
            TaskPathResolver.getLocalFlinkLibPath(
                flinkRequest.getFlinkHomeOptions().getFlinkPath()
            )
        );
        List<URL> flinkOptJars = getFlinkJars(
            TaskPathResolver.getLocalFlinkOptPath(
                flinkRequest.getFlinkHomeOptions().getFlinkPath()
            )
        );
        flinkHomeJars.addAll(flinkOptJars);
        logger.info("Flink home jar has been add find, [{}]",flinkHomeJars.toString());
        // 获取项目下的 Flink 插件包
        String realVersion = flinkRequest.getFlinkHomeOptions().getRealVersion();
        List<URL> appFlinkClientJars = getAppFlinkClientJars(TaskPathResolver.getLocalProjectHomeLibPath(),
            String.format(FLINK_VERSION_CLIENT_JAR, getMajorVersion(realVersion)));
        // only one app flink client jar is here
        if (CollectionUtils.isEmpty(appFlinkClientJars) || appFlinkClientJars.size() != 1){
            logger.error("AppFlinkClientJars.size() is {}",appFlinkClientJars);
            throw new RuntimeException(String.format("AppFlinkClientJars.size() is [%d]",appFlinkClientJars.size()));
        }
        logger.info("Flink client jar has been find, [{}]",appFlinkClientJars.toString());
        flinkHomeJars.addAll(appFlinkClientJars);
        // add connector and udf
        if(CollectionUtils.isNotEmpty(flinkRequest.getExtendedJarResources())){
            List<URL> collect = flinkRequest.getExtendedJarResources().stream().map(extendedJar -> {
                try {
                    return new File(extendedJar.getJarLocalPath()).toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            }).filter(Asserts::isNotNull).collect(Collectors.toList());
            flinkHomeJars.addAll(collect);
        }
        return new ChildFirstClassLoader(
            flinkHomeJars.toArray(new URL[0]),
            Thread.currentThread().getContextClassLoader()
        );
    }

    /**
     * 加载本地目录依赖jar包
     * @param flinkHomeJars
     */
    void getLocalTaskDependJar(List<URL> flinkHomeJars,String localTaskDeployPath){
        if(StringUtils.isEmpty(localTaskDeployPath)){
            logger.warn("No depended jars here.");
            return;
        }
        File file = new File(localTaskDeployPath);
        if(!file.exists()){
            logger.warn("No depended jars here.");
            return;
        }
        List<URL> flinkJars = getFlinkJars(localTaskDeployPath);
        logger.info("Depended jars has been find, [{}]",flinkJars.toString());
        flinkHomeJars.addAll(flinkJars);
    }

    private String getMajorVersion(String realVersion) {
        String flinkShortVersion = "";
        Matcher matcher = flinkVersionPattern.matcher(realVersion);
        if (matcher.matches()) {
            flinkShortVersion = matcher.group(1);
        }
        return flinkShortVersion;
    }

    /**
     * 从 dpline 本地获取包地址
     *
     * @param appLibPath
     * @param flinkMajorVersion
     * @return
     */
    private List<URL> getAppFlinkClientJars(String appLibPath, String flinkMajorVersion) {
        File[] libFiles = new File(appLibPath).listFiles();
        List<URL> flinkCustomClients = new ArrayList<>();
        if (Asserts.isNotNull(libFiles)) {
            flinkCustomClients = Arrays.stream(libFiles)
                .filter(file -> file.getName().contains(flinkMajorVersion))
                .map(file -> {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        }
        return flinkCustomClients;
    }

    private List<URL> getFlinkJars(String flinkPath) {
        File[] flinkFiles = new File(flinkPath).listFiles();
        List<URL> flinkHomeJars = new ArrayList<>();
        if (Asserts.isNotNull(flinkFiles)) {
            flinkHomeJars = Arrays.stream(flinkFiles)
                .filter(file -> !file.getName().startsWith("log4j"))
                .map(file -> {
                    try {
                        return file.toURI().toURL();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
        }
        return flinkHomeJars;
    }
}
