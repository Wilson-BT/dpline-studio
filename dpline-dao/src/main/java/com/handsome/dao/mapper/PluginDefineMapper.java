

package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.PluginDefine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PluginDefineMapper extends BaseMapper<PluginDefine> {

    /**
     * check table exist
     *
     * @return boolean
     */
    int checkTableExist();

    /**
     * query all plugin define
     *
     * @return PluginDefine list
     */
    List<PluginDefine> queryAllPluginDefineList();

    /**
     * query by plugin type
     *
     * @param pluginType pluginType
     * @return PluginDefine list
     */
    List<PluginDefine> queryByPluginType(@Param("pluginType") String pluginType);

    /**
     * query detail by id
     *
     * @param id id
     * @return PluginDefineDetail
     */
    PluginDefine queryDetailById(@Param("id") int id);

    /**
     * query by name and type
     *
     * @param pluginName
     * @param pluginType
     * @return
     */
    PluginDefine queryByNameAndType(@Param("pluginName") String pluginName, @Param("pluginType") String pluginType);
}
