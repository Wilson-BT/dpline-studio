package com.dpline.console.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dpline.common.Constants;
import com.dpline.common.enums.Flag;
import com.dpline.common.enums.UserType;
import com.dpline.common.util.Result;
import com.dpline.console.exception.ServiceException;
import com.dpline.console.util.Context;
import com.dpline.console.util.ContextUtils;
import com.dpline.dao.entity.User;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.common.enums.Status;
import com.dpline.dao.generic.GenericModel;
import com.dpline.dao.generic.Pagination;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public abstract class GenericService<T, PK extends Serializable> extends ServiceImpl<GenericMapper<T, PK>,T> {
    protected static final Logger logger = LoggerFactory.getLogger(GenericService.class);
    protected GenericMapper<T, PK> genericMapper;

    public GenericService(GenericMapper<T, PK> genericMapper) {
        this.genericMapper = genericMapper;
    }

    /**
     * 插入数据
     *
     * 如果主键是基于DB的方式，数据插入成功后，主键值会自动填充到输入对象中
     *
     * @param data 数据
     * @return 返回操作记录数
     */
    @Transactional
    public int insert(T data) {
        int result = 0;
        try {
            setDefault(data, true);
            result = genericMapper.insert(data);
        } catch (Exception e) {
            logger.error(Status.INSERT_EXCEPTION.getMsg(), e);
            throw new ServiceException(Status.INSERT_EXCEPTION);
        }

        return result;
    }

    /**
     * 插入数据，忽略值为null的字段
     * @param data 数据
     * @return 返回操作记录数
     */
    @Transactional
    public int insertSelective(T data) {
        int result = 0;
        try {
            setDefault(data, true);
            result = genericMapper.insert(data);
        } catch (Exception e) {
            logger.error(Status.INSERT_EXCEPTION.getMsg(), e);
            throw new ServiceException(Status.INSERT_EXCEPTION);
        }

        return result;
    }

    /**
     * 批量插入数据
     * @param datas 数据
     * @return 返回操作记录数
     */
    @Transactional
    public int insertBatch(List<T> datas) {
        int result = 0;
        try {
            if (datas != null) {
                for (T data : datas) {
                    setDefault(data, true);
                    int k = genericMapper.insert(data);
                    result = result + k;
                }
            }
        } catch (Exception e) {
            logger.error(Status.INSERT_EXCEPTION.getMsg(), e);
            throw new ServiceException(Status.INSERT_EXCEPTION);
        }

        return result;
    }


    /**
     * 更新数据
     * 主键为更新条件，其他为数据
     * @param datas 数据
     * @return 更新结果行数
     */
    @Transactional
    public int update(T... datas) {
        int result = 0;
        if (datas != null) {
            try {
                for (T data : datas) {
                    setDefault(data, false);
                    if (updateById(data)) {
                        result++;
                    }
                }
            } catch (Exception e) {
                logger.error(Status.UPDATE_EXCEPTION.getMsg(), e);
                throw new ServiceException(Status.UPDATE_EXCEPTION);
            }
        }

        return result;
    }

    /**
     * 更新数据，忽略空字段
     * 主键为更新条件，其他非null字段为数据
     * @param datas 数据
     * @return 第一个对象的update语句更新的行数
     */
    @Transactional
    public int updateSelective(T... datas) {
        return update(datas);
    }

    /**
     * !!! 注意, 该方法只能用于非分库分表数据源, 并且jdbc连接字符串要增加allowMultiQueries=true参数 !!!
     * 批量更新数据，忽略空字段
     * 主键为更新条件，其他非null字段为数据
     * @param datas 数据
     * @return 更新结果行数
     */
    @Transactional
    public int batchUpdateSelective(List<T> datas) {
        int result = 0;
        for (T data : datas) {
            result += update(data);
        }
        return result;
    }


    /**
     * 通过主键删除记录
     * @param ids  主键
     * @return    删除行数
     */
    @Transactional
    public int delete(PK... ids) {
        int result = 0;
        try {
            List<PK> collect = Arrays.stream(ids).collect(Collectors.toList());
            result = genericMapper.deleteBatchIds(collect);
        } catch (Exception e) {
            logger.error(Status.DELETE_EXCEPTION.getMsg(), e);
            throw new ServiceException(Status.DELETE_EXCEPTION);
        }

        return result;
    }

    /**
     * 通过主键获取数据
     * @param id  主键
     * @return    一行数据
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public T get(PK id) {
        T result = null;
        try {
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",id);
            result = genericMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            logger.error(Status.SELECT_EXCEPTION.getMsg(), e);
        }

        return result;
    }



    /**
     * 通过主键获取数据
     * @param ids  主键
     * @return List 如果无数据时，返回是长度为0的List对象
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> getByIds(PK... ids) {
        List<T> result = null;
        try {
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id",ids);
            result = genericMapper.selectList(queryWrapper);
        } catch (Exception e) {
            logger.error(Status.SELECT_EXCEPTION.getMsg(), e);
        }
        if (result == null) {
            result = new ArrayList<T>();
        }
        return result;
    }

    /**
     * 通过Model获取数据
     * @param data  Model数据，非null字段都做为条件查询
     * @return List 如果无数据时，返回是长度为0的List对象
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> selectAll(T data) {
        List<T> result = null;
        try {
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            queryWrapper.setEntity(data);
            result = genericMapper.selectList(queryWrapper);
        } catch (Exception e) {
            logger.error(Status.SELECT_EXCEPTION.getMsg(), e);
        }

        if (result == null) {
            result = new ArrayList<T>();
        }
        return result;
    }



    /**
     * check admin
     *
     * @param user input user
     * @return ture if administrator, otherwise return false
     */
    public boolean isAdmin(User user) {
        return user.getIsAdmin() == UserType.ADMIN_USER.getCode();
    }

    /**
     * put message to map
     *
     * @param result result code
     * @param status status
     * @param statusParams status message
     */
    public void putMsg(Map<String, Object> result, Status status, Object... statusParams) {
        result.put(Constants.STATUS, status);
        if (statusParams != null && statusParams.length > 0) {
            result.put(Constants.MSG, MessageFormat.format(status.getMsg(), statusParams));
        } else {
            result.put(Constants.MSG, status.getMsg());
        }
    }

    /**
     * put message to result object
     *
     * @param result result code
     * @param status status
     * @param statusParams status message
     */
    public void putMsg(Result<Object> result, Status status, Object... statusParams) {
        result.setCode(status.getCode());
        if (statusParams != null && statusParams.length > 0) {
            result.setMsg(MessageFormat.format(status.getMsg(), statusParams));
        } else {
            result.setMsg(status.getMsg());
        }
    }

    /**
     * check
     *
     * @param result result
     * @param bool bool
     * @param userNoOperationPerm status
     * @return check result
     */
    public boolean check(Map<String, Object> result, boolean bool, Status userNoOperationPerm) {
        // only admin can operate
        if (bool) {
            result.put(Constants.STATUS, userNoOperationPerm);
            result.put(Constants.MSG, userNoOperationPerm.getMsg());
            return true;
        }
        return false;
    }


    public static Pagination pageHelper(List list, Integer pageNum, Integer pageSize) {
        Pagination instance = Pagination.getInstance(pageNum,pageSize);
        int total = list.size();
        instance.setRowTotal(total);
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        if (startIndex > endIndex) {
            instance.setRows(new ArrayList<>());
            return instance;
        } else {
            instance.setRows(list.subList(startIndex, endIndex));
            return instance;
        }
    }



    /**
     * 设置添加公用参数
     *
     * @param data
     */
    private void setDefault(T data, boolean isNew) {
        if (data instanceof GenericModel) {
            GenericModel model = (GenericModel) data;
            Context context = ContextUtils.get();
            if (isNew) {
                model.setCreateTime(new Date(System.currentTimeMillis()));
                if (context != null) {
                    if (StringUtils.isNotEmpty(context.getUserCode())) {
                        model.setCreateUser(context.getUser().getUserCode());
                    }else{
                        model.setCreateUser("System");
                    }

                }
            }
            model.setUpdateTime(new Date(System.currentTimeMillis()));
            if (context != null) {
                if (StringUtils.isNotEmpty(context.getUserCode())) {
                    model.setUpdateUser(context.getUser().getUserCode());
                }else{
                    model.setUpdateUser("System");
                }
            }
            if(model.getEnabledFlag() == null){
                model.setEnabledFlag(Flag.YES.getCode());
            }
        }
    }


    protected void executePagination(PaginationCallback<T> callback, Pagination<T> pagination) {
        try {
            if(null != pagination) {
                PageHelper.startPage(pagination.getPage(), pagination.getPageSize(), pagination.isCount());
                List<T> pageResult = callback.execute(pagination);

                if (pagination.isCount()) {
                    Page page = (Page) pageResult;

                    pagination.setRowTotal((int) page.getTotal());
                    pagination.setPageTotal(page.getPages());
                }

                List<T> result = new ArrayList<T>();
                if (pageResult != null && pageResult.size() > 0) {
                    result.addAll(pageResult);
                }

                pagination.setRows(result);

            }
        } catch (Exception e) {
            logger.error(Status.SELECT_EXCEPTION.getMsg(), e);
            throw new ServiceException(Status.SELECT_EXCEPTION);

        }
        finally {
            PageHelper.clearPage();
        }
    }


    @Transactional
    public  int disable(Class<T> clazz,PK... ids) {
        int result = 0;
        try {
            T t = clazz.newInstance();
            Field enabledFlag = FieldUtils.getField(clazz, "enabledFlag", true);
            FieldUtils.writeField(enabledFlag,t,0L,true);

            QueryWrapper<T> updateWrapper = new QueryWrapper<>();
            updateWrapper.in("id",ids);

            result= genericMapper.update(t,updateWrapper);
        } catch (Exception e) {
            logger.error(Status.DELETE_EXCEPTION.getMsg(), e);
            throw new ServiceException(Status.DELETE_EXCEPTION);
        }

        return result;
    }
}