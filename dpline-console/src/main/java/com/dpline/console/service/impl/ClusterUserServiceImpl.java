package com.dpline.console.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dpline.common.enums.Status;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.CollectionUtils;
import com.dpline.common.util.Result;
import com.dpline.console.service.ClusterUserService;
import com.dpline.console.service.GenericService;
import com.dpline.dao.entity.Cluster;
import com.dpline.dao.entity.ClusterUser;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.ClusterUserMapper;
import com.dpline.dao.rto.ClusterUserRto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ClusterUserServiceImpl extends GenericService<ClusterUser, Long> implements ClusterUserService {

    private static final Logger logger = LoggerFactory.getLogger(ClusterUserServiceImpl.class);

    public ClusterUserServiceImpl(@Autowired ClusterUserMapper clusterUserMapper) {
        super(clusterUserMapper);
    }

    public ClusterUserMapper getMapper() {
        return (ClusterUserMapper) super.genericMapper;
    }


    @Override
    public List<ClusterUser> list(ClusterUser clusterUser) {
        if (clusterUser == null) {
            return new ArrayList<>();
        }
        return getMapper().list(clusterUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<Object> addClusterUser(List<ClusterUser> clusterUserList) {
        Result<Object> result = new Result<>();
        if (CollUtil.isEmpty(clusterUserList)) {
            putMsg(result, Status.CLUSTER_ADD_LIST_NULL);
            return result;
        }
        Optional<Long> clusterIdOptional = clusterUserList.stream().map(ClusterUser::getClusterId).findFirst();
        List<String> userCodeList = clusterUserList.stream().map(ClusterUser::getUserCode).collect(Collectors.toList());
        if (CollUtil.isEmpty(userCodeList) || !clusterIdOptional.isPresent()) {
            putMsg(result, Status.CLUSTER_ADD_LIST_NULL);
            return result;
        }

        // 插入之前先判断是否存在
        LambdaQueryWrapper<ClusterUser> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ClusterUser::getClusterId, clusterIdOptional.get());
        queryWrapper.in(ClusterUser::getUserCode, userCodeList);
        // 先判断是否存在
        List<ClusterUser> savedEnginUserList = this.getMapper().selectList(queryWrapper);
        List<ClusterUser> toInsertList = new ArrayList<>();
        if (CollUtil.isEmpty(savedEnginUserList)) {
            toInsertList = clusterUserList;
        } else {
            Map<String, Long> existRelaMap = savedEnginUserList.stream().collect(Collectors.toMap(ClusterUser::getUserCode, ClusterUser::getClusterId));
            toInsertList = clusterUserList.stream()
                .filter(x -> Asserts.isNull(existRelaMap.get(x.getUserCode()))).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(toInsertList)){
            putMsg(result, Status.USER_RELATION_ALL_SAVED);
            return result;
        }
        return result.setData(insertBatch(toInsertList)).ok();
    }

    @Override
    public Integer deleteClusterUser(ClusterUser clusterUser) {
        if (clusterUser == null || clusterUser.getId() == null) {
            return 0;
        }
        int delete = delete(clusterUser.getId());
        return delete;
    }

    public Integer deleteRelationByUserCode(String userCode) {
        return this.getMapper().deleteRelationByUserCode(userCode);
    }

    public Integer deleteRelationByClusterId(Long clusterId) {
        return this.getMapper().deleteRelationByClusterId(clusterId);
    }

}
