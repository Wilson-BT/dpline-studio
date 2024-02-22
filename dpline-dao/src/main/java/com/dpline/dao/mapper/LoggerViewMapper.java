package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.common.log.InsertMessage;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@DS("doris")
public interface LoggerViewMapper extends GenericMapper<InsertMessage,String> {

    public ArrayList<InsertMessage> loggerViewFromOperatorTime(@Param("startTime") Long timeStampStart,
                                                               @Param("jobName") String jobName,
                                                               @Param("containerType") String containerType,
                                                               @Param("limitCount") Integer limitCount
    );


    public ArrayList<InsertMessage> logTail(@Param("jobName") String jobName,
                                     @Param("containerType") String containerType,
                                     @Param("lastStartTime") Long lastStartTime,
                                     @Param("limitCount") Integer limitCount);

    public ArrayList<InsertMessage> loggerViewToOperateTime(@Param("endTime") Long endTime,
                                                               @Param("jobName") String jobName,
                                                               @Param("containerType") String containerType,
                                                               @Param("limitCount") Integer limitCount
    );
}
