package com.dpline.console.service.impl;

import com.dpline.common.enums.Status;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.CollectionUtils;
import com.dpline.common.util.Result;
import com.dpline.console.service.GenericService;
import com.dpline.dao.entity.AlertInstance;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.AlertInstanceMapper;
import com.dpline.dao.rto.AlertInstanceRto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertServiceImpl extends GenericService<AlertInstance,Long> {

    @Autowired
    JobServiceImpl JobServiceImpl;

    public AlertServiceImpl(@Autowired AlertInstanceMapper genericMapper) {
        super(genericMapper);
    }

    public AlertInstanceMapper getMapper(){
        return (AlertInstanceMapper) this.genericMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<Object> addAlertInstance(AlertInstance alertInstance){
        Result<Object> result = new Result<>();
        // 判断是否有同样名称存在
        if(existInstanceName(alertInstance)){
            putMsg(result,Status.ALERT_NAME_EXIST_ERROR, alertInstance.getInstanceName());
            return result;
        }
        insert(alertInstance);
        return result.ok();
    }

    private Boolean existInstanceName(AlertInstance alertInstance){
        return CollectionUtils.isNotEmpty(this.getMapper().existInstanceName(alertInstance.getInstanceName()));
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<Object> deleteAlertInstance(Long id){
        Result<Object> result = new Result<>();
        if(Asserts.isZero(id)){
            putMsg(result, Status.ALERT_ID_NULL_ERROR);
            return result;
        }
        if(CollectionUtils.isNotEmpty(JobServiceImpl.getMapper().selectByAlertInstanceId(id))){
            putMsg(result,Status.ALERT_BOUNDED_JOB_ERROR);
            return result;
        }
        delete(id);
        return result.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<Object> updateAlertInstance(AlertInstance alertInstance) {
        Result<Object> result = new Result<>();
        AlertInstance oldAlertInstance = this.getMapper().selectById(alertInstance.getId());
        // 判断有没有相同的名称
        if(!alertInstance.getInstanceName().equals(oldAlertInstance.getInstanceName())
            && existInstanceName(alertInstance)){
            putMsg(result,Status.ALERT_NAME_EXIST_ERROR,alertInstance.getInstanceName());
            return result;
        }
        update(alertInstance);
        return result.ok();
    }

    public Result<Object> list(AlertInstanceRto alertInstanceRto){
        Result<Object> result = new Result<>();
        Pagination<AlertInstance> alertInstance = Pagination.getInstanceFromRto(alertInstanceRto);
        this.executePagination(x -> this.getMapper().list(x), alertInstance);
        return result.setData(alertInstance).ok();
    }

    public Result<Object> query(AlertInstance alertInstance) {
        Result<Object> result = new Result<>();
        List<AlertInstance> alertInstanceList = this.getMapper().search(alertInstance);
        return result.setData(alertInstanceList).ok();
    }

    /**
     * @param id
     * @param enabledFlag
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> updateState(Long id, int enabledFlag) {
        Result<Object> result = new Result<>();
        if(CollectionUtils.isNotEmpty(JobServiceImpl.getMapper().selectByAlertInstanceId(id))){
            putMsg(result,Status.ALERT_BOUNDED_JOB_ERROR);
            return result;
        }
        AlertInstance oldAlertInstance = this.getMapper().selectById(id);
        oldAlertInstance.setEnabledFlag(enabledFlag);
        update(oldAlertInstance);
        return result.setData(oldAlertInstance).ok();
    }
}
