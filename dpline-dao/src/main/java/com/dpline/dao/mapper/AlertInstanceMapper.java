package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dpline.dao.entity.AlertInstance;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("mysql")
@Repository
public interface AlertInstanceMapper extends GenericMapper<AlertInstance,Long> {

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
    AlertInstance queryById(@Param("id") Long id);

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
    List<AlertInstance> existInstanceName(@Param("instanceName") String instanceName);

    /**
     * @param alertInstance
     * @return
     */
    List<AlertInstance> list(Pagination<AlertInstance> alertInstance);

    List<AlertInstance> search(AlertInstance alertInstance);

}
