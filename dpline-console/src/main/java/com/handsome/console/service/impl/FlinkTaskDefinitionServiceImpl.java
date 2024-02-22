package com.handsome.console.service.impl;

import com.handsome.common.Constants;
import com.handsome.common.enums.DraftTagType;
import com.handsome.common.enums.Status;
import com.handsome.common.enums.TaskType;
import com.handsome.common.util.CodeGenerateUtils;
import com.handsome.common.util.StringUtils;
import com.handsome.dao.dto.FlinkTaskDefinitionDto;
import com.handsome.dao.entity.*;
import com.handsome.dao.mapper.FlinkTaskDefinitionMapper;
import com.handsome.dao.mapper.FlinkTagTaskResRelationMapper;
import com.handsome.dao.mapper.FlinkTaskTagLogMapper;
import com.handsome.dao.mapper.FlinkTagTaskUdfRelationMapper;
import com.handsome.console.exception.ServiceException;
import com.handsome.console.service.FlinkTaskDefinitionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class FlinkTaskDefinitionServiceImpl extends BaseServiceImpl implements FlinkTaskDefinitionService {

    private Logger logger = LoggerFactory.getLogger(FlinkTaskDefinitionServiceImpl.class);

    @Autowired
    private FlinkTaskDefinitionMapper flinkTaskDefinitionMapper;

    @Autowired
    private FlinkTaskTagLogMapper flinkTaskTagLogMapper;

    @Autowired
    private ProjectServiceImpl projectServiceImpl;

    @Autowired
    private FlinkTagTaskResRelationMapper flinkTaskResourceRelationMapper;

    @Autowired
    private FlinkTagTaskUdfRelationMapper flinkTaskUdfRelationMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createFlinkTaskDefinitionDraft(User loginUser, String taskName, int flinkVersionId, long projectCode, TaskType taskType, String description) {
        // check if user have perm to create flink task
        Map<String, Object> result = new HashMap<>();
        result = projectServiceImpl.queryByCode(loginUser, projectCode);
        if (!result.get(Constants.STATUS).equals(Status.SUCCESS)) {
            return result;
        }
        // check if exist in same project
        if (existSameFlinkTaskName(taskName, projectCode)) {
            putMsg(result, Status.FLINK_NAME_EXIST_ERROR, taskName);
            result.put(Constants.DATA_LIST, null);
            return result;
        }
        FlinkTaskDefinition flinkTaskDefinition = new FlinkTaskDefinition();
        try {
            Date date = new Date();
            flinkTaskDefinition.setId(CodeGenerateUtils.getInstance().genCode());
            flinkTaskDefinition.setTaskType(taskType);
            flinkTaskDefinition.setFlinkVersionId(flinkVersionId);
            flinkTaskDefinition.setProjectCode(projectCode);
            flinkTaskDefinition.setTaskName(taskName);
            flinkTaskDefinition.setCreateTime(date);
            flinkTaskDefinition.setUpdateTime(date);
            flinkTaskDefinition.setDescription(description);
            flinkTaskDefinitionMapper.insert(flinkTaskDefinition);
            result.put(Constants.DATA_LIST, flinkTaskDefinition);
        } catch (CodeGenerateUtils.CodeGenerateException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
        return result;
    }

    /**
     * update task definition draft
     *
     * @return Map
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateTaskDefinitionDraft(User loginUser, FlinkTaskDefinitionDto flinkTaskDefinitionDto) {
        Map<String, Object> result = projectServiceImpl.queryByCode(loginUser, flinkTaskDefinitionDto.getProjectCode());
        result.put(Constants.DATA_LIST, null);
        if (!result.get(Constants.STATUS).equals(Status.SUCCESS)) {
            return result;
        }
        FlinkTaskDefinition oldFlinkTaskDefinition = flinkTaskDefinitionMapper.selectById(flinkTaskDefinitionDto.getId());
        if (oldFlinkTaskDefinition == null) {
            putMsg(result, Status.TASK_DEFINE_NOT_EXIST);
            return result;
        }
        // name not same，need search
        if (!oldFlinkTaskDefinition.getTaskName().equals(flinkTaskDefinitionDto.getTaskName())
                && existSameFlinkTaskName(flinkTaskDefinitionDto.getTaskName(), flinkTaskDefinitionDto.getProjectCode())) {
            putMsg(result, Status.FLINK_NAME_EXIST_ERROR);
            return result;
        }
        FlinkTaskDefinition flinkTaskDefinition = flinkTaskDefinitionDto.createFlinkTaskDefinition();
        flinkTaskDefinitionMapper.updateById(flinkTaskDefinition);
        flinkTaskDefinition.setCreateTime(oldFlinkTaskDefinition.getCreateTime());
        result.put(Constants.DATA_LIST, flinkTaskDefinition);
        return result;
    }

    /**
     * create tag on flink task
     *
     * @param loginUser
     * @param flinkTaskDefinitionId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createFlinkTaskDefinitionTag(User loginUser, long flinkTaskDefinitionId, String taskTagName) {
        Map<String, Object> resultMap = new HashMap<>();
        //exist task
        FlinkTaskDefinition flinkTaskDefinition = flinkTaskDefinitionMapper.selectById(flinkTaskDefinitionId);
        if (flinkTaskDefinition == null) {
            putMsg(resultMap, Status.TASK_DEFINE_NOT_EXIST);
            return resultMap;
        }
        //have perm
        resultMap = projectServiceImpl.queryByCode(loginUser, flinkTaskDefinition.getProjectCode());
        resultMap.put(Constants.DATA_LIST, null);
        if (!resultMap.get(Constants.STATUS).equals(Status.SUCCESS)) {
            return resultMap;
        }
        //if tag name exists
        if (existSameFlinkTaskTagName(taskTagName, flinkTaskDefinitionId)) {
            putMsg(resultMap, Status.TASK_TAG_NAME_EXIST_ERROR);
            return resultMap;
        }
        FlinkTaskTagLog flinkTaskTagLog;
        try {
            flinkTaskTagLog = flinkTaskDefinition.createFlinkTaskTagLog(taskTagName);
            List<FlinkTagTaskUdfRelation> flinkTagTaskUdfRelations = flinkTaskUdfRelationMapper.selectAllById(flinkTaskDefinitionId);
            List<FlinkTagTaskResRelation> flinkTagTaskResRelations = flinkTaskResourceRelationMapper.selectAllById(flinkTaskDefinitionId);
            Date now = new Date();
            // task draft id -> task tag log Id
            if (CollectionUtils.isNotEmpty(flinkTagTaskUdfRelations)) {
                for (FlinkTagTaskUdfRelation flinkTagTaskUdfRelation : flinkTagTaskUdfRelations) {
                    flinkTagTaskUdfRelation.setId(flinkTaskTagLog.getId());
                    flinkTagTaskUdfRelation.setDraftTagType(DraftTagType.TASK_TAG);
                    flinkTagTaskUdfRelation.setCreateTime(now);
                    flinkTagTaskUdfRelation.setUpdateTime(now);
                }
                flinkTaskUdfRelationMapper.batchInsert(flinkTagTaskUdfRelations);
            }

            if (CollectionUtils.isNotEmpty(flinkTagTaskResRelations)) {
                for (FlinkTagTaskResRelation flinkTagTaskResRelation : flinkTagTaskResRelations) {
                    flinkTagTaskResRelation.setId(flinkTaskTagLog.getId());
                    flinkTagTaskResRelation.setDraftTagType(DraftTagType.TASK_TAG);
                    flinkTagTaskResRelation.setCreateTime(now);
                    flinkTagTaskResRelation.setUpdateTime(now);
                }
                flinkTaskResourceRelationMapper.batchInsert(flinkTagTaskResRelations);
            }
            flinkTaskTagLogMapper.insert(flinkTaskTagLog);
            resultMap.put(Constants.DATA_LIST, flinkTaskTagLog);
        } catch (Exception e) {
            logger.error(e.toString());
            throw new ServiceException(e.getMessage());
        }
        return resultMap;
    }

    /**
     * delete tag if there is no instance exists on this
     *
     * @param loginUser
     * @param flinkTaskTagId
     * @return
     */
    @Override
    public Map<String, Object> deleteFlinkTaskDefinitionTag(User loginUser, long flinkTaskTagId) {
        return null;
    }

    @Override
    public Map<String, Object> runLocalModeTask(User loginUser, long flinkTaskTagId, String socketId) {
        // 需要校验sql的有效性
        return null;
    }

    /**
     * update resource and udfs
     *
     * @param loginUser
     * @param resourceIds
     * @param udfIds
     * @return
     */
    @Override
    public Map<String, Object> updateTaskDefinitionRes(User loginUser, long taskDefinitionId, String resourceIds, String udfIds) {
        Map<String, Object> result = new HashMap<>();
        // res
        List<FlinkTagTaskResRelation> flinkTagTaskResRelationList = flinkTaskResourceRelationMapper.selectAllById(taskDefinitionId);
        List<Integer> resIdList = Arrays.stream(StringUtils.strSplitToArray(resourceIds)).map(Integer::parseInt).collect(Collectors.toList());
        List<Integer> oldResIds = flinkTagTaskResRelationList.stream().map(FlinkTagTaskResRelation::getResourceId).collect(Collectors.toList());
        // new have, old not have,need insert.
        Set<Integer> addResList = getDiffEntityList(resIdList, oldResIds, true);
        if (CollectionUtils.isNotEmpty(addResList)) {
            List<FlinkTagTaskResRelation> resList = addResList.stream().map(resId -> {
                FlinkTagTaskResRelation flinkTagTaskResRelation = new FlinkTagTaskResRelation();
                flinkTagTaskResRelation.setId(taskDefinitionId);
                flinkTagTaskResRelation.setResourceId(resId);
                flinkTagTaskResRelation.setDraftTagType(DraftTagType.TASK_DEFINITION);
                flinkTagTaskResRelation.setCreateTime(new Date());
                flinkTagTaskResRelation.setUpdateTime(new Date());
                return flinkTagTaskResRelation;
            }).collect(Collectors.toList());
            flinkTaskResourceRelationMapper.batchInsert(resList);
        }
        // old have, new not have,need delete
        Set<Integer> delResList = getDiffEntityList(resIdList, oldResIds, false);
        if (CollectionUtils.isNotEmpty(delResList)) {
            flinkTaskResourceRelationMapper.batchDeleteByIdAndResId(taskDefinitionId,delResList,DraftTagType.TASK_DEFINITION.getKey());
        }
        // mixed, no change

        // udf
        List<FlinkTagTaskUdfRelation> flinkTagTaskUdfRelationList = flinkTaskUdfRelationMapper.selectAllById(taskDefinitionId);
        List<Integer> udfIdList = Arrays.stream(StringUtils.strSplitToArray(udfIds)).map(Integer::parseInt).collect(Collectors.toList());
        List<Integer> oldUdfIds = flinkTagTaskUdfRelationList.stream().map(FlinkTagTaskUdfRelation::getUdfId).collect(Collectors.toList());
        Set<Integer> addUdfList = getDiffEntityList(udfIdList, oldUdfIds, true);
        if (CollectionUtils.isNotEmpty(addUdfList)) {
            List<FlinkTagTaskUdfRelation> udfList = addUdfList.stream().map(udfId -> {
                FlinkTagTaskUdfRelation flinkTagTaskUdfRelation = new FlinkTagTaskUdfRelation();
                flinkTagTaskUdfRelation.setId(taskDefinitionId);
                flinkTagTaskUdfRelation.setUdfId(udfId);
                flinkTagTaskUdfRelation.setDraftTagType(DraftTagType.TASK_DEFINITION);
                flinkTagTaskUdfRelation.setCreateTime(new Date());
                flinkTagTaskUdfRelation.setUpdateTime(new Date());
                return flinkTagTaskUdfRelation;
            }).collect(Collectors.toList());
            flinkTaskUdfRelationMapper.batchInsert(udfList);
        }
        // old have, new not have,need delete
        Set<Integer> delUdfList = getDiffEntityList(udfIdList, oldUdfIds, false);
        if (CollectionUtils.isNotEmpty(delUdfList)) {
            flinkTaskUdfRelationMapper.batchDeleteByIdAndUdfId(taskDefinitionId,delUdfList,DraftTagType.TASK_DEFINITION.getKey());
        }
       putMsg(result,Status.SUCCESS);
       return result;
    }

    private String[] strSplitToArray(String str) {
        return str.replace(Constants.LEFT_BRACKETS, Constants.BLACK)
                .replace(Constants.RIGHT_BRACKETS, Constants.BLACK)
                .split(Constants.COMMA);
    }

    /**
     * if exist the same flink Name in Flink project
     *
     * @param taskName
     * @param projectCode
     */
    private boolean existSameFlinkTaskName(String taskName, long projectCode) {
        return flinkTaskDefinitionMapper.existSameFlinkTaskName(taskName, projectCode) == Boolean.TRUE;
    }

    private boolean existSameFlinkTaskTagName(String taskTagName, long flinkTaskDefinitionId) {
        return flinkTaskTagLogMapper.existSameTagNameInSameTask(flinkTaskDefinitionId, taskTagName) == Boolean.TRUE;
    }

    /**
     * get different from two list
     * true is left
     *
     * @param oList
     * @param pList
     * @param leftRight
     * @param <O>
     * @return
     */
    public static <O> Set<O> getDiffEntityList(List<O> oList, List<O> pList, boolean leftRight) {
        if (leftRight) {
            return oList.stream()
                    .filter(t -> !pList.contains(t))
                    .collect(Collectors.toSet());
        }
        return pList.stream()
                .filter(t -> !oList.contains(t))
                .collect(Collectors.toSet());
    }

    public static <O> Set<O> getMixEntityList(List<O> oList, List<O> pList) {
        return pList.stream()
                .filter(oList::contains)
                .collect(Collectors.toSet());
    }

}
