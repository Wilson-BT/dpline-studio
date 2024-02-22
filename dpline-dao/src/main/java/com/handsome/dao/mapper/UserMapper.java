package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.handsome.dao.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * user mapper interface
 */
@CacheConfig(cacheNames = "user", keyGenerator = "cacheKeyGenerator")
public interface UserMapper extends BaseMapper<User> {
    /**
     * select by user id
     */
    @Cacheable(sync = true)
    User selectById(int id);

    /**
     * delete by id
     */
    @CacheEvict
    int deleteById(int id);

    /**
     * update
     */
    @CacheEvict(key = "#p0.id")
    int updateById(@Param("et") User user);

    /**
     * query all general user
     *
     * @return user list
     */
    List<User> queryAllGeneralUser();

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
    User queryDetailsById(@Param("userId") int userId);

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
    List<User> selectByIds(@Param("ids") List<Integer> ids);

    /**
     * query authed user list by projectId
     *
     * @param projectId projectId
     * @return user list
     */
    List<User> queryAuthedUserListByProjectId(@Param("projectId") int projectId);
}
