package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.JobSavepoint;
import com.dpline.dao.generic.GenericMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("mysql")
@Repository
public interface JobSavepointMapper extends GenericMapper<JobSavepoint,Long> {

    List<JobSavepoint> selectByJobId(@Param("jobId") Long jobId);


    Integer deleteSavePointByJobId(@Param("jobId") Long jobId);
}
