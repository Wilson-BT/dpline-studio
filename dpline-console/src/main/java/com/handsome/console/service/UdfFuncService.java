package com.handsome.console.service;

import com.handsome.dao.entity.User;
import com.handsome.common.util.Result;

import java.util.Map;

/**
 * udf func service
 */
public interface UdfFuncService {

    /**
     * create udf function
     *
     * @param loginUser login user
     * @param funcName function name
     * @param argTypes argument types
     * @param database database
     * @param desc description
     * @param resourceId resource id
     * @param className class name
     * @return create result code
     */
    Result<Object> createUdfFunction(User loginUser,
                                     String funcName,
                                     String className,
                                     String argTypes,
                                     String database,
                                     String desc,
                                     int resourceId);

    /**
     * query udf function
     *
     * @param id  udf function id
     * @return udf function detail
     */
    Map<String, Object> queryUdfFuncDetail(int id);

    /**
     * updateProcessInstance udf function
     *
     * @param udfFuncId udf function id
     * @param funcName function name
     * @param argTypes argument types
     * @param database data base
     * @param desc description
     * @param resourceId resource id
     * @param className class name
     * @return update result code
     */
    Map<String, Object> updateUdfFunc(int udfFuncId,
                                      String funcName,
                                      String className,
                                      String argTypes,
                                      String database,
                                      String desc,
                                      int resourceId);

    /**
     * query udf function list paging
     *
     * @param pageNo page number
     * @param pageSize page size
     * @param searchVal search value
     * @return udf function list page
     */
    Result<Object> queryUdfFuncListPaging(String searchVal, Integer pageNo, Integer pageSize);

    /**
     * query udf list
     *
     * @return udf func list
     */
    Result<Object> queryUdfFuncList();

    /**
     * delete udf function
     *
     * @param id udf function id
     * @return delete result code
     */
    Result<Object> delete(int id);

    /**
     * verify udf function by name
     *
     * @param name name
     * @return true if the name can user, otherwise return false
     */
    Result<Object> verifyUdfFuncByName(String name);

}