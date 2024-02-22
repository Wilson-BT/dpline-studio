//
//
//package com.handsome.console.controller;
//
//import com.handsome.console.aspect.AccessLogAnnotation;
//import com.handsome.common.enums.Status;
//import com.handsome.console.exception.ApiException;
//import com.handsome.console.service.AlertPluginInstanceService;
//import com.handsome.common.util.Result;
//import com.handsome.common.Constants;
//import com.handsome.common.util.ParameterUtils;
//import com.handsome.dao.entity.User;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//import springfox.documentation.annotations.ApiIgnore;
//
//
//import java.util.Map;
//
//import static com.handsome.common.enums.Status.*;
//
///**
// * alert plugin instance controller
// */
//@RestController
//@RequestMapping("alert-plugin-instances")
//public class AlertPluginInstanceController extends BaseController {
//
//    private static final Logger logger = LoggerFactory.getLogger(AlertPluginInstanceController.class);
//
//    @Autowired
//    private AlertPluginInstanceService alertPluginInstanceService;
//
//
//    /**
//     * create alert plugin instance
//     *
//     * @param loginUser login user
//     * @param pluginDefineId alert plugin define id
//     * @param instanceName instance name
//     * @param pluginInstanceParams instance params
//     * @return result
//     */
//    @PostMapping()
//    @ResponseStatus(HttpStatus.CREATED)
//    @ApiException(CREATE_ALERT_PLUGIN_INSTANCE_ERROR)
//    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
//    public Result createAlertPluginInstance(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
//                                            @RequestParam(value = "pluginDefineId") int pluginDefineId,
//                                            @RequestParam(value = "instanceName") String instanceName,
//                                            @RequestParam(value = "pluginInstanceParams") String pluginInstanceParams) {
//        Map<String, Object> result = alertPluginInstanceService.create(loginUser, pluginDefineId, instanceName, pluginInstanceParams);
//        return returnDataList(result);
//    }
//
//    /**
//     * updateAlertPluginInstance
//     *
//     * @param loginUser login user
//     * @param id alert plugin instance id
//     * @param instanceName instance name
//     * @param pluginInstanceParams instance params
//     * @return result
//     */
//    @PutMapping(value = "/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    @ApiException(UPDATE_ALERT_PLUGIN_INSTANCE_ERROR)
//    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
//    public Result updateAlertPluginInstance(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
//                                            @PathVariable(value = "id") int id,
//                                            @RequestParam(value = "instanceName") String instanceName,
//                                            @RequestParam(value = "pluginInstanceParams") String pluginInstanceParams) {
//        Map<String, Object> result = alertPluginInstanceService.update(loginUser, id, instanceName, pluginInstanceParams);
//        return returnDataList(result);
//    }
//
//    /**
//     * deleteAlertPluginInstance
//     *
//     * @param loginUser login user
//     * @param id id
//     * @return result
//     */
//    @DeleteMapping(value = "/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    @ApiException(DELETE_ALERT_PLUGIN_INSTANCE_ERROR)
//    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
//    public Result deleteAlertPluginInstance(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
//                                            @PathVariable(value = "id") int id) {
//
//        Map<String, Object> result = alertPluginInstanceService.delete(loginUser, id);
//        return returnDataList(result);
//    }
//
//    /**
//     * getAlertPluginInstance
//     *
//     * @param loginUser login user
//     * @param id alert plugin instance id
//     * @return result
//     */
//    @GetMapping(value = "/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    @ApiException(GET_ALERT_PLUGIN_INSTANCE_ERROR)
//    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
//    public Result getAlertPluginInstance(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
//                                         @PathVariable(value = "id") int id) {
//        Map<String, Object> result = alertPluginInstanceService.get(loginUser, id);
//        return returnDataList(result);
//    }
//
//    /**
//     * getAlertPluginInstance
//     *
//     * @param loginUser login user
//     * @return result
//     */
//    @GetMapping(value = "/list")
//    @ResponseStatus(HttpStatus.OK)
//    @ApiException(QUERY_ALL_ALERT_PLUGIN_INSTANCE_ERROR)
//    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
//    public Result getAlertPluginInstance(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
//        Map<String, Object> result = alertPluginInstanceService.queryAll();
//        return returnDataList(result);
//    }
//
//    /**
//     * check alert group exist
//     *
//     * @param loginUser login user
//     * @param alertInstanceName alert instance name
//     * @return check result code
//     */
//    @GetMapping(value = "/verify-name")
//    @ResponseStatus(HttpStatus.OK)
//    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
//    public Result verifyGroupName(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
//                                  @RequestParam(value = "alertInstanceName") String alertInstanceName) {
//
//        boolean exist = alertPluginInstanceService.checkExistPluginInstanceName(alertInstanceName);
//        Result result = new Result();
//        if (exist) {
//            logger.error("alert plugin instance {} has exist, can't create again.", alertInstanceName);
//            result.setCode(Status.PLUGIN_INSTANCE_ALREADY_EXIT.getCode());
//            result.setMsg(Status.PLUGIN_INSTANCE_ALREADY_EXIT.getMsg());
//        } else {
//            result.setCode(Status.SUCCESS.getCode());
//            result.setMsg(Status.SUCCESS.getMsg());
//        }
//        return result;
//    }
//
//    /**
//     * paging query alert plugin instance group list
//     *
//     * @param loginUser login user
//     * @param searchVal search value
//     * @param pageNo page number
//     * @param pageSize page size
//     * @return alert plugin instance list page
//     */
//    @GetMapping()
//    @ResponseStatus(HttpStatus.OK)
//    @ApiException(LIST_PAGING_ALERT_PLUGIN_INSTANCE_ERROR)
//    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
//    public Result listPaging(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
//                             @RequestParam(value = "searchVal", required = false) String searchVal,
//                             @RequestParam("pageNo") Integer pageNo,
//                             @RequestParam("pageSize") Integer pageSize) {
//        Result result = checkPageParams(pageNo, pageSize);
//        if (!result.checkResult()) {
//            return result;
//        }
//        searchVal = ParameterUtils.handleEscapes(searchVal);
//        return alertPluginInstanceService.listPaging(loginUser, searchVal, pageNo, pageSize);
//    }
//
//}
