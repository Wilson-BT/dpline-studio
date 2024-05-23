package com.dpline.console.handler;

import com.dpline.dao.entity.Job;

import java.io.IOException;

public interface DeployHandler {

    public static final String MAIN_FLAG = "Main";

    public static final String EXTEND_FLAG = "Extend";

    public static final String SQL_FLAG = "Sql";


    boolean deploy(Job job) throws Exception;

    void clear(Job job) throws IOException;
}
