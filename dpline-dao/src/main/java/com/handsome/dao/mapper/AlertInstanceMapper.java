package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.handsome.dao.entity.AlertInstance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlertInstanceMapper extends BaseMapper<AlertInstance> {

    /**
     * query all alert plugin instance
     *
     * @return AlertPluginInstance list
     */
    List<AlertInstance> queryAllAlertPluginInstanceList();

    /**
     * query by alert group id
     *
     * @param ids
     * @return AlertPluginInstance list
     */
    List<AlertInstance> queryByIds(@Param("ids") List<Integer> ids);


    /**
     * query by alert group id
     *
     * @param id
     * @return AlertPluginInstance list
     */
    AlertInstance queryById(@Param("id") Integer id);

    /**
     * Query alert plugin instance by given name
     * @param page                page
     * @param instanceName         Alert plugin name
     * @return alertPluginInstance Ipage
     */
    IPage<AlertInstance> queryByInstanceNamePage(Page page, @Param("instanceName") String instanceName);

    /**
     *
     * @param instanceName instanceName
     * @return if exist return true else return null
     */
    Boolean existInstanceName(@Param("instanceName") String instanceName);

}
