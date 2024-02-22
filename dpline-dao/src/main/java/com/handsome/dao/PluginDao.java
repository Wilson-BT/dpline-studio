

package com.handsome.dao;

import com.handsome.dao.entity.PluginDefine;
import com.handsome.dao.mapper.PluginDefineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
public class PluginDao {
    @Autowired
    private PluginDefineMapper pluginDefineMapper;

    /**
     * check plugin define table exist
     *
     * @return boolean
     */
    public boolean checkPluginDefineTableExist() {
        return pluginDefineMapper.checkTableExist() > 0;
    }

    /**
     * add pluginDefine
     *
     * @param pluginDefine plugin define entiy
     * @return plugin define id
     */
    public int addPluginDefine(PluginDefine pluginDefine) {
        return pluginDefineMapper.insert(pluginDefine);
    }

    /**
     * add or update plugin define
     *
     * @param pluginDefine new pluginDefine
     */
    public int addOrUpdatePluginDefine(PluginDefine pluginDefine) {
        requireNonNull(pluginDefine, "pluginDefine is null");
        requireNonNull(pluginDefine.getPluginName(), "pluginName is null");
        requireNonNull(pluginDefine.getPluginType(), "pluginType is null");

        PluginDefine currPluginDefine = pluginDefineMapper.queryByNameAndType(pluginDefine.getPluginName(), pluginDefine.getPluginType());
        if (currPluginDefine == null) {
            if (pluginDefineMapper.insert(pluginDefine) == 1 && pluginDefine.getId() > 0) {
                return pluginDefine.getId();
            }
            throw new IllegalStateException("Failed to insert plugin definition");
        }
        if (!currPluginDefine.getPluginParams().equals(pluginDefine.getPluginParams())) {
            currPluginDefine.setUpdateTime(pluginDefine.getUpdateTime());
            currPluginDefine.setPluginParams(pluginDefine.getPluginParams());
            pluginDefineMapper.updateById(currPluginDefine);
        }
        return currPluginDefine.getId();
    }

    /**
     * query plugin define by id
     *
     * @param pluginDefineId plugin define id
     * @return PluginDefine
     */
    public PluginDefine getPluginDefineById(int pluginDefineId) {
        return pluginDefineMapper.selectById(pluginDefineId);
    }

    public PluginDefineMapper getPluginDefineMapper() {
        return pluginDefineMapper;
    }

    public void setPluginDefineMapper(PluginDefineMapper pluginDefineMapper) {
        this.pluginDefineMapper = pluginDefineMapper;
    }
}
