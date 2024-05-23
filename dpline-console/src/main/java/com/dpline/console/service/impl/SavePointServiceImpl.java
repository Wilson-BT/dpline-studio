package com.dpline.console.service.impl;

import com.dpline.common.enums.RunMode;
import com.dpline.common.enums.RunModeType;
import com.dpline.common.enums.Status;
import com.dpline.common.store.FsStore;
import com.dpline.common.util.*;
import com.dpline.console.service.GenericService;
import com.dpline.dao.entity.Job;
import com.dpline.dao.entity.JobSavepoint;
import com.dpline.dao.mapper.JobSavepointMapper;
import com.dpline.dao.rto.JobSavepointRto;
import org.apache.hadoop.fs.FileStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.dpline.common.util.TaskPathResolver.SAVEPOINT_DIR_FORMAT;

@Service
public class SavePointServiceImpl extends GenericService<JobSavepoint, Long> {

    private static final Pattern REGEX_USER_NAME = Pattern.compile("/checkpoint/([a-zA-Z0-9_-]{3,20})/([a-zA-Z0-9._-]{3,45})/([a-zA-Z0-9_-]{3,35})/chk-([a-zA-Z0-9_-]+)/([a-zA-Z0-9_-]+)");

    @Autowired
    private JobServiceImpl jobServiceImpl;

    @Autowired
    FsStore fsStore;

    public SavePointServiceImpl(@Autowired JobSavepointMapper genericMapper) {
        super(genericMapper);
    }

    public JobSavepointMapper getMapper() {
        return (JobSavepointMapper) this.genericMapper;
    }


    /**
     * @param jobSavepointRto
     * @return
     * @throws Exception
     */
    public Result<Object> queryCheckpointAndSavepoint(JobSavepointRto jobSavepointRto) {
        Result<Object> result = new Result<>();
        JobSavepointRto.CheckpointType checkpointType = JobSavepointRto.CheckpointType.of(
            jobSavepointRto.getCheckpointType());
        List<JobSavepoint> checkPointList = new ArrayList<>();
        switch (checkpointType){
            case SAVEPOINT:
                checkPointList =  this.getMapper().selectByJobId(jobSavepointRto.getJobId());
                break;
            case CHECKPOINT:
                Job job = jobServiceImpl.getMapper().selectById(jobSavepointRto.getJobId());
                try {
                    checkPointList = inferLastCheckPoint(job);
                } catch (Exception exception) {
                    logger.error("Get checkPointList failed.",exception);
                }
                break;
            default:
                logger.error("unSupport type to get checkpoint path.");
        };
            // 查找 job 所在的 Minio 路径，列出所有路径
        result.setData(checkPointList);
        return result.ok();
    }

    /**
     * 推断 上一次 checkpoint 位置
     *
     * @param job
     * @return
     */
    public List<JobSavepoint> inferLastCheckPoint(Job job) throws Exception {
        ArrayList<JobSavepoint> arrayList = new ArrayList<>();
        String jobCheckPointDir = "";
        RunModeType runModeType = RunModeType.of(job.getRunModeType());
        if (RunMode.APPLICATION.equals(runModeType.getRunMode())) {
            jobCheckPointDir = TaskPathResolver.getJobDefaultCheckPointDir(job.getProjectId(), job.getId(), job.getRunJobId());
        } else {
            // TODO
            //  application 模式 /dpline/checkpoint/projectId/jobId/flinkJobId/chk-xxx/_metadata (以上代码已经实现)
            //  session 模式 checkpoint/projectId/sessionId/jobId/chk-xxx/_metadata     (需要实现)
            return arrayList;
        }
        // 获取到路径下所有对象
        List<FileStatus> items = getRemoteCheckPointList(jobCheckPointDir);
        // 对所有对象清洗目录，去重，转化为 JobSavepoint
        return convertAndSort(items);
    }

