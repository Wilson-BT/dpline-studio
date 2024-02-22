package com.handsome.console.handler;

import com.handsome.common.enums.ResFsType;
import com.handsome.common.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


public class CustomCodeTaskDeployHandler extends TaskDeployHandler {

    public static final String CODE_POD_TEM_NAME = "code-pod-template.yaml";

    public static Logger logger = LoggerFactory.getLogger(CustomCodeTaskDeployHandler.class);

    @Override
    public boolean createLocalDirAndJars(String mainJarPath,String sqlText, String taskDeployDir) {
        String localMainFilePath = taskDeployDir + "/" + MAIN_FILE_KEY;
        try {
            FileUtils.createDir(localMainFilePath, ResFsType.LOCAL);
            FileUtils.copyFileToDirectory(new File(mainJarPath),new File(localMainFilePath));
            logger.info("[{}] has been created.", taskDeployDir);
            return true;
        } catch (IOException e) {
            logger.error("Local file generate failed,{}",e.toString());
            try {
                FileUtils.deleteDirectory(new File(localMainFilePath));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return false;
    }
}
