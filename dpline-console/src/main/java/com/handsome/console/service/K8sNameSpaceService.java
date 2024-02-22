package com.handsome.console.service;

import com.handsome.common.enums.ReleaseState;
import com.handsome.dao.entity.User;
import com.handsome.common.util.Result;

public interface K8sNameSpaceService extends BaseService {

    Result<Object> createK8sNameSpace(User loginUser, String alias, String description, String k8sConfigPath,String serviceAccount,String selectorLables);


    Result<Object> updateK8sNameSpace(User loginUser, long K8sNameSpaceId, ReleaseState releaseState, String alias, String description, String k8sConfigPath,String serviceAccount,String selectorLables);


    Result<Object> deleteK8sNameSpace(User loginUser, long K8sNameSpaceId);

    Result<Object> queryAuthK8sNameSpace(User loginUser);
}
