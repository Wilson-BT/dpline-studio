package com.dpline.store;

import com.dpline.common.store.HdfsStore;
import org.junit.Test;

import java.io.IOException;

public class MinioTest {

    @Test
    public void test() {
        HdfsStore hdfsStore = new HdfsStore();
        try {
            hdfsStore.upload("C:\\Users\\wangchunshun\\Desktop\\Document\\flink-1.16.3\\examples\\batch\\WordCount.jar", "/dpline/upload/jar/13431396471104/13534692796864/13565253555008/WordCount.jar", false, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteTest(){
        HdfsStore hdfsStore = new HdfsStore();
        try {
            hdfsStore.delete("/xxx/dsgdsfr", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *
     */
    @Test
    public void copyTest(){
        HdfsStore hdfsStore = new HdfsStore();
        try {
            hdfsStore.copy("/xxx/dsgdsfr/xxx.jar","/nihao/dpline-dao-0.0.4.jar",false, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void downloadTest(){
        HdfsStore hdfsStore = new HdfsStore();
        try {
            hdfsStore.download("/nihao/dpline-dao-0.0.4.jar","C:\\Users\\wangchunshun\\Desktop\\xxx.jar",false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void mkdirTest(){
        HdfsStore hdfsStore = new HdfsStore();
        try {
            hdfsStore.mkdir("/wangchunshun/hiehie");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
