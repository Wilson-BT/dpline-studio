package com.handsome.common.utiltest;

import com.handsome.common.util.MinioUtils;
import org.junit.Test;

import java.io.IOException;

public class MinioTest {

    @Test
    public void copyTest(){
        try {
//            MinioUtils.getInstance().copyLocalToMino("/tmp/upload","/wangchunshun",false,true);
            MinioUtils.getInstance().mkdir("/test/checkpoints/5483260672288/flinkSqlTest/00000000000000000000000000000000/chk111");
//            MinioUtils.getInstance().copyLocalToMino("/tmp/upload/id_rsa","/wangchunshun/wang",false,true);
//            MinioUtils.getInstance().delete("/wangchunshun/wang",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
