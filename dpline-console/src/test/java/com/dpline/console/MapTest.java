package com.dpline.console;

import org.junit.Test;

import java.util.HashMap;

public class MapTest {

    @Test
    public void mapValueReplace(){
        HashMap<String, String> otherRunTimeConfigMap = new HashMap<>();
        otherRunTimeConfigMap.put("hhhh","${jobName}");
        otherRunTimeConfigMap.forEach(
            (key,value) -> {
                if(value.contains("${jobName}")){
                    otherRunTimeConfigMap.put(key,value.toString().replace("${jobName}","xxxxxxxxxx"));
                }
            }
        );
        System.out.println(otherRunTimeConfigMap.toString());
    }
}
