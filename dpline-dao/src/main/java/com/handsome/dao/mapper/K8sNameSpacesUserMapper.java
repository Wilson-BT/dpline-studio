package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.K8sNameSpacesUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 为用户赋权Flink version
 */
public interface K8sNameSpacesUserMapper extends BaseMapper<K8sNameSpacesUser> {


//    List<K8sNameSpacesUser> queryK8sNameSpacesIdListByUserId(@Param("userId") int userId);

    int deleteByk8sNameSpaceId(@Param("k8sNamespaceId") long k8sNameSpaceId);

    void batchInsert(@Param("K8sNameSpacesUserList") List<K8sNameSpacesUser> arrayList);

    void batchRevokeK8sAuths(@Param("authedK8sIdList") Set<Long> authedK8sIdList, @Param("userId") int userId);
}
