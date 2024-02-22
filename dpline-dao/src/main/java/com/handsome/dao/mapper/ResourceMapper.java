package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.handsome.dao.entity.Resource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ResourceMapper extends BaseMapper<Resource> {


    Boolean existResource(@Param("fullName") String fullName,
                           @Param("userId") int userId,
                           @Param("type") int type);

    List<Integer> listChildren(@Param("direcotyId") int resourceId);

    List<Resource> listResourceByIds(@Param("resIds") Integer[] childResIdArray);

    void batchUpdateResource(@Param("resourceList") List<Resource> childResourceList);

    List<Resource> queryResource(@Param("fullName") String fullName,@Param("type") int type);

    List<Resource> queryResourceListAuthored(@Param("userId") int userId,
                                             @Param("type") int type);

    IPage<Resource> queryResourcePaging(IPage<Resource> page,
                                        @Param("userId") int userId,
                                        @Param("id") int id,
                                        @Param("type") int type,
                                        @Param("searchVal") String searchVal,
                                        @Param("resIds") List<Integer> resIds);

    /**
     * 根据资源id批量获取资源信息
     *
     * @param resIds
     * @return
     */
    List<Resource> queryResourceListById(@Param("resIds") List<Integer> resIds);


    /**
     * delete resource by id array
     * @param resIds resource id array
     * @return delete num
     */
    int deleteIds(@Param("resIds")Integer[] resIds);


    List<Resource> queryResourceExceptUserId(@Param("userId") int userId);


}
