package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.common.enums.ReleaseState;
import com.handsome.dao.entity.K8sNameSpace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


public interface K8sNameSpaceMapper extends BaseMapper<K8sNameSpace> {

    /**
     * 是否存在相同的名字或者相同的路径，如果存在
     *
     * @param k8sName
     * @return
     */
    Boolean existK8sNameSpace(@Param("k8sName")String k8sName,
                              @Param("kubeConfPath")String kubeConfPath);

    /**
     * list all namespace by online conditions
     *
     * @return
     */
    List<K8sNameSpace> queryAllK8sNameSpace();


    List<K8sNameSpace> queryOnlineK8sNameSpace();


    Set<K8sNameSpace> queryK8sNameSpacesIdListByUserId(@Param("userId") int userId, @Param("releaseState") ReleaseState releaseState);


}
