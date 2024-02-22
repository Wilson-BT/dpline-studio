package com.handsome.console.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.handsome.common.Constants;
import com.handsome.common.enums.*;
import com.handsome.common.util.CodeGenerateUtils;
import com.handsome.common.util.EncryptionUtils;
import com.handsome.dao.entity.*;
import com.handsome.dao.mapper.*;
import com.handsome.console.exception.ServiceException;
import com.handsome.console.service.UsersService;
import com.handsome.console.util.CheckUtils;
import com.handsome.console.util.PageInfo;
import com.handsome.common.util.Result;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * users service impl
 */
@Service
public class UsersServiceImpl extends BaseServiceImpl implements UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Autowired
    private ResourceUserMapper resourceUserMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private K8sNameSpacesUserMapper k8sNameSpaceUserMapper;

    @Autowired
    private K8sNameSpaceMapper k8sNameSpaceMapper;

    @Autowired
    private FlinkTaskInstanceMapper flinkTaskInstanceMapper;

    @Autowired
    private AccessTokenMapper accessTokenMapper;

    @Autowired
    private FlinkTagTaskResRelationMapper flinkTagTaskResRelationMapper;

    @Autowired
    private FlinkTaskTagLogMapper flinkTaskTagLogMapper;

    @Autowired
    private FlinkTaskDefinitionMapper flinkTaskDefinitionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createUser(User loginUser, String userName, String userPassword,String email, String phone, int state,int isAdmin) throws IOException {

        Map<String, Object> result = new HashMap<>();

        //check all user params
        String msg = this.checkUserParams(userName, userPassword, email, phone, isAdmin);

        if (!StringUtils.isEmpty(msg)) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, msg);
            return result;
        }
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }

        User user = createUser(userName, userPassword, email, phone, state, isAdmin);

        result.put(Constants.DATA_LIST, user);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUser(String userName, String userPassword, String email, String phone, int state,int isAdmin) {
        User user = new User();
        Date now = new Date();

        user.setUserName(userName);
        user.setPassword(EncryptionUtils.getMd5(userPassword));
        user.setEmail(email);
        user.setPhone(phone);
        user.setState(state);
        // create general users, administrator users are currently built-in
        user.setIsAdmin(UserType.GENERAL_USER.getCode());
        user.setCreateTime(now);
        user.setUpdateTime(now);
        // save user
        userMapper.insert(user);
        return user;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUser(UserType userType, String userName, String email,int isAdmin) {
        User user = new User();
        Date now = new Date();

        user.setUserName(userName);
        user.setEmail(email);
        // create general users, administrator users are currently built-in
        user.setIsAdmin(userType.getCode());
        user.setCreateTime(now);
        user.setUpdateTime(now);
        // save user
        userMapper.insert(user);
        return user;
    }

    @Override
    public User getUserByUserCode(String userCode) {
        return userMapper.queryByUserNameAccurately(userCode);
    }

    @Override
    public User queryUser(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> queryUser(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return userMapper.selectByIds(ids);
    }

    @Override
    public User queryUser(String name) {
        return userMapper.queryByUserNameAccurately(name);
    }

    @Override
    public User queryUser(String userCode, String password) {
        String md5 = EncryptionUtils.getMd5(password);
        return userMapper.queryUserByCodePassword(userCode, md5);
    }

    @Override
    public int getUserIdByName(String name) {
        int executorId = 0;
        if (StringUtils.isNotEmpty(name)) {
            User executor = queryUser(name);
            if (null != executor) {
                executorId = executor.getId();
            } else {
                executorId = -1;
            }
        }

        return executorId;
    }

    @Override
    public Result queryUserList(User loginUser, String searchVal, Integer pageNo, Integer pageSize) {
        Result result = new Result();
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }

        Page<User> page = new Page<>(pageNo, pageSize);

        IPage<User> scheduleList = userMapper.queryUserPaging(page, searchVal);

        PageInfo<User> pageInfo = new PageInfo<>(pageNo, pageSize);
        pageInfo.setTotal((int) scheduleList.getTotal());
        pageInfo.setTotalList(scheduleList.getRecords());
        result.setData(pageInfo);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String, Object> updateUser(User loginUser, int userId, String userName, String userPassword, String email, String phone, int state) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put(Constants.STATUS, false);

        if (check(result, !hasPerm(loginUser, userId), Status.USER_NO_OPERATION_PERM)) {
            return result;
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            putMsg(result, Status.USER_NOT_EXIST, userId);
            return result;
        }
        if (StringUtils.isNotEmpty(userName)) {

            if (!CheckUtils.checkUserName(userName)) {
                putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, userName);
                return result;
            }

            User tempUser = userMapper.queryByUserNameAccurately(userName);
            if (tempUser != null && tempUser.getId() != userId) {
                putMsg(result, Status.USER_NAME_EXIST);
                return result;
            }
            user.setUserName(userName);
        }

        if (StringUtils.isNotEmpty(userPassword)) {
            if (!CheckUtils.checkPassword(userPassword)) {
                putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, userPassword);
                return result;
            }
            user.setPassword(EncryptionUtils.getMd5(userPassword));
        }

        if (StringUtils.isNotEmpty(email)) {
            if (!CheckUtils.checkEmail(email)) {
                putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, email);
                return result;
            }
            user.setEmail(email);
        }

        if (StringUtils.isNotEmpty(phone) && !CheckUtils.checkPhone(phone)) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, phone);
            return result;
        }

        if (state == 0 && user.getState() != state && loginUser.getId() == user.getId()) {
            putMsg(result, Status.NOT_ALLOW_TO_DISABLE_OWN_ACCOUNT);
            return result;
        }

        user.setPhone(phone);
        user.setState(state);
        Date now = new Date();
        user.setUpdateTime(now);

        // updateProcessInstance user
        userMapper.updateById(user);

        putMsg(result, Status.SUCCESS);
        return result;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String, Object> deleteUserById(User loginUser, int id) throws IOException {
        Map<String, Object> result = new HashMap<>();
        //only admin can operate
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM, id);
            return result;
        }
        //check exist
        User tempUser = userMapper.selectById(id);
        if (tempUser == null) {
            putMsg(result, Status.USER_NOT_EXIST, id);
            return result;
        }
        // check if is a project owner
        List<Project> projects = projectMapper.queryProjectCreatedByUser(id);
        if (CollectionUtils.isNotEmpty(projects)) {
            String projectNames = projects.stream().map(Project::getName).collect(Collectors.joining(","));
            putMsg(result, Status.TRANSFORM_PROJECT_OWNERSHIP, projectNames);
            return result;
        }
        //TODO 需要删除掉相应的本地目录
        accessTokenMapper.deleteAccessTokenByUserId(id);
        userMapper.deleteById(id);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 为项目赋权
     *
     * @param loginUser  login user
     * @param userId     user id
     * @param projectIds project id array
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String, Object> grantProject(User loginUser, int userId, String projectIds) {
        Map<String, Object> result = new HashMap<>();
        result.put(Constants.STATUS, false);
        //only admin can operate
        if (check(result, !isAdmin(loginUser), Status.USER_NO_OPERATION_PERM)) {
            return result;
        }
        //check exist
        User tempUser = userMapper.selectById(userId);
        if (tempUser == null) {
            putMsg(result, Status.USER_NOT_EXIST, userId);
            return result;
        }

        //if the selected projectIds are empty, delete all items associated with the user
        if (check(result, StringUtils.isEmpty(projectIds), Status.SUCCESS)) {
            projectUserMapper.deleteProjectRelation(0, userId);
            return result;
        }

        String[] projectIdArr = projectIds.split(",");

        for (String projectId : projectIdArr) {
            Date now = new Date();
            ProjectUser projectUser = new ProjectUser();
            projectUser.setUserId(userId);
            projectUser.setProjectId(Integer.parseInt(projectId));
            projectUser.setPerm(Constants.AUTHORIZE_ALL_PERM);
            projectUser.setCreateTime(now);
            projectUser.setUpdateTime(now);
            projectUserMapper.insert(projectUser);
        }
        putMsg(result, Status.SUCCESS);

        return result;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String, Object> grantProjectByCode(User loginUser, int userId, long projectCode) {
        Map<String, Object> result = new HashMap<>();
        result.put(Constants.STATUS, false);

        // 1. check if user is existed
        User tempUser = this.userMapper.selectById(userId);
        if (tempUser == null) {
            this.putMsg(result, Status.USER_NOT_EXIST, userId);
            return result;
        }

        // 2. check if project is existed
        Project project = this.projectMapper.queryByCode(projectCode);
        if (project == null) {
            this.putMsg(result, Status.PROJECT_NOT_FOUNT, projectCode);
            return result;
        }

        // 3. only project owner can operate
        if (!this.hasPerm(loginUser, project.getUserId())) {
            this.putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }

        // 4. maintain the relationship between project and user
        final Date today = new Date();
        ProjectUser projectUser = new ProjectUser();
        projectUser.setUserId(userId);
        projectUser.setProjectId(project.getId());
        projectUser.setPerm(Constants.AUTHORIZE_ALL_PERM);
        projectUser.setCreateTime(today);
        projectUser.setUpdateTime(today);
        this.projectUserMapper.insert(projectUser);

        this.putMsg(result, Status.SUCCESS);
        return result;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String, Object> revokeProject(User loginUser, int userId, long projectCode) {
        Map<String, Object> result = new HashMap<>();
        result.put(Constants.STATUS, false);

        // 1. only admin can operate
        if (this.check(result, !this.isAdmin(loginUser), Status.USER_NO_OPERATION_PERM)) {
            return result;
        }

        // 2. check if user is existed
        User user = this.userMapper.selectById(userId);
        if (user == null) {
            this.putMsg(result, Status.USER_NOT_EXIST, userId);
            return result;
        }

        // 3. check if project is existed
        Project project = this.projectMapper.queryByCode(projectCode);
        if (project == null) {
            this.putMsg(result, Status.PROJECT_NOT_FOUNT, projectCode);
            return result;
        }
        // 4. delete the relationship between project and user
        this.projectUserMapper.deleteProjectRelation(project.getId(), user.getId());
        this.putMsg(result, Status.SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> grantResources(User loginUser, int userId, String resourceIds) {

        Map<String, Object> result = new HashMap<>();
        //only admin can operate
        if (check(result, !isAdmin(loginUser), Status.USER_NO_OPERATION_PERM)) {
            return result;
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            putMsg(result, Status.USER_NOT_EXIST, userId);
            return result;
        }

        Set<Integer> needAuthorizeResIdsOrigin = new HashSet<>();
        Set<Integer> needAuthorizeResIds = new HashSet<>();
        if (StringUtils.isNotBlank(resourceIds)) {
            String[] resourceFullIdArr = resourceIds.split(",");
            // need authorize resource id set
            for (String resourceFullId : resourceFullIdArr) {
                String[] resourceIdArr = resourceFullId.split("-");
                for (int i = 0; i <= resourceIdArr.length - 1; i++) {
                    int resourceIdValue = Integer.parseInt(resourceIdArr[i]);
                    needAuthorizeResIdsOrigin.add(resourceIdValue);
                }
            }
            needAuthorizeResIds = needAuthorizeResIdsOrigin;
        }
        // get resources which had been authorized
        List<Integer> autheredResIds = resourceUserMapper.queryResourcesIdListByUserIdAndPerm(userId, Constants.NO_PERMISSON);

        // remove resources which had been authorized
        needAuthorizeResIds.removeAll(autheredResIds);

        // get the unauthorized resource id list
        for (int resourceIdValue : needAuthorizeResIds) {
            Resource resource = resourceMapper.selectById(resourceIdValue);
            if (resource == null) {
                putMsg(result, Status.RESOURCE_NOT_EXIST);
                return result;
            }

            Date now = new Date();
            ResourcesUser resourcesUser = new ResourcesUser();
            resourcesUser.setUserId(userId);
            resourcesUser.setResourcesId(resourceIdValue);
            if (resource.isDirectory()) {
                resourcesUser.setPerm(Constants.AUTHORIZE_READABLE_PERM);
            } else {
                resourcesUser.setPerm(Constants.AUTHORIZE_ALL_PERM);
            }

            resourcesUser.setCreateTime(now);
            resourcesUser.setUpdateTime(now);
            resourceUserMapper.insert(resourcesUser);
        }
        // 移除掉之前有权限的，但是未传参数进来的部分，需要移除掉，(B - (B & A))
        autheredResIds.removeAll(needAuthorizeResIdsOrigin);

        // check if the resource had been bounded by task definition or task tag
        Optional<FlinkTagTaskResRelation> firstResValue = flinkTagTaskResRelationMapper
                .queryByResId(autheredResIds)
                .stream()
                .findFirst();
        if (firstResValue.isPresent()) {
            FlinkTagTaskResRelation tagTaskResRelation = firstResValue.get();
            if (tagTaskResRelation.getDraftTagType().equals(DraftTagType.TASK_TAG)) {
                putMsg(result, Status.TASK_RESOURCE_IS_BOUND,
                        flinkTaskTagLogMapper.queryNameById(tagTaskResRelation.getId()));
                return result;
            }
            putMsg(result, Status.TASK_RESOURCE_IS_BOUND,
                    flinkTaskDefinitionMapper.queryNameById(tagTaskResRelation.getId()));
            return result;
        }
        if(CollectionUtils.isNotEmpty(autheredResIds)){
            resourceUserMapper.deleteBatchIds(autheredResIds);
        }
        putMsg(result, Status.SUCCESS);
        return result;
    }


    /**
     * @param loginUser login user
     * @return
     */
    @Override
    public Map<String, Object> getUserInfo(User loginUser) {
        Map<String, Object> result = new HashMap<>();
        User user = null;
        if (loginUser.getIsAdmin() == UserType.ADMIN_USER.getCode()) {
            user = loginUser;
        } else {
            user = userMapper.queryDetailsById(loginUser.getId());
            //TODO 是否要显示告警组的信息
        }
        result.put(Constants.DATA_LIST, user);

        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 获取所有普通用户
     *
     * @param loginUser login user
     * @return
     */
    @Override
    public Map<String, Object> queryAllGeneralUsers(User loginUser) {
        Map<String, Object> result = new HashMap<>();
        //only admin can operate
        if (check(result, !isAdmin(loginUser), Status.USER_NO_OPERATION_PERM)) {
            return result;
        }
        List<User> userList = userMapper.queryAllGeneralUser();
        result.put(Constants.DATA_LIST, userList);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * 获取所有用户
     *
     * @param loginUser login user
     * @return
     */
    @Override
    public Map<String, Object> queryUserList(User loginUser) {
        Map<String, Object> result = new HashMap<>();
        //only admin can operate
        if (check(result, !isAdmin(loginUser), Status.USER_NO_OPERATION_PERM)) {
            return result;
        }

        List<User> userList = userMapper.selectList(null);
        result.put(Constants.DATA_LIST, userList);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * 校验用户名是否存在
     *
     * @param userName user name
     * @return
     */
    @Override
    public Result<Object> verifyUserName(String userName) {
        Result<Object> result = new Result<>();
        User user = userMapper.queryByUserNameAccurately(userName);
        if (user != null) {
            putMsg(result, Status.USER_NAME_EXIST);
        } else {
            putMsg(result, Status.SUCCESS);
        }

        return result;
    }

    /**
     * 获取所有非本告警组的所有用户
     *
     * @param loginUser    login user
     * @param alertgroupId alert group id
     * @return
     */
    @Override
    public Map<String, Object> unauthorizedUser(User loginUser, Integer alertgroupId) {
        Map<String, Object> result = new HashMap<>();
        //only admin can operate
        if (check(result, !isAdmin(loginUser), Status.USER_NO_OPERATION_PERM)) {
            return result;
        }

        List<User> userList = userMapper.selectList(null);
        List<User> resultUsers = new ArrayList<>();
        Set<User> userSet = null;
        if (userList != null && !userList.isEmpty()) {
            userSet = new HashSet<>(userList);

            List<User> authedUserList = userMapper.queryUserListByAlertGroupId(alertgroupId);

            Set<User> authedUserSet = null;
            if (authedUserList != null && !authedUserList.isEmpty()) {
                authedUserSet = new HashSet<>(authedUserList);
                userSet.removeAll(authedUserSet);
            }
            resultUsers = new ArrayList<>(userSet);
        }
        result.put(Constants.DATA_LIST, resultUsers);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    @Override
    public Map<String, Object> authorizedUser(User loginUser, Integer alertgroupId) {
        Map<String, Object> result = new HashMap<>();
        //only admin can operate
        if (check(result, !isAdmin(loginUser), Status.USER_NO_OPERATION_PERM)) {
            return result;
        }
        List<User> userList = userMapper.queryUserListByAlertGroupId(alertgroupId);
        result.put(Constants.DATA_LIST, userList);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String, Object> registerUser(String userName, String userPassword, String repeatPassword, String email,int isAdmin) {
        Map<String, Object> result = new HashMap<>();

        //check user params
        String msg = this.checkUserParams(userName, userPassword, email, "",isAdmin);

        if (!StringUtils.isEmpty(msg)) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, msg);
            return result;
        }

        if (!userPassword.equals(repeatPassword)) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, "two passwords are not same");
            return result;
        }
        User user = createUser(userName, userPassword, email, "", Flag.NO.ordinal(),isAdmin);
        putMsg(result, Status.SUCCESS);
        result.put(Constants.DATA_LIST, user);
        return result;
    }

    @Override
    public Map<String, Object> activateUser(User loginUser, String userName) {
        Map<String, Object> result = new HashMap<>();
        result.put(Constants.STATUS, false);

        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }

        if (!CheckUtils.checkUserName(userName)) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, userName);
            return result;
        }

        User user = userMapper.queryByUserNameAccurately(userName);

        if (user == null) {
            putMsg(result, Status.USER_NOT_EXIST, userName);
            return result;
        }

        if (user.getState() != Flag.NO.ordinal()) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, userName);
            return result;
        }

        user.setState(Flag.YES.ordinal());
        Date now = new Date();
        user.setUpdateTime(now);
        userMapper.updateById(user);

        User responseUser = userMapper.queryByUserNameAccurately(userName);
        putMsg(result, Status.SUCCESS);
        result.put(Constants.DATA_LIST, responseUser);
        return result;
    }

    @Override
    public Map<String, Object> batchActivateUser(User loginUser, List<String> userNames) {
        Map<String, Object> result = new HashMap<>();

        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }

        int totalSuccess = 0;
        List<String> successUserNames = new ArrayList<>();
        Map<String, Object> successRes = new HashMap<>();
        int totalFailed = 0;
        List<Map<String, String>> failedInfo = new ArrayList<>();
        Map<String, Object> failedRes = new HashMap<>();
        for (String userName : userNames) {
            Map<String, Object> tmpResult = activateUser(loginUser, userName);
            if (tmpResult.get(Constants.STATUS) != Status.SUCCESS) {
                totalFailed++;
                Map<String, String> failedBody = new HashMap<>();
                failedBody.put("userName", userName);
                Status status = (Status) tmpResult.get(Constants.STATUS);
                String errorMessage = MessageFormat.format(status.getMsg(), userName);
                failedBody.put("msg", errorMessage);
                failedInfo.add(failedBody);
            } else {
                totalSuccess++;
                successUserNames.add(userName);
            }
        }
        successRes.put("sum", totalSuccess);
        successRes.put("userName", successUserNames);
        failedRes.put("sum", totalFailed);
        failedRes.put("info", failedInfo);
        Map<String, Object> res = new HashMap<>();
        res.put("success", successRes);
        res.put("failed", failedRes);
        putMsg(result, Status.SUCCESS);
        result.put(Constants.DATA_LIST, res);
        return result;
    }

    /**
     * grant all k8s auth to user
     *
     * @param loginUser
     * @param userId
     * @param k8sNameSpaceIds
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> grantK8sNameSpaces(User loginUser, int userId, String k8sNameSpaceIds) {
        Map<String, Object> result = new HashMap<>();
        if (check(result, !isAdmin(loginUser), Status.USER_NO_OPERATION_PERM)) {
            return result;
        }
        // 之前已经被赋权的
        Set<Long> needAuthorizeResIdsOrigin = new HashSet<>();
        // 之前已经被赋权的副本，用于同取 非交集部分 即 (A - (A & B))
        Set<Long> needAuthorizeResIds = new HashSet<>();
        if (StringUtils.isNotBlank(k8sNameSpaceIds)) {
            needAuthorizeResIdsOrigin = Arrays.stream(k8sNameSpaceIds.split(",")).map(Long::parseLong).collect(Collectors.toSet());
            needAuthorizeResIds = needAuthorizeResIdsOrigin;
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            putMsg(result, Status.USER_NOT_EXIST, userId);
            return result;
        }
        // 查看已经上线的,已经赋权的k8sNameSpace
        Set<K8sNameSpace> authedK8sIds = k8sNameSpaceMapper.queryK8sNameSpacesIdListByUserId(userId, ReleaseState.ONLINE);
        Set<Long> authedK8sIdList = authedK8sIds.stream().map(K8sNameSpace::getId)
                .collect(Collectors.toSet());
        // 有权限的部分不管了
        needAuthorizeResIds.removeAll(authedK8sIdList);
        // 无权限的部分赋予权限
        List<K8sNameSpacesUser> arrayList = new ArrayList<>();
        for (long k8sNameSpaceId : needAuthorizeResIds) {
            try {
                K8sNameSpacesUser k8sNameSpacesUser = new K8sNameSpacesUser();
                k8sNameSpacesUser.setK8sNameSpaceId(k8sNameSpaceId);
                k8sNameSpacesUser.setId(CodeGenerateUtils.getInstance().genCode());
                k8sNameSpacesUser.setUserId(userId);
                k8sNameSpacesUser.setCreateTime(new Date());
                k8sNameSpacesUser.setUpdateTime(new Date());
                arrayList.add(k8sNameSpacesUser);
            } catch (CodeGenerateUtils.CodeGenerateException e) {
                logger.error("ID 生成异常:{}", e.getMessage());
                throw new ServiceException(Status.GRANT_K8S_NAMESPACE_ERROR);
            }
            k8sNameSpaceUserMapper.batchInsert(arrayList);
        }
        // 移除掉之前有权限的，但是未传参数进来的部分，需要移除掉，(B - (B & A))
        authedK8sIdList.removeAll(needAuthorizeResIdsOrigin);
        // judge whether authedK8sIdList is used by user's task instance
        String projectTaskInstName = flinkTaskInstanceMapper.queryTaskInstanceNameByK8sAndUser(authedK8sIdList, userId);
        if(StringUtils.isNotEmpty(projectTaskInstName)){
            logger.error(String.format("k8s has been used by %s",projectTaskInstName));
            throw new ServiceException(String.format("k8s has been used by %s",projectTaskInstName));
        }
        k8sNameSpaceUserMapper.batchRevokeK8sAuths(authedK8sIdList,userId);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    private String checkUserParams(String userName, String password, String email, String phone,int userType) {
        String msg = null;
        if (!CheckUtils.checkUserName(userName)) {
            msg = userName;
        } else if (!CheckUtils.checkPassword(password)) {
            msg = password;
        } else if (!CheckUtils.checkEmail(email)) {
            msg = email;
        } else if (!CheckUtils.checkPhone(phone)) {
            msg = phone;
        } else if (!CheckUtils.checkUserType(userType)){
            msg = userType + "";
        }
        return msg;
    }

}
