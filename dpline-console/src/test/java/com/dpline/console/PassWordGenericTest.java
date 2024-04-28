package com.dpline.console;

import com.dpline.common.util.EncryptionUtils;
import org.junit.Test;

public class PassWordGenericTest {

    @Test
    public void getPassWord(){
        String password="123456";
        String md5 = EncryptionUtils.getMd5(password);
        System.out.println(md5);
    }
}
