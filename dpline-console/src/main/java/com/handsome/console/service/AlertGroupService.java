

package com.handsome.console.service;

import com.handsome.common.util.Result;
import com.handsome.dao.entity.User;

import java.util.Map;

/**
 * alert group service
 */
public interface AlertGroupService {

    /**
     * query alert group list
     *
     * @return alert group list
     */
    Map<String, Object> queryAlertgroup();

    /**
     * query alert group by id
     *
     * @param loginUser login user
     * @param id alert group id
     * @return one alert group
     */
    Map<String, Object> queryAlertGroupById(User loginUser, Integer id);
    /**
     * paging query alarm group list
     *
     * @param loginUser login user
     * @param searchVal search value
     * @param pageNo page number
     * @param pageSize page size
     * @return alert group list page
     */
    Result listPaging(User loginUser, String searchVal, Integer pageNo, Integer pageSize);

    /**
     * create alert group
     *
     * @param loginUser login user
     * @param groupName group name
     * @param desc description
     * @param alertInstanceIds alertInstanceIds
     * @return create result code
     */
    Map<String, Object> createAlertgroup(User loginUser, String groupName, String desc, String alertInstanceIds);

    /**
     * updateProcessInstance alert group
     *
     * @param loginUser login user
     * @param id alert group id
     * @param groupName group name
     * @param desc description
     * @param alertInstanceIds alertInstanceIds
     * @return update result code
     */
    Map<String, Object> updateAlertgroup(User loginUser, int id, String groupName, String desc, String alertInstanceIds);

    /**
     * delete alert group by id
     *
     * @param loginUser login user
     * @param id alert group id
     * @return delete result code
     */
    Map<String, Object> delAlertgroupById(User loginUser, int id);

    /**
     * verify group name exists
     *
     * @param groupName group name
     * @return check result code
     */
    boolean existGroupName(String groupName);
}
