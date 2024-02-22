package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.handsome.dao.entity.UdfFunc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * udf function mapper interface
 */
public interface UdfFuncMapper extends BaseMapper<UdfFunc> {

    /**
     * select udf by id
     * @param id udf id
     * @return UdfFunc
     */
    UdfFunc selectUdfById(@Param("id") int id);

    /**
     * query udf function by ids and function name
     * @param ids ids
     * @param funcNames funcNames
     * @return udf function list
     */
    List<UdfFunc> queryUdfByIdStr(@Param("ids") int[] ids,
                                  @Param("funcNames") String funcNames);

    /**
     * udf function page
     * @param page page
     * @param searchVal searchVal
     * @return udf function IPage
     */
    IPage<UdfFunc> queryUdfFuncPaging(IPage<UdfFunc> page,
                                      @Param("searchVal") String searchVal);

    /**
     *  query authed udf function
     * @return udf function list
     */
    List<UdfFunc> queryAuthedUdfFunc();

    /**
     * list UDF by resource id
     * @param   resourceIds  resource id array
     * @return  UDF function list
     */
    List<UdfFunc> listUdfByResourceId(@Param("resourceIds") Integer[] resourceIds);

    /**
     * batch update udf func
     * @param udfFuncList  udf list
     * @return update num
     */
    int batchUpdateUdfFunc(@Param("udfFuncList") List<UdfFunc> udfFuncList);

}
