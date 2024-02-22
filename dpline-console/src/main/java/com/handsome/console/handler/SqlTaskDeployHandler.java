package com.handsome.console.handler;

import com.handsome.common.enums.ResFsType;
import com.handsome.common.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class SqlTaskDeployHandler extends TaskDeployHandler {

    private Logger logger = LoggerFactory.getLogger(SqlTaskDeployHandler.class);

    private static final String SQL_FILE_NAME = "dpline-app.sql";

    public static final String EXTENDED_JARS = "extended-jars";

    public static final String SQL_POD_TEM_NAME = "sql-pod-template.yaml";

    @Override
    public boolean createLocalDirAndJars(String mainJarPath,String sqlText, String taskDeployDir) {
        String localMainFilePath = taskDeployDir + "/" + MAIN_FILE_KEY;
        try {
            FileUtils.createDir(localMainFilePath, ResFsType.LOCAL);
            // sql 写入本地文件
            FileUtils.writeContent2File(sqlText,localMainFilePath + "/" + SQL_FILE_NAME);
            // TODO 如果有udf，将 udf move 到本地udf目录下
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
