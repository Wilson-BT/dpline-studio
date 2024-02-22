package com.handsome.console.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.handsome.common.Constants;
import com.handsome.common.enums.Status;
import com.handsome.common.util.PropertyUtils;
import com.handsome.dao.entity.Resource;
import com.handsome.dao.entity.UdfFunc;
import com.handsome.dao.entity.User;
import com.handsome.dao.mapper.ResourceMapper;
import com.handsome.dao.mapper.UdfFuncMapper;
import com.handsome.console.service.UdfFuncService;
import com.handsome.console.util.PageInfo;
import com.handsome.common.util.Result;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * udf func service impl
 */
@Service
public class UdfFuncServiceImpl extends BaseServiceImpl implements UdfFuncService {

    private static final Logger logger = LoggerFactory.getLogger(UdfFuncServiceImpl.class);

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private UdfFuncMapper udfFuncMapper;

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
    @Override
    public Result<Object> createUdfFunction(User loginUser,
                                            String funcName,
                                            String className,
                                            String argTypes,
                                            String database,
                                            String desc,
                                            int resourceId) {
        Result<Object> result = new Result<>();

        // if resource upload startup
        if (!PropertyUtils.getResRemoteType()) {
            logger.error("resource upload startup state: {}", PropertyUtils.getResRemoteType());
            putMsg(result, Status.S3_NOT_STARTUP);
            return result;
        }
        // verify udf func name exist
        if (checkUdfFuncNameExists(funcName)) {
            putMsg(result, Status.UDF_FUNCTION_EXISTS);
            return result;
        }

        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            logger.error("resourceId {} is not exist", resourceId);
            putMsg(result, Status.RESOURCE_NOT_EXIST);
            return result;
        }

        //save data
        UdfFunc udf = new UdfFunc();
        Date now = new Date();
        udf.setUserId(loginUser.getId());
        udf.setFuncName(funcName);
        udf.setClassName(className);
        if (!StringUtils.isEmpty(argTypes)) {
            udf.setArgTypes(argTypes);
        }
        if (!StringUtils.isEmpty(database)) {
            udf.setDatabase(database);
        }
        udf.setDescription(desc);
        udf.setResourceId(resourceId);
        udf.setResourceName(resource.getFullName());

        udf.setCreateTime(now);
        udf.setUpdateTime(now);

        udfFuncMapper.insert(udf);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     *
     * @param name name
     * @return check result code
     */
    private boolean checkUdfFuncNameExists(String name) {
        List<UdfFunc> resource = udfFuncMapper.queryUdfByIdStr(null, name);
        return resource != null && !resource.isEmpty();
    }

    /**
     * query udf function
     *
     * @param id  udf function id
     * @return udf function detail
     */
    @Override
    public Map<String, Object> queryUdfFuncDetail(int id) {
        Map<String, Object> result = new HashMap<>();
        UdfFunc udfFunc = udfFuncMapper.selectById(id);
        if (udfFunc == null) {
            putMsg(result, Status.RESOURCE_NOT_EXIST);
            return result;
        }
        result.put(Constants.DATA_LIST, udfFunc);
        putMsg(result, Status.SUCCESS);
        return result;
    }

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
    @Override
    public Map<String, Object> updateUdfFunc(int udfFuncId,
                                             String funcName,
                                             String className,
                                             String argTypes,
                                             String database,
                                             String desc,
                                             int resourceId) {
        Map<String, Object> result = new HashMap<>();
        // verify udfFunc is exist
        UdfFunc udf = udfFuncMapper.selectUdfById(udfFuncId);

        if (udf == null) {
            result.put(Constants.STATUS, Status.UDF_FUNCTION_NOT_EXIST);
            result.put(Constants.MSG, Status.UDF_FUNCTION_NOT_EXIST.getMsg());
            return result;
        }

        // if resource upload startup
        if (!PropertyUtils.getResRemoteType()) {
            logger.error("resource upload startup state: {}", PropertyUtils.getResRemoteType());
            putMsg(result, Status.S3_NOT_STARTUP);
            return result;
        }

        // verify udfFuncName is exist
        if (!funcName.equals(udf.getFuncName())) {
            if (checkUdfFuncNameExists(funcName)) {
                logger.error("UdfFuncRequest {} has exist, can't create again.", funcName);
                result.put(Constants.STATUS, Status.UDF_FUNCTION_EXISTS);
                result.put(Constants.MSG, Status.UDF_FUNCTION_EXISTS.getMsg());
                return result;
            }
        }

        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            logger.error("resourceId {} is not exist", resourceId);
            result.put(Constants.STATUS, Status.RESOURCE_NOT_EXIST);
            result.put(Constants.MSG, Status.RESOURCE_NOT_EXIST.getMsg());
            return result;
        }
        Date now = new Date();
        udf.setFuncName(funcName);
        udf.setClassName(className);
        udf.setArgTypes(argTypes);
        if (!StringUtils.isEmpty(database)) {
            udf.setDatabase(database);
        }
        udf.setDescription(desc);
        udf.setResourceId(resourceId);
        udf.setResourceName(resource.getFullName());

        udf.setUpdateTime(now);

        udfFuncMapper.updateById(udf);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * query udf function list paging
     *
     * @param pageNo page number
     * @param pageSize page size
     * @param searchVal search value
     * @return udf function list page
     */
    @Override
    public Result<Object> queryUdfFuncListPaging(String searchVal, Integer pageNo, Integer pageSize) {
        Result<Object> result = new Result<>();
        PageInfo<UdfFunc> pageInfo = new PageInfo<>(pageNo, pageSize);
        IPage<UdfFunc> udfFuncList = getUdfFuncsPage(searchVal, pageSize, pageNo);
        pageInfo.setTotal((int)udfFuncList.getTotal());
        pageInfo.setTotalList(udfFuncList.getRecords());
        result.setData(pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get udf functions
     *
     * @param searchVal search value
     * @param pageSize page size
     * @param pageNo page number
     * @return udf function list page
     */
    private IPage<UdfFunc> getUdfFuncsPage(String searchVal, Integer pageSize, int pageNo) {
        Page<UdfFunc> page = new Page<>(pageNo, pageSize);
        return udfFuncMapper.queryUdfFuncPaging(page, searchVal);
    }

    /**
     * query udf list
     *
     * @return udf func list
     */
    @Override
    public Result<Object> queryUdfFuncList() {
        Result<Object> result = new Result<>();
        List<UdfFunc> udfFuncList = udfFuncMapper.queryAuthedUdfFunc();
        result.setData(udfFuncList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * delete udf function
     *
     * @param id udf function id
     * @return delete result code
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Result<Object> delete(int id) {
        Result<Object> result = new Result<>();
        udfFuncMapper.deleteById(id);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * verify udf function by name
     *
     * @param name name
     * @return true if the name can user, otherwise return false
     */
    @Override
    public Result<Object> verifyUdfFuncByName(String name) {
        Result<Object> result = new Result<>();
        if (checkUdfFuncNameExists(name)) {
            putMsg(result, Status.UDF_FUNCTION_EXISTS);
        } else {
            putMsg(result, Status.SUCCESS);
        }
        return result;
    }

}
