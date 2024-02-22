

package com.handsome.console.service;

import com.handsome.common.util.Result;
import com.handsome.dao.entity.User;

import java.util.Map;

/**
 * alert plugin instance service
 */
public interface AlertPluginInstanceService {

    /**
     * creat alert plugin instance
     *
     * @param loginUser login user
     * @param pluginDefineId plugin define id
     * @param instanceName instance name
     * @param pluginInstanceParams plugin instance params
     * @return result
     */
    Map<String, Object> create(User loginUser,int pluginDefineId,String instanceName,String pluginInstanceParams);

    /**
     * update
     * @param loginUser login user
     * @param alertPluginInstanceId plugin instance id
     * @param instanceName instance name
     * @param pluginInstanceParams plugin instance params
     * @return result
     */
    Map<String, Object> update(User loginUser, int alertPluginInstanceId,String instanceName,String pluginInstanceParams);

    /**
     * delete alert plugin instance
     *
     * @param loginUser login user
     * @param id id
     * @return result
     */
    Map<String, Object> delete(User loginUser, int id);

    /**
     * get alert plugin instance
     *
     * @param loginUser login user
     * @param id get id
     * @return alert plugin
     */
    Map<String, Object> get(User loginUser, int id);

    /**
     * queryAll
     *
     * @return alert plugins
     */
    Map<String, Object> queryAll();

    /**
     * checkExistPluginInstanceName
     * @param pluginName plugin name
     * @return isExist
     */
    boolean checkExistPluginInstanceName(String pluginName);

    /**
     * queryPluginPage
     * @param loginUser login user
     * @param searchVal search value
     * @param pageNo    page index
     * @param pageSize  page size
     * @return plugins
     */
    Result listPaging(User loginUser, String searchVal, int pageNo, int pageSize);
}
