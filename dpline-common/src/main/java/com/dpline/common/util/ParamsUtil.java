package com.dpline.common.util;

import com.dpline.common.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 将 字符串参数 转换为 String 类型的 数组
 */
public class ParamsUtil {

    public static List<String> getParams(String params) {
        if (params == null || params.trim().length() == 0) {
            return null;
        }
        ArrayList<String> stringArrayList = new ArrayList<>();
        // 如果参数中包含引号，需要找到下一个引号，将引号内的内容作为一个参数
        StringBuilder sb = new StringBuilder("");
        for (int index = 0; index < params.length(); index++) {
            char c = params.charAt(index);
            // 如果是空格,且sb不为空
            if(Constants.SPACE.charAt(0) == c && StringUtils.isNotEmpty(sb)){
                // 如果开头是 引号，本次不是以引号结尾，说明，后面可能还会有参数，所以，不截断
                if(sb.charAt(0) == '\"' && sb.charAt(sb.length() -1) != '\"'){
                    sb.append(c);
                } else {
                    stringArrayList.add(sb.toString());
                    sb = new StringBuilder("");
                }
                continue;
            }
            sb.append(c);
        }
        if(StringUtils.isNotEmpty(sb)){
            stringArrayList.add(sb.toString());
        }
        return stringArrayList.stream().map(str ->{
            if (str.startsWith("\"") && str.endsWith("\"")) {
                str = str.substring(1, str.length() - 1).trim();
            }
            return str;
        }).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        String arg = "--adag dsgrsrh --dgertresgv \"faegteg adgeg\" --dsshr dge ";
        List<String> params = getParams(arg);
        System.out.println(params);
    }

}
