package com.dpline.common.util;

import com.dpline.common.enums.ResFsType;
import com.dpline.common.enums.Status;
import com.dpline.common.store.FsStore;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * flink 工具类
 */
public class FlinkUtils {

    private static final Logger logger = LoggerFactory.getLogger(FlinkUtils.class);

    public static final Pattern flinkHomeNamePattern = Pattern.compile("flink-(\\d+\\.\\d+)(\\.)?(\\d+)?.*$");

    public static final Pattern flinkVersionCmdPattern = Pattern.compile("Version: (\\d+\\.\\d+)(\\.)?(\\d+)?.*$");

    public static Optional<String> getRealVersionFromHomePath(String flinkPath) {
        String flinkPathName = flinkPath.substring(flinkPath.lastIndexOf("/") + 1);
        if(FsStore.WINDOWS){
            flinkPathName =  flinkPath.substring(flinkPath.lastIndexOf("\\") + 1);
        }
        String version = strLine2Version(flinkHomeNamePattern, flinkPathName);
        return Optional.of(version);
    }

    /**
     * 是否使用命令行判断
     *
     * @param flinkPath
     * @param homeName
     * @return
     */
    public static Optional<String> getRealVersionFromFlinkHome(String flinkPath, boolean homeName) {
        if (StringUtils.isEmpty(flinkPath)) {
            return Optional.empty();
        }
        if (homeName) {
            return getRealVersionFromHomePath(flinkPath);
        }
        return getRealVersionFromHomeCmd(flinkPath);
    }

    /**
     * 通过命令行获取到flink的版本信息
     * @param flinkPath
     * @return
     */
    private static Optional<String> getRealVersionFromHomeCmd(String flinkPath) {
        // 通过命令行获取. 使用什么方式进行处理
        assert flinkPath != null;
        // 判断文件是否存在,如果不存在的话，可以选择
        String flinkCmdPath = String.format("%sbin", flinkPath.endsWith("/") ? flinkPath : flinkPath + "/") + "/flink";
        String cmd = flinkCmdPath + " --version";
        try {
            if (!FileUtils.checkFileExist(flinkCmdPath, ResFsType.LOCAL)){
                logger.error(Status.FLINK_CLIENT_NOT_EXISTS.getMsg());
                return Optional.empty();
            }
            int status;
            String version = null;
            Process pipeline = Runtime.getRuntime().exec(cmd);
            status = pipeline.waitFor();
            if (status != 0) {
                logger.error("Cmd [{}] exec failed.", cmd);
                return Optional.empty();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(pipeline.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Version")) {
                    version = strLine2Version(flinkVersionCmdPattern, line);
                }
            }
            return Optional.ofNullable(version);
        } catch (InterruptedException | IOException e) {
            logger.error("Cmd [{}] exec failed.because of {}", cmd, e.getMessage());
            return Optional.empty();
        }
    }

    private static String strLine2Version(Pattern flinkVersionPattern, String line) {
        StringBuilder flinkVersion = new StringBuilder();
        Matcher matcher = flinkVersionPattern.matcher(line);
        if (matcher.find()) {
            IntStream range = IntStream.range(1, matcher.groupCount() + 1);
            range.forEach((x) -> {
                flinkVersion.append(matcher.group(x));
            });
        }
        return flinkVersion.toString();
    }
}
