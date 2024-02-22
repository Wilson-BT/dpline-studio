package com.dpline.console;


import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {

    @Test
    public void patternTest(){
        Pattern REGEX_USER_NAME = Pattern.compile("checkpoints/([a-zA-Z0-9_-]{3,20})/([a-zA-Z0-9_-]{3,20})/([a-zA-Z0-9_-]{3,40})/chk-([a-zA-Z0-9_-]+)/([a-zA-Z0-9_-]+)");
        String path = "checkpoints/111111111111111/11111123435435436/00000000000000000000000000000000/chk-13938/104b4f80-64a7-4656-892b-318252a8a64f";
        // 使用path
        Matcher matcher = REGEX_USER_NAME.matcher(path);
        if(matcher.find()){
            System.out.println(matcher.group(4));
        }
//        System.out.println(path.substring(0,path.lastIndexOf("/")));
    }

}
