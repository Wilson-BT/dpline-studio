package com.dpline.minio;

import com.dpline.common.util.TaskPathResolver;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PathsTest {

    @Test
    public void pathTest(){
        String path = "/tmp/hhhhh.text";
        try {
            String log = "nihao,wohao,dajiahao";
            Files.write(Paths.get(path), log.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
