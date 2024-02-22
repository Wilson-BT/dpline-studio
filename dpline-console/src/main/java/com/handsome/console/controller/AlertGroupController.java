

package com.handsome.console.controller;

import com.handsome.console.aspect.AccessLogAnnotation;
import com.handsome.common.enums.Status;
import com.handsome.console.exception.ApiException;
import com.handsome.console.service.AlertGroupService;
import com.handsome.common.util.Result;
import com.handsome.common.Constants;
import com.handsome.common.util.ParameterUtils;
import com.handsome.dao.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


import java.util.Map;

import static com.handsome.common.enums.Status.*;

/**
 * alert group controller
 */
@RestController
@RequestMapping("/alert-groups")
public class AlertGroupController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AlertGroupController.class);

    @Autowired
    private AlertGroupService alertGroupService;


    /**
     * create alert group
     *
     * @param loginUser login user
     * @param groupName group name
     * @param description description
     * @return create result code
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(CREATE_ALERT_GROUP_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result createAlertgroup(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                   @RequestParam(value = "groupName") String groupName,
                                   @RequestParam(value = "description", required = false) String description,
                                   @RequestParam(value = "alertInstanceIds") String alertInstanceIds) {
        Map<String, Object> result = alertGroupService.createAlertgroup(loginUser, groupName, description, alertInstanceIds);
        return returnDataList(result);
    }

    /**
     * alert group list
     *
     * @param loginUser login user
     * @return alert group list
     */
    @GetMapping(value = "/list")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_ALL_ALERTGROUP_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result list(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {

        Map<String, Object> result = alertGroupService.queryAlertgroup();
        return returnDataList(result);
    }

    /**
     * paging query alarm group list
     *
     * @param loginUser login user
     * @param pageNo page number
     * @param searchVal search value
     * @param pageSize page size
     * @return alert group list page
     */
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @ApiException(LIST_PAGING_ALERT_GROUP_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result listPaging(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                             @RequestParam(value = "searchVal", required = false) String searchVal,
                             @RequestParam("pageNo") Integer pageNo,
                             @RequestParam("pageSize") Integer pageSize) {
        Result result = checkPageParams(pageNo, pageSize);
        if (!result.checkResult()) {
            return result;
        }
        searchVal = ParameterUtils.handleEscapes(searchVal);
        return alertGroupService.listPaging(loginUser, searchVal, pageNo, pageSize);
    }
    /**
     * check alarm group detail by Id
     *
     * @param loginUser login user
     * @param id alert group id
     * @return one alert group
     */

    @PostMapping(value = "/query")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_ALERT_GROUP_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryAlertGroupById(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                      @RequestParam("id") Integer id) {

        Map<String, Object> result = alertGroupService.queryAlertGroupById(loginUser, id);
        return returnDataList(result);
    }



    /**
     * updateProcessInstance alert group
     *
     * @param loginUser login user
     * @param id alert group id
     * @param groupName group name
     * @param description description
     * @return update result code
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(UPDATE_ALERT_GROUP_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result updateAlertgroup(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                   @PathVariable(value = "id") int id,
                                   @RequestParam(value = "groupName") String groupName,
                                   @RequestParam(value = "description", required = false) String description,
                                   @RequestParam(value = "alertInstanceIds") String alertInstanceIds) {

        Map<String, Object> result = alertGroupService.updateAlertgroup(loginUser, id, groupName, description, alertInstanceIds);
        return returnDataList(result);
    }

    /**
     * delete alert group by id
     *
     * @param loginUser login user
     * @param id alert group id
     * @return delete result code
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(DELETE_ALERT_GROUP_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result delAlertgroupById(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                    @PathVariable(value = "id") int id) {
        Map<String, Object> result = alertGroupService.delAlertgroupById(loginUser, id);
        return returnDataList(result);
    }


    /**
     * check alert group exist
     *
     * @param loginUser login user
     * @param groupName group name
     * @return check result code
     */
    @GetMapping(value = "/verify-name")
    @ResponseStatus(HttpStatus.OK)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result verifyGroupName(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                  @RequestParam(value = "groupName") String groupName) {

        boolean exist = alertGroupService.existGroupName(groupName);
        Result result = new Result();
        if (exist) {
            logger.error("group {} has exist, can't create again.", groupName);
            result.setCode(Status.ALERT_GROUP_EXIST.getCode());
            result.setMsg(Status.ALERT_GROUP_EXIST.getMsg());
        } else {
            result.setCode(Status.SUCCESS.getCode());
            result.setMsg(Status.SUCCESS.getMsg());
        }
        return result;
    }
}
