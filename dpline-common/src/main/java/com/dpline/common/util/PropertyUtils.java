package com.dpline.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.dpline.common.Constants.COMMON_PROPERTIES_PATH;

/**
 * 加载配置文件
 */
public class PropertyUtils {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

    private static final Properties properties = new Properties();


    static {
        loadPropertyFile(COMMON_PROPERTIES_PATH);
    }

    /**
     * @param commonPropertiesPath
     */
    private static synchronized void loadPropertyFile(String... commonPropertiesPath) {
        //先加载系统配置
        System.getProperties().forEach((key,value)->{
            final String kStr = String.valueOf(key);
            logger.info("Overriding property from system property: {}", kStr);
            properties.setProperty(kStr,String.valueOf(value));
        });
        // 再加载自定义配置
        for (String proPath:commonPropertiesPath) {
            InputStream resourceAsStream = PropertyUtils.class.getResourceAsStream(proPath);
            try {
                properties.load(resourceAsStream);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                System.exit(1);
            }
        }
    }


    /**
     * get property value with upper case
     *
     * @param key property name
     * @return property value  with upper case
     */
    public static String getUpperCaseString(String key) {
        return properties.getProperty(key.trim()).toUpperCase();
    }

    /**
     * get property value with lower case
     *
     * @param key
     * @return
     */
    public static String getLowerCaseString(String key) {
        return properties.getProperty(key.trim()).toLowerCase(Locale.ROOT);
    }

    public static String getProperty(String key) {
        return properties.getProperty(key.trim());
    }

    public static String getProperty(String key,String defaultValue) {
        return properties.getProperty(key.trim(),defaultValue);
    }

    /**
     * get property value
     *
     * @param key property name
     * @return get property int value , if key == null, then return -1
     */
    public static int getInt(String key) {
        return getInt(key, -1);
    }

    /**
     * @param key key
     * @param defaultValue default value
     * @return property value
     */
    public static int getInt(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.info(e.getMessage(), e);
        }
        return defaultValue;
    }

    public static String getPathFromEnv(String envKey) {
        return Optional.ofNullable(System.getenv(envKey)).orElse(System.getProperty(envKey));
    }

    public static Boolean getBoolean(String key, Boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            logger.info(e.getMessage(), e);
        }
        return defaultValue;
    }

    public static Map<String, String> getPrefixedProperties(String prefix) {
        Map<String, String> matchedProperties = new HashMap<>();
        for (String propName : properties.stringPropertyNames()) {
            if (propName.startsWith(prefix)) {
                matchedProperties.put(propName, properties.getProperty(propName));
            }
        }
        return matchedProperties;
    }

}
