package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.ResourcesUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * resource user relation mapper interface
 */
public interface ResourceUserMapper extends BaseMapper<ResourcesUser> {

    /**
     * query resourcesId list by userId and perm
     * @param userId userId
     * @param perm perm
     * @return resourcesId list result
     */
    List<Integer> queryResourcesIdListByUserIdAndPerm(@Param("userId") int userId,
                                                      @Param("perm") int perm);

    /**
     * delete resource user relation
     * @param userId userId
     * @param resourceId resourceId
     * @return delete result
     */
    int deleteResourceUser(@Param("userId") int userId,
                           @Param("resourceId") int resourceId);

    /**
     * delete resource user relation
     * @param userId userId
     * @param resIds resource Ids
     * @return delete result
     */
    int deleteResourceUserArray(@Param("userId") int userId,
                           @Param("resIds") Integer[] resIds);

}
