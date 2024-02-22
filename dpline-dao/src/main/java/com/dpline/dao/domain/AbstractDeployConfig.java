package com.dpline.dao.domain;


import com.dpline.common.util.TaskPathResolver;
import lombok.Data;

/**
 *
 */
@Data
public abstract class AbstractDeployConfig {

    long projectId;

    long jobId;

    long motorVersionId;

    private static final String SQL_FILE_NAME = "dpline-flink-app.sql";

    private static final String MAIN_FILE_PATH = "%s/main";

    private static final String EXTENDED_FILE_PATH = "%s/extended";

    private static final String SQL_FILE_PATH = "%s/sql";

    public String getTaskLocalPath(){
        return TaskPathResolver.getTaskLocalDeployDir(projectId, jobId);
    }

    public String getTaskRemotePath(){
        return TaskPathResolver.getTaskRemoteDeployDir(projectId,jobId);
    }

    /**
     * @return main 文件的位置
     */
    public String mainFilePath(){
        return String.format(MAIN_FILE_PATH,getTaskLocalPath());
    }

    public String mainRemoteFilePath(){
        return String.format(MAIN_FILE_PATH,getTaskRemotePath());
    }


    /**
     * @return 扩展文件路径
     */
    public String extendedFilePath(){
        return String.format(EXTENDED_FILE_PATH,getTaskLocalPath());
    }

    public String extendedRemoteFilePath(){
        return String.format(EXTENDED_FILE_PATH,getTaskRemotePath());
    }

    /**
     * @return sql 文件路径
     */
    public String sqlFilePath(){
        return String.format(SQL_FILE_PATH,getTaskLocalPath());
    }

    /**
     * @return sql 文件路径
     */
    public String sqlRemoteFilePath(){
        return String.format(SQL_FILE_PATH,getTaskRemotePath());
    }

    /**
     * @return sql 文件绝对路径名
     */
    public String sqlFileAbsoluteName(){
        return sqlFilePath() + "/" + SQL_FILE_NAME;
    }

}
