package com.handsome.flink.core.util;

import com.handsome.common.options.FlinkHomeOptions;
import com.handsome.common.util.Asserts;
import com.handsome.common.util.TaskPath;
import com.handsome.flink.core.ChildFirstClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FlinkVersionClassLoader {


    private static final ConcurrentHashMap<String, ClassLoader> clientClassLoaderCache = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(FlinkVersionClassLoader.class);

    private final Pattern flinkVersionPattern = Pattern.compile("^(\\d+\\.\\d+)(\\.)?.*$");

    private static final String FLINK_VERSION_CLIENT_JAR = "dpline-flink-client-%s";

    /**
     * 创建flink 版本的 Class loader
     *
     * @param flinkHomeOptions
     * @return ClassLoader
     */
    public ClassLoader getFlinkClientClassLoader(FlinkHomeOptions flinkHomeOptions) {
        // 获取flink home 下的 所有jar包
        List<URL> flinkHomeJars = getFlinkHomeJars(
            TaskPath.getLocalFlinkLibPath(
                flinkHomeOptions.getFlinkPath()
            )
        );
        logger.info("Flink jar has been add find, [{}]",flinkHomeJars.toString());
        // 获取项目下的 Flink 插件包
        String realVersion = flinkHomeOptions.getRealVersion();
        List<URL> appFlinkClientJars = getAppFlinkClientJars(TaskPath.getLocalProjectHomeLibPath(),
            String.format(FLINK_VERSION_CLIENT_JAR, getMajorVersion(realVersion)));
        logger.info("Flink client jar has been find, [{}]",appFlinkClientJars.toString());
        flinkHomeJars.addAll(appFlinkClientJars);
        // TODO 使用 udf jar 包
        // TODO 使用 扩展的 connector jar 包
        return clientClassLoaderCache.computeIfAbsent(
            realVersion,
            new Function<String, ClassLoader>() {
                @Override
                public ClassLoader apply(String s) {
                    return new ChildFirstClassLoader(
                        flinkHomeJars.toArray(new URL[flinkHomeJars.size()]),
                        Thread.currentThread().getContextClassLoader()
                    );
                }
            });
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

    private List<URL> getFlinkHomeJars(String flinkPath) {
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
