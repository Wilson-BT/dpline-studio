package com.dpline.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dpline.common.enums.Status;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.CollectionUtils;
import com.dpline.console.service.GenericService;
import com.dpline.dao.dto.ProjectUserInfo;
import com.dpline.dao.entity.User;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.UserMapper;
import com.dpline.common.Constants;
import com.dpline.common.util.EncryptionUtils;
import com.dpline.common.util.Result;
import com.dpline.console.util.CheckUtils;
import com.dpline.dao.rto.DplineUserRto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * users service impl
 */
@Service
public class UsersServiceImpl extends GenericService<User,Long> {


    @Autowired
    ProjectUserServiceImpl projectUserServiceImpl;

    @Autowired
    ClusterUserServiceImpl clusterUserServiceImpl;


    private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);

    public UsersServiceImpl(@Autowired UserMapper userMapper) {
        super(userMapper);
    }

    public UserMapper getMapper() {
        return (UserMapper) super.genericMapper;
    }

    
    public Result<Pagination<User>> getUserDetail(DplineUserRto dplineUserRto) {
        Result<Pagination<User>> result = new Result<>();
        result.ok();
        Pagination<User> userRto = Pagination.getInstanceFromRto(dplineUserRto);
        this.executePagination(p -> this.getMapper().listUserDetail(p),userRto);
        result.setData(userRto);
        return result;
    }


    /**
     * @param loginUser login user
     * @return
     */
    
    public Map<String, Object> getUserInfo(User loginUser) {
        Map<String, Object> result = new HashMap<>();
//        User user = null;
//        if (loginUser.getIsAdmin() == UserType.ADMIN_USER.getCode()) {
//            user = loginUser;
//        } else {
//            user = getMapper().queryDetailsById(loginUser.getId());
//            //TODO 是否要显示告警组的信息
//        }
        result.put(Constants.DATA_LIST, loginUser);

        putMsg(result, Status.SUCCESS);
        return result;
    }


    
    @Transactional(rollbackFor = Exception.class)
    public User createUser(String userName, String userPassword, String email, String phone, int state,int isAdmin) {
        User user = new User();
        Date now = new Date();

        user.setUserName(userName);
        user.setPassword(EncryptionUtils.getMd5(userPassword));
        user.setEmail(email);
        user.setPhone(phone);
        user.setEnabledFlag(state);
        // create general users, administrator users are currently built-in
        user.setIsAdmin(isAdmin);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        // save user
        insertSelective(user);
        return user;

    }

    
    @Transactional(rollbackFor = Exception.class)
    public User createUser(String userCode, String email,int isAdmin) {
        User user = new User();
        user.setUserCode(userCode);
        user.setEmail(email);
        // create general users, administrator users are currently built-in
        user.setIsAdmin(isAdmin);
        // save user
        insertSelective(user);
        return user;
    }

    
    public User getUserByUserCode(String userCode) {
        return getMapper().queryByUserNameAccurately(userCode);
    }

    
    public User queryUser(long id) {
        return getMapper().selectById(id);
    }

    
    public List<User> queryUser(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return getMapper().selectByIds(ids);
    }

    
    public User queryUser(String name) {
        return getMapper().queryByUserNameAccurately(name);
    }

    public User queryUserByUserCode(String code) {
        return getMapper().queryByUserCode(code);
    }

    
    public User queryUser(String userCode, String password) {
        String md5 = EncryptionUtils.getMd5(password);
        return getMapper().queryUserByCodePassword(userCode, md5);
    }


    /**
     * 获取所有用户
     *
     * @param loginUser login user
     * @return
     */
    
    public Map<String, Object> queryUserList(User loginUser) {
        Map<String, Object> result = new HashMap<>();
        //only admin can operate
        if (check(result, !isAdmin(loginUser), Status.USER_NO_OPERATION_PERM)) {
            return result;
        }

        List<User> userList = getMapper().queryAllUser();
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
    
    public Result<Object> verifyUserName(String userName) {
        Result<Object> result = new Result<>();
        User user = getMapper().queryByUserNameAccurately(userName);
        if (user != null) {
            putMsg(result, Status.USER_NAME_EXIST);
        } else {
            putMsg(result, Status.SUCCESS);
        }

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

    public Result<Object> insertUser(User user) {
        Result<Object> result = new Result<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code",user.getUserCode());
        User newUser = this.getMapper().selectOne(queryWrapper);
        if(Asserts.isNotNull(newUser)){
            putMsg(result,Status.USER_CODE_EXIST);
            return result;
        }
        user.setPassword(EncryptionUtils.getMd5(user.getPassword()));
        insert(user);
        putMsg(result,Status.SUCCESS);
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    public Result<Object> updateUser(User user) {
        Result<Object> result = new Result<>();
        user.setPassword(EncryptionUtils.getMd5(user.getPassword()));
        this.update(user);
        putMsg(result,Status.SUCCESS);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<Object> deleteUser(User user) {
        Result<Object> result = new Result<>();
        User userRel = this.getMapper().selectById(user.getId());
        this.getMapper().deleteById(userRel.getId());
        // 删除和用户有关的东西
        projectUserServiceImpl.deleteRelationByUserCode(userRel.getUserCode());
        clusterUserServiceImpl.deleteRelationByUserCode(userRel.getUserCode());
        return result.ok();
    }

    public Result<Object> searchUserByNameOrCode(String nameOrCode) {
        Result<Object> result = new Result<>();
        List<User> user = this.getMapper().queryUserByNameCode(nameOrCode);
        if (!CollectionUtils.isEmpty(user)){
            putMsg(result,Status.SUCCESS);
            result.setData(user);
            return result;
        }
        putMsg(result,Status.USER_NOT_EXIST);
        return result;
    }

    public List<ProjectUserInfo> getProjectUser(String userCode, Collection<Long> projectIdCollect) {
        return getMapper().getProjectUser(userCode,projectIdCollect);
    }
}
