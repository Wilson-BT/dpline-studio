package com.dpline.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class YamlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(YamlReader.class);

    private YamlReader() {
    }

    public static YamlReader getInstance() {
        return EnumHolder.INSTANCE.getYamlReader();
    }

    public Map<String, Object> read(String filePath) {
        Yaml yaml = new Yaml();
        Map<String, Object> properties = new HashMap<>();
        InputStream in = null;
        try {
            in = new FileInputStream(filePath);
            properties = yaml.loadAs(in, HashMap.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return properties;
    }

    public Map<String, Object> readText(String text) {
        Yaml yaml = new Yaml();
        if(StringUtils.isBlank(text)){
            return null;
        }
        Map<String, Object> properties = new HashMap<>();
        properties = yaml.load(text);
        return properties;
    }

    public Map<String, Object> read(File file) {
        InputStream in = null;
        Yaml yaml = new Yaml();
        Map<String, Object> properties = new HashMap<>();
        try {
            in = new FileInputStream(file);
            properties = yaml.loadAs(in, HashMap.class);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return properties;
    }

    public Map<String, Object> read(Class clz, String fileName) {
        InputStream in = null;
        Yaml yaml = new Yaml();
        Map<String, Object> properties = new HashMap<>();
        try {
            in = clz.getClassLoader().getResourceAsStream(fileName);
            properties = yaml.loadAs(in, HashMap.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return properties;
    }

    private enum EnumHolder {
        INSTANCE;
        private YamlReader instance;

        EnumHolder() {
            this.instance = new YamlReader();
        }

        private YamlReader getYamlReader() {
            return instance;
        }
    }
}
