package com.handsome.console.controller;

import com.handsome.common.Constants;
import com.handsome.common.enums.ReleaseState;
import com.handsome.console.aspect.AccessLogAnnotation;
import com.handsome.dao.entity.User;
import com.handsome.console.exception.ApiException;
import com.handsome.console.service.K8sNameSpaceService;
import com.handsome.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


import static com.handsome.common.enums.Status.*;


@RestController
@RequestMapping("k8s_namespace")
public class K8sNameSpaceController extends BaseController {


    @Autowired
    private K8sNameSpaceService k8sNameSpaceService;

    /**
     * 创建 k8s 配置地址
     *
     * @param loginUser   login user
     * @param nameSpace        nameSpace
     * @param description description
     * @return create result code
     */
    @PostMapping()
    @ApiException(CREATE_K8S_ENVIRIONMENT_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> createK8sNameSpace(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @RequestParam(value = "nameSpace") String nameSpace,
                                             @RequestParam(value = "description", required = false) String description,
                                             @RequestParam(value = "path") String kubeConfigPath,
                                             @RequestParam(value = "serviceAccount")String serviceAccount,
                                             @RequestParam(value = "selectorLables",required = false,defaultValue = "{}") String selectorLables
    ) {
        return k8sNameSpaceService.createK8sNameSpace(loginUser, nameSpace, description, kubeConfigPath,serviceAccount,selectorLables);
    }

    /**
     * 更新k8s 客户端地址
     *
     * @param loginUser
     * @param kubeConfigId
     * @param releaseState
     * @param nameSpace
     * @param description
     * @param kubeConfigPath
     * @return
     */
    @PutMapping(value = "/{id}")
    @ApiException(UPDATE_K8S_ENVIRIONMENT_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> updateK8sNameSpace(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @PathVariable(value = "id") long kubeConfigId,
                                             @RequestParam(value = "online") ReleaseState releaseState,
                                             @RequestParam(value = "nameSpace", required = false) String nameSpace,
                                             @RequestParam(value = "description", required = false) String description,
                                             @RequestParam(value = "path", required = false) String kubeConfigPath,
                                             @RequestParam(value = "serviceAccount")String serviceAccount,
                                             @RequestParam(value = "selectorLables",required = false,defaultValue = "[]") String selectorLables
    ) {
        return k8sNameSpaceService.updateK8sNameSpace(loginUser, kubeConfigId, releaseState, nameSpace, description, kubeConfigPath,serviceAccount,selectorLables);
    }

    /**
     * 删除k8s客户端地址配置
     *
     * @param loginUser
     * @param kubeConfigId
     * @return
     */
    @DeleteMapping(value = "/{id}")
    @ApiException(DELETE_K8S_ENVIRIONMENT_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> deleteK8sNameSpace(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @PathVariable(value = "id") long kubeConfigId
    ) {
        return k8sNameSpaceService.deleteK8sNameSpace(loginUser, kubeConfigId);
    }

    /**
     * 查询用户权限范围内的k8s列表，不管是否上线
     *
     * @param loginUser
     * @return
     */
    @GetMapping("/authed_k8s")
    @ApiException(LIST_K8S_ENVIRIONMENT_INSTANCE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> queryK8sNameSpaceList(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        return k8sNameSpaceService.queryAuthK8sNameSpace(loginUser);
    }


}
