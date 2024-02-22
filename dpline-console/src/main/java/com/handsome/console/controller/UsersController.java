package com.handsome.console.controller;

import com.handsome.common.Constants;
import com.handsome.common.enums.Status;
import com.handsome.common.util.ParameterUtils;
import com.handsome.console.aspect.AccessLogAnnotation;
import com.handsome.dao.entity.User;
import com.handsome.console.exception.ApiException;
import com.handsome.console.service.UsersService;
import com.handsome.common.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.handsome.common.enums.Status.*;


@RestController
@RequestMapping("/users")
public class UsersController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UsersService usersService;

    /**
     * create user
     *
     * @param loginUser    login user
     * @param userName     user name
     * @param password user password
     * @param email        email
     * @param phone        phone
     * @return create result code
     */
    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(CREATE_USER_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = {"loginUser", "password"})
    public Result createUser(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                             @RequestParam(value = "userName") String userName,
                             @RequestParam(value = "userCode") String userCode,
                             @RequestParam(value = "password") String password,
                             @RequestParam(value = "isAdmin") int isAdmin,
                             @RequestParam(value = "email") String email,
                             @RequestParam(value = "phone", required = false) String phone,
                             @RequestParam(value = "state", required = false) int state) throws Exception {
        Map<String, Object> result = usersService.createUser(loginUser, userName, password, email, phone, state,isAdmin);
        return returnDataList(result);
    }


    /**
     * user list no paging
     *
     * @param loginUser login user
     * @return user list
     */
    @GetMapping(value = "/list")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(USER_LIST_ERROR)
    @AccessLogAnnotation
    public Result listUser(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Map<String, Object> result = usersService.queryAllGeneralUsers(loginUser);
        return returnDataList(result);
    }


    /**
     * user list no paging
     *
     * @param loginUser login user
     * @return user list
     */
    @GetMapping(value = "/list-all")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(USER_LIST_ERROR)
    @AccessLogAnnotation
    public Result listAll(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Map<String, Object> result = usersService.queryUserList(loginUser);
        return returnDataList(result);
    }


    /**
     * verify username
     *
     * @param loginUser login user
     * @param userName  user name
     * @return true if user name not exists, otherwise return false
     */
    @GetMapping(value = "/verify-user-name")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(VERIFY_USERNAME_ERROR)
    @AccessLogAnnotation
    public Result verifyUserName(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @RequestParam(value = "userName") String userName
    ) {
        return usersService.verifyUserName(userName);
    }


    /**
     * unauthorized user
     *
     * @param loginUser    login user
     * @param alertgroupId alert group id
     * @return unauthorize result code
     */
    @GetMapping(value = "/unauth-user")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(UNAUTHORIZED_USER_ERROR)
    @AccessLogAnnotation
    public Result unauthorizedUser(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                   @RequestParam("alertgroupId") Integer alertgroupId) {
        Map<String, Object> result = usersService.unauthorizedUser(loginUser, alertgroupId);
        return returnDataList(result);
    }


    /**
     * authorized user
     *
     * @param loginUser    login user
     * @param alertgroupId alert group id
     * @return authorized result code
     */
    @GetMapping(value = "/authed-user")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(AUTHORIZED_USER_ERROR)
    @AccessLogAnnotation
    public Result authorizedUser( @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @RequestParam("alertgroupId") Integer alertgroupId) {
        try {
            Map<String, Object> result = usersService.authorizedUser(loginUser, alertgroupId);
            return returnDataList(result);
        } catch (Exception e) {
            logger.error(Status.AUTHORIZED_USER_ERROR.getMsg(), e);
            return error(Status.AUTHORIZED_USER_ERROR.getCode(), Status.AUTHORIZED_USER_ERROR.getMsg());
        }
    }

    /**
     * user registry
     * dataTypeClass = String.class
     *
     * @param userName       user name
     * @param userPassword   user password
     * @param repeatPassword repeat password
     * @param email          user email
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(CREATE_USER_ERROR)
    @AccessLogAnnotation
    public Result<Object> registerUser(@RequestParam(value = "userName") String userName,
                                       @RequestParam(value = "password") String userPassword,
                                       @RequestParam(value = "repeatPassword") String repeatPassword,
                                       @RequestParam(value = "isAdmin") int isAdmin,
                                       @RequestParam(value = "email") String email) throws Exception {
        userName = ParameterUtils.handleEscapes(userName);
        userPassword = ParameterUtils.handleEscapes(userPassword);
        repeatPassword = ParameterUtils.handleEscapes(repeatPassword);
        email = ParameterUtils.handleEscapes(email);
        Map<String, Object> result = usersService.registerUser(userName, userPassword, repeatPassword, email,isAdmin);
        return returnDataList(result);
    }

    /**
     * user activate
     *
     * @param userName user name
     */
    @PostMapping("/activate")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(UPDATE_USER_ERROR)
    @AccessLogAnnotation
    public Result<Object> activateUser(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                       @RequestParam(value = "userName") String userName) {
        userName = ParameterUtils.handleEscapes(userName);
        Map<String, Object> result = usersService.activateUser(loginUser, userName);
        return returnDataList(result);
    }

    /**
     * user batch activate
     *
     * @param userNames user names
     */
    @PostMapping("/batch/activate")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(UPDATE_USER_ERROR)
    @AccessLogAnnotation
    public Result<Object> batchActivateUser(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                            @RequestBody List<String> userNames) {
        List<String> formatUserNames = userNames.stream().map(ParameterUtils::handleEscapes).collect(Collectors.toList());
        Map<String, Object> result = usersService.batchActivateUser(loginUser, formatUserNames);
        return returnDataList(result);
    }

    @PostMapping(value = "/grant-project")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(GRANT_PROJECT_ERROR)
    @AccessLogAnnotation
    public Result<Object> grantProject(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                       @RequestParam(value = "userId") int userId,
                                       @RequestParam(value = "projectIds") String projectIds) {
        Map<String, Object> result = usersService.grantProject(loginUser, userId, projectIds);
        return returnDataList(result);
    }

    /**
     * grant project by code
     *
     * @param loginUser   login user
     * @param userId      user id
     * @param projectCode project code
     * @return grant result code
     */
    @PostMapping(value = "/grant-project-by-code")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(GRANT_PROJECT_ERROR)
    @AccessLogAnnotation
    public Result<Object> grantProjectByCode(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @RequestParam(value = "userId") int userId,
                                             @RequestParam(value = "projectCode") long projectCode) {
        Map<String, Object> result = this.usersService.grantProjectByCode(loginUser, userId, projectCode);
        return this.returnDataList(result);
    }

    @PostMapping(value = "/revoke-project")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(REVOKE_PROJECT_ERROR)
    @AccessLogAnnotation
    public Result<Object> revokeProject(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                        @RequestParam(value = "userId") int userId,
                                        @RequestParam(value = "projectCode") long projectCode) {
        Map<String, Object> result = this.usersService.revokeProject(loginUser, userId, projectCode);
        return returnDataList(result);
    }

    /**
     * 为资源重新赋权，这里需要商榷，因为每次都是全量赋权，没有增量
     *
     * @param loginUser
     * @param userId
     * @param resourceIds
     * @return
     */
    @PostMapping(value = "/grant-file")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(GRANT_RESOURCE_ERROR)
    @AccessLogAnnotation
    public Result grantResource(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam(value = "userId") int userId,
                                @RequestParam(value = "resourceIds") String resourceIds) {
        Map<String, Object> result = usersService.grantResources(loginUser, userId, resourceIds);
        return returnDataList(result);
    }

    /**
     * grant k8s auther
     * @param loginUser
     * @param userId
     * @param k8sNameSpaceIds
     * @return
     */
    @PostMapping(value = "/grant-k8s")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(GRANT_K8S_NAMESPACE_ERROR)
    @AccessLogAnnotation
    public Result grantK8sNameSpace(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam(value = "userId") int userId,
                                @RequestParam(value = "k8sNameSpaceIds") String k8sNameSpaceIds) {
        Map<String, Object> result = usersService.grantK8sNameSpaces(loginUser, userId, k8sNameSpaceIds);
        return returnDataList(result);
    }

    /**
     * 获取用户信息
     *
     * @param loginUser
     * @return
     */
    @GetMapping(value = "/get-user-info")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(GET_USER_INFO_ERROR)
    @AccessLogAnnotation
    public Result getUserInfo(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Map<String, Object> result = usersService.getUserInfo(loginUser);
        return returnDataList(result);
    }

}