    /**
     * 将 Item 转化为 JobSavepoint，进行数据清洗并去重
     * @param items
     * @return
     */
    public List<JobSavepoint> convertAndSort(List<FileStatus> items) {
        List<JobSavepoint> jobSavePoints = new ArrayList<>();
        if(CollectionUtils.isEmpty(items)){
            return jobSavePoints;
        }
        HashMap<String, JobSavepoint> pathHashMap = new HashMap<>();
        items.forEach(item -> {
            String realPath = item.getPath().toString();
            Matcher matcher = REGEX_USER_NAME.matcher(realPath);
            Date date = new Date(item.getModificationTime());

            String lastCheckPointDir = "";
            if(matcher.find()){
                int start = matcher.start(5);
                lastCheckPointDir = realPath.substring(0, start - 1);
            }else {
                lastCheckPointDir = realPath.substring(0,realPath.lastIndexOf("/"));
            }
            // 如果 包含 lastCheckPointDir，且已经重复更新了，才更新，否则
            if (pathHashMap.containsKey(lastCheckPointDir) && pathHashMap.get(lastCheckPointDir).getUpdateTime().before(date)){
                pathHashMap.get(lastCheckPointDir).setUpdateTime(date);
                pathHashMap.get(lastCheckPointDir).setCreateTime(date);
                return;
            }
            JobSavepoint jobSavepoint = new JobSavepoint();
            jobSavepoint.setSavepointPath(
                String.format(SAVEPOINT_DIR_FORMAT,
                        fsStore.getFileSystemPrefix(),
                    lastCheckPointDir)
            );
            String checkPointId = matcher.group(4);
            jobSavepoint.setId(Long.parseLong(checkPointId));
            // 还需要将 chk- 的编码取出，作为 id
            jobSavepoint.setCreateTime(date);
            jobSavepoint.setUpdateTime(date);
            jobSavepoint.setCreateUser("System");
            jobSavepoint.setUpdateUser("System");
            pathHashMap.put(lastCheckPointDir,jobSavepoint);
        });
        jobSavePoints = pathHashMap.values().stream().sorted(new Comparator<JobSavepoint>() {
            @Override
            public int compare(JobSavepoint o1, JobSavepoint o2) {
                if (o1.getCreateTime().before(o2.getCreateTime())) {
                    return 1;
                }
                if (o1.getCreateTime().after(o2.getCreateTime())) {
                    return -1;
                }
                return 0;
            }
        }).collect(Collectors.toList());
        return jobSavePoints;
    }

    public List<FileStatus> getRemoteCheckPointList(String jobCheckPointPath) throws Exception {
        if(fsStore.notExists(jobCheckPointPath)){
            return new ArrayList<>();
        }
        List<FileStatus> allObjects = fsStore.listAllFiles(jobCheckPointPath, true);
        return allObjects.stream().filter(item -> {
            Matcher matcher = REGEX_USER_NAME.matcher(item.getPath().toString());
            if (matcher.find()) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    /**
     * @return
     */
    public Result<Object> queryAllSavePoint(Long jobId) {
        Result<Object> result = new Result<>();
        List<JobSavepoint> jobSavePoints = this.getMapper().selectByJobId(jobId);
        return result.ok().setData(jobSavePoints);
    }

    /**
     * 删除 savepoint
     * @param savepointId
     * @return
     */
    public Result<Object> deleteSavePoint(Long savepointId) {
        Result<Object> result = new Result<>();
        JobSavepoint jobSavepoints = this.getMapper().selectById(savepointId);
        try {
            fsStore.delete(jobSavepoints.getSavepointPath().replace(fsStore.getFileSystemPrefix(), ""),true);
            this.getMapper().deleteById(savepointId);
        } catch (Exception exception) {
            putMsg(result, Status.DELETE_SAVEPOINT_ERROR);
            return result;
        }
        // 删除远程记录
        return result.ok();
    }

    public Result<Object> deleteCheckPoint(String checkpointPath) {
        Result<Object> result = new Result<>();
        checkpointPath = checkpointPath.replace(fsStore.getFileSystemPrefix(), "");
        try {
            fsStore.delete(checkpointPath,true);
        } catch (Exception e) {
            logger.error("删除 checkpoint 失败", e);
            putMsg(result, Status.DELETE_CHECKPOINT_ERROR);
            return result;
        }
        return result.ok();
    }
}
