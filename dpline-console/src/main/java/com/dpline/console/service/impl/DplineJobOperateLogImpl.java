package com.dpline.console.service.impl;

import com.dpline.common.Constants;
import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.enums.Status;
import com.dpline.common.util.*;
import com.dpline.console.service.GenericService;
import com.dpline.dao.dto.RuntimeLogResp;
import com.dpline.dao.entity.DplineJobOperateLog;
import com.dpline.dao.entity.Job;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.DplineJobOperateLogMapper;
import com.dpline.dao.mapper.LoggerViewMapper;
import com.dpline.dao.rto.DplineJobOperateLogRto;
import com.dpline.common.log.InsertMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class DplineJobOperateLogImpl extends GenericService<DplineJobOperateLog, Long> {

    @Autowired
    JobServiceImpl jobServiceImpl;

    @Autowired
    LoggerViewMapper loggerViewMapper;


    public DplineJobOperateLogImpl(@Autowired DplineJobOperateLogMapper genericMapper) {
        super(genericMapper);
    }

    public DplineJobOperateLogMapper getMapper(){
        return (DplineJobOperateLogMapper) this.genericMapper;
    }

    public Long queryLastOperateTimeStamp(Long jobId){
        return this.getMapper().queryLastOperateTime(jobId);
    }

    public Integer createNewLog(Long jobId,
                                OperationsEnum operationsEnum,
                                String operateLogPath,
                                String traceId){
        DplineJobOperateLog dplineJobOperateLog = new DplineJobOperateLog();
        dplineJobOperateLog.setJobId(jobId);
        dplineJobOperateLog.setOperateType(operationsEnum.name());
        dplineJobOperateLog.setOperateTimestamp(System.currentTimeMillis());
        dplineJobOperateLog.setOperateLogPath(operateLogPath);
        dplineJobOperateLog.setTraceId(traceId);
        return insert(dplineJobOperateLog);
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 向下刷新，只记录最新时间，开始时间
     *
     * @param jobId
     * @param lastTime
     * @param limitCount
     * @return
     */
    public Result<Object> logViewFromTimeStamp(Long jobId, String lastTime, Integer limitCount) {
        Result<Object> result = new Result<>();
        Long milliseconds;
        Job job = jobServiceImpl.getMapper().selectById(jobId);
        RuntimeLogResp runtimeLogResp = new RuntimeLogResp();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (StringUtils.isEmpty(lastTime)){
                milliseconds = queryLastOperateTimeStamp(jobId);
            } else {
                milliseconds = dateFormat.parse(lastTime).getTime();
            }
            // 开始时间，结束时间
            // loggerViewMapper
            ArrayList<InsertMessage> managerLog = this.loggerViewMapper.loggerViewFromOperatorTime(milliseconds, job.getJobName(), "jobmanager", limitCount);
            // 从什么时候开始
            managerLog.forEach( logEntity-> {
                stringBuilder.append(logFlatMessage(logEntity));
            });

            runtimeLogResp.setLogs(stringBuilder.toString());
            if(managerLog.size() > 0){
                runtimeLogResp.setLatestTime(new Timestamp(managerLog.get(managerLog.size() -1).getTimestamp()));
            } else {
                runtimeLogResp.setLatestTime(new Timestamp(milliseconds));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result.ok().setData(runtimeLogResp);
    }

    /**
     * 初始化 tail 日志
     * @param jobId
     * @return
     */
    public Result<Object> logViewTail(Long jobId,Integer limitCount) {
        Result<Object> result = new Result<>();
        if(Asserts.isNull(limitCount)){
            limitCount = 1000;
        }
        Job job = jobServiceImpl.getMapper().selectById(jobId);
        // 从上一次启动时间计算
        Long lastStartTime = this.getMapper().getLastStartTime(jobId);
        if(Asserts.isNull(lastStartTime)){
            lastStartTime = 0L;
        }
        ArrayList<InsertMessage> jobmanagerLog = loggerViewMapper.logTail(job.getJobName(), "jobmanager", lastStartTime, limitCount);
        StringBuilder stringBuilder = new StringBuilder();
        RuntimeLogResp runtimeLogResp = new RuntimeLogResp();
        if (jobmanagerLog.size() > 0){
            for (int i = jobmanagerLog.size() - 1 ;i >= 0;i--) {
                stringBuilder.append(logFlatMessage(jobmanagerLog.get(i)));
            }
            // 有值的话，结束时间为开头
            runtimeLogResp.setLatestTime(new Timestamp(jobmanagerLog.get(0).getTimestamp()));
            // 有值的话，结束时间为结尾
            runtimeLogResp.setStartTime(new Timestamp(jobmanagerLog.get(jobmanagerLog.size() - 1).getTimestamp()));
        } else {
            // 无值的话，开始时间，结束时间都是此时
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            runtimeLogResp.setLatestTime(timestamp);
            runtimeLogResp.setStartTime(timestamp);
        }
        runtimeLogResp.setLogs(stringBuilder.toString());
        return result.ok().setData(runtimeLogResp);
    }

    public Result<Object> logViewToTime(Long jobId, String endTime, Integer limitCount) {
        Result<Object> result = new Result<>();
        if(Asserts.isNull(limitCount)){
            limitCount = 1000;
        }
        Job job = jobServiceImpl.getMapper().selectById(jobId);
        ArrayList<InsertMessage> jobmanagerLog;
        RuntimeLogResp runtimeLogResp = new RuntimeLogResp();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            long milliseconds = dateFormat.parse(endTime).getTime();
            jobmanagerLog = loggerViewMapper.loggerViewToOperateTime(milliseconds, job.getJobName(), "jobmanager", limitCount);
            if (jobmanagerLog.size() > 0){
                for (int i = jobmanagerLog.size() - 1 ;i >= 0;i--) {
                    stringBuilder.append(logFlatMessage(jobmanagerLog.get(i)));
                }
                runtimeLogResp.setStartTime(new Timestamp(jobmanagerLog.get(jobmanagerLog.size() - 1).getTimestamp()));
            } else {
                runtimeLogResp.setStartTime(new Timestamp(milliseconds));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        runtimeLogResp.setLogs(stringBuilder.toString());
        return result.ok().setData(runtimeLogResp);
    }

    private String logFlatMessage(InsertMessage insertMessage) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dateFormat.format(new Date(insertMessage.getTimestamp()))).append(Constants.SPACE);
        stringBuilder.append(insertMessage.getLevel()).append(Constants.SPACE);
        stringBuilder.append(insertMessage.getClassName()).append(Constants.SPACE);
        stringBuilder.append(Constants.LEFT_BRACKETS + insertMessage.getLineNumber()+ Constants.RIGHT_BRACKETS).append(Constants.SPACE);
        stringBuilder.append(insertMessage.getContent()).append(Constants.CRLF_N);
        return stringBuilder.toString();
    }


    /**
     * 根据jobId 查询 操作日志
     * @param operateLogRto
     * @return
     */
    public Result<Object> searchOperateLogByJobId(DplineJobOperateLogRto operateLogRto) {
        Result<Object> result = new Result<>();
        Pagination<DplineJobOperateLog> InstanceList = Pagination.getInstanceFromRto(operateLogRto);
        this.executePagination(x -> this.getMapper().queryJobOperateHistory(x), InstanceList);
        return result.ok().setData(InstanceList);
    }


    public Result<Object> deleteLog(Long id,String operateLogPath) {
        Result<Object> result = new Result<>();
        try {
            this.getMapper().deleteById(id);
            // 删除下面的日志
            FileUtils.deleteFile(operateLogPath);
            putMsg(result, Status.SUCCESS);
        } catch (Exception ex) {
            logger.error(ExceptionUtil.exceptionToString(ex));
            putMsg(result, Status.DELETE_EXCEPTION);
        }
        return result;
    }
}
