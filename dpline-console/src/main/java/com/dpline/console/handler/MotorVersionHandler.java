package com.dpline.console.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.dpline.common.enums.RunMotorType;
import com.dpline.common.util.Asserts;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MotorVersionHandler {

    private static Logger logger = LoggerFactory.getLogger(MotorVersionHandler.class);

    private static Pattern compile = Pattern.compile("\\$\\{(.*)}");


    public static String parseJarMotorVersion(String jarPath, RunMotorType runMotorType) throws IOException {

        String classPrefix = runMotorType.getClassPrefix();

        String version = null;

        if (!FileUtil.exist(jarPath)) {
            return null;
        }

        // 2.获取符合规则的文件名
        java.util.jar.JarFile jarFile = null;
        jarFile = new java.util.jar.JarFile(jarPath);
        List<String> fileNameList = new ArrayList<>();
        Enumeration<JarEntry> en = jarFile.entries();
        while (en.hasMoreElements()) {
            JarEntry entry = en.nextElement();
            String fileName = entry.getName();
            if (fileName.startsWith("META-INF/maven") && fileName.endsWith("pom.xml")) {
                fileNameList.add(fileName);
            }
        }

        logger.info("获取符合规则的文件名数量: {}", fileNameList.size());
        long starTime = System.currentTimeMillis();
        // 3.遍历获取
        for (String fileName : fileNameList) {
            JarEntry jarEntry = jarFile.getJarEntry(fileName);
            InputStream inputStream = null;
            try {
                inputStream = jarFile.getInputStream(jarEntry);
                MavenXpp3Reader reader = new MavenXpp3Reader();
                Model model = reader.read(inputStream);
                Properties properties = model.getProperties();

                for (Dependency dependency : model.getDependencies()) {
                    if (classPrefix.equals(dependency.getGroupId())) {
                        if(Asserts.isNull(dependency.getVersion())){
                            continue;
                        }
                        Matcher m = compile.matcher(dependency.getVersion());
                        if (m.find()) {
                            String propertyKey = m.group(1);
                            version = properties.getProperty(propertyKey);
                        } else {
                            version = dependency.getVersion();
                        }
                        long endTime = System.currentTimeMillis();
                        logger.info("解析pom获取flink版本耗时: [{}ms]", (endTime - starTime));
                        break;
                    }
                }
            } catch (Exception e){
                logger.error("Jar read error");
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            if (StrUtil.isNotEmpty(version)) {
                break;
            }
        }
        return version;
    }
}
