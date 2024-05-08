package com.dpline.yarn;

import com.dpline.yarn.operator.HadoopManager;
import org.junit.Test;


public class HadoopTest {

    @Test
    public void hadoopTest() throws Exception {
        HadoopManager hadoopManager = new HadoopManager();
        hadoopManager.createHadoop("1", "D:\\Application\\hadoop\\etc\\hadoop");
        hadoopManager.closeHadoop("1");

    }
}
