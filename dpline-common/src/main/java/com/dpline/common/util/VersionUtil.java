package com.dpline.common.util;

import cn.hutool.core.convert.Convert;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class VersionUtil {
    public static final String INIT_VERSION = "1.0";
    public static final String INIT_VERSION_PLUS = "v1";
    public static final int NUM_MAX = 10;
    public static final String VERSION_PRE = "v";
    public static final String VERSION_FORMAT = "v%s";

    public static final Pattern VERSION_PATTERN = Pattern.compile("^v\\d+$");
    /**
     * 升级版本号,格式：V1
     *
     * @param version
     * @return
     */
    public static String increaseVersion(String version) {
        if (StringUtils.isBlank(version)) {
            return INIT_VERSION_PLUS;
        }
        String versionNum = StringUtils.substringAfter(version, VERSION_PRE);
        Integer versionInt = Convert.toInt(versionNum);
        String newVersion = String.format(VERSION_FORMAT, ++versionInt);
        return newVersion;
    }

    public static String fallbackVersion(String version) {
        if(INIT_VERSION_PLUS.equals(version)){
            return INIT_VERSION_PLUS;
        }
        String versionNum = StringUtils.substringAfter(version, VERSION_PRE);
        Integer versionInt = Convert.toInt(versionNum);
        String newVersion = String.format(VERSION_FORMAT, --versionInt);
        return newVersion;
    }
    /**
     * 升级版本号,格式：1.0
     *
     * @param versionNum
     * @return
     */
    public static String upgradeVersion(String versionNum) {
        if (StringUtils.isBlank(versionNum)) {
            return INIT_VERSION;
        }
        List<String> verStr = Splitter.on(".").splitToList(versionNum);
        Integer[] nums = new Integer[verStr.size()];
        for (int i = 0; i < verStr.size(); i++) {
            nums[i] = Convert.toInt(verStr.get(i));
        }
        // 递归递增
        upgradeVersionNum(nums,nums.length-1);
        return Joiner.on(".").join(nums);
    }

    private static void upgradeVersionNum(Integer[] nums,int index) {
        if (index == 0) {
            nums[0] = nums[0] + 1;
        } else {
            int value = nums[index] + 1;
            if (value < NUM_MAX) {
                nums[index] = value;
            } else {
                nums[index] = 0;
                upgradeVersionNum(nums, index - 1);
            }
        }
    }

    public static Integer findMaxVersion(List<String> fileTags) {
        // 将所有的版本号提取出来
        AtomicInteger MAX_VERSION = new AtomicInteger(0);
        fileTags.forEach(fileTag -> {
            if (VERSION_PATTERN.matcher(fileTag).matches()) {
                String version = StringUtils.substringAfter(fileTag, VERSION_PRE);
                Integer versionInt = Convert.toInt(version);
                if (versionInt > MAX_VERSION.get()) {
                    MAX_VERSION.set(versionInt);
                }
            }
        });
        return MAX_VERSION.get();



    }
}
