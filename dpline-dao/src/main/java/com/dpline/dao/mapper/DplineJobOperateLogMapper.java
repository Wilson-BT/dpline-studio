package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.DplineJobOperateLog;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@DS("mysql")
public interface DplineJobOperateLogMapper extends GenericMapper<DplineJobOperateLog, Long> {

    Long queryLastOperateTime(@Param("jobId") Long jobId);


    ArrayList<DplineJobOperateLog> queryJobOperateHistory(Pagination<DplineJobOperateLog> pagination);

    Long getLastStartTime(@Param("jobId") Long jobId);
}
