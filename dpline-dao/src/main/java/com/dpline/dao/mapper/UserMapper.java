package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dpline.dao.dto.ProjectUserInfo;
import com.dpline.dao.entity.ProjectUser;
import com.dpline.dao.entity.User;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * user mapper interface
 */
@DS("mysql")
@Repository
@CacheConfig(cacheNames = "user", keyGenerator = "cacheKeyGenerator")
public interface UserMapper extends GenericMapper<User, Long> {

    /**
     * query all general user
     *
     * @return user list
     */
    List<User> queryAllGeneralUser();

    List<User> queryAllUser();

    /**
     * query user by name
     *
     * @param userName userName
     * @return user
     */
    User queryByUserNameAccurately(@Param("userName") String userName);

    /**
     * query user by userName and password
     *
     * @param userCode userCode
     * @param password password
     * @return user
     */
//    User queryUserByNamePassword(@Param("userCode") String userCode, @Param("password") String password);


    User queryUserByCodePassword(@Param("userCode") String userCode, @Param("password") String password);


    /**
     * user page
     *
     * @param page page
     * @param userName userName
     * @return user IPage
     */
    IPage<User> queryUserPaging(Page page,
                                @Param("userName") String userName);

    /**
     * query user detail by id
     *
     * @param userId userId
     * @return user
     */
    User queryDetailsById(@Param("userId") Long userId);

    /**
     * query user list by alertgroupId
     *
     * @param alertgroupId alertgroupId
     * @return user list
     */
    List<User> queryUserListByAlertGroupId(@Param("alertgroupId") int alertgroupId);

    /**
     * query user by token
     *
     * @param token token
     * @return user
     */
    User queryUserByToken(@Param("token") String token);


    /**
     * query user by ids
     *
     * @param ids id list
     * @return user list
     */
    List<User> selectByIds(@Param("ids") List<Long> ids);

    /**
     * query authed user list by projectId
     *
     * @param projectId projectId
     * @return user list
     */
    List<User> queryAuthedUserListByProjectId(@Param("projectId") int projectId);

    ProjectUser getProjectHistory(@Param("userCode") String userCode);

    ProjectUser queryProjectsByUser(@Param("userCode") String userCode);

    List<User> listUserDetail(Pagination<User> pagination);

    List<User> queryUserByNameCode(@Param("nameOrCode") String nameOrCode);

    List<ProjectUserInfo> getProjectUser(@Param("userCode")String userCode, @Param("projectId") Collection<Long> projectIdCollect);

    User queryByUserCode(@Param("userCode") String code);

    Integer disableUser(@Param("userId") Long userId,@Param("updateUser") String updateUser);
}



