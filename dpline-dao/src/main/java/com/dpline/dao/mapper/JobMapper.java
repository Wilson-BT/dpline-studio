package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.Job;
import com.dpline.dao.entity.Project;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@DS("mysql")
@Repository
public interface JobMapper extends GenericMapper<Job,Long> {


    Job queryJobByFileId(@Param("fileId") Long fileId);

    List<Job> selectByDockerImageId(@Param("dockerImageId") long dockerImageId);

    List<Job> queryJobs(Pagination<Job> pagination);

    List<Job> selectByName(@Param("jobName") String jobName);

    List<Job> selectByFileId(@Param("fileId") Long id);

    List<Job> selectByBatchFileIds(@Param("fileIdList") Collection<Long> fileIdList);

    Job selectById(@Param("id") Long id);

    List<Job> selectByJobStatus(@Param("runningExecStatusList") List<String> runningArray);

//    Integer updateRunJobId(@Param("id") Long id,
//                          @Param("runJobId") String runJobId);

    Integer updateExecStatus(@Param("jobId") long taskId,
                             @Param("execStatus") String execStatus);

    List<Job> selectByNameAndClusterId(@Param("jobName") String jobName,
                                       @Param("clusterId") Long clusterId,
                                       @Param("projectId") Long projectId);

    List<Job> selectByMainJarId(@Param("mainJarId") Long mainJarId);

    List<Job> selectByMainResourceId(@Param("mainResourceId") Long mainResourceId);

    List<Job> selectByResourceId(@Param("mainResourceId") Long mainResourceId,
                                 @Param("motorVersionId") Long motorVersionId);

    Integer updateMainJarId(@Param("jobId") Long jobId,
                         @Param("mainJarId") Long mainJarId);

    List<Job> selectByClusterId(@Param("clusterId") Long clusterId);

    List<Job> selectByAlertInstanceId(@Param("alertInstanceId") Long alertInstanceId);

    Integer updateAlertConfig(@Param("jobId") Long jobId,
                             @Param("alertMode") String alertMode,
                             @Param("alertInstanceId") Long alertInstanceId);

    Project selectProjectByJobId(@Param("id") long jobId);
}
