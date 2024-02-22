package com.handsome.console.controller;

import com.handsome.common.Constants;
import com.handsome.common.enums.ResourceType;
import com.handsome.common.enums.Status;
import com.handsome.common.util.ParameterUtils;
import com.handsome.console.aspect.AccessLogAnnotation;
import com.handsome.dao.entity.User;
import com.handsome.console.exception.ApiException;
import com.handsome.console.service.ResourcesService;
import com.handsome.console.service.UdfFuncService;
import com.handsome.common.util.Result;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;


import java.util.Map;

import static com.handsome.common.enums.Status.*;

/**
 * resources controller
 */
@RestController
@RequestMapping("resources")
public class ResourcesController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ResourcesController.class);

    @Autowired
    private ResourcesService resourceService;

    @Autowired
    private UdfFuncService udfFuncService;

    /**
     * @param loginUser   login user
     * @param type        type
     * @param alias       alias
     * @param description description
     * @param pid         parent id
     * @param currentDir  current directory
     * @return create result code
     */
    @PostMapping(value = "/directory")
    @ApiException(CREATE_RESOURCE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result createDirectory(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                  @RequestParam(value = "type") ResourceType type,
                                  @RequestParam(value = "name") String alias,
                                  @RequestParam(value = "description", required = false) String description,
                                  @RequestParam(value = "pid") int pid,
                                  @RequestParam(value = "currentDir") String currentDir) {
        return resourceService.createDirectory(loginUser, alias, description, type, pid, currentDir);
    }

    /**
     * create resource
     *
     * @return create result code
     */
    @PostMapping()
    @ApiException(CREATE_RESOURCE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result createResource(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @RequestParam(value = "type") ResourceType type,
                                 @RequestParam(value = "name") String alias,
                                 @RequestParam(value = "description", required = false) String description,
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "pid") int pid,
                                 @RequestParam(value = "currentDir") String currentDir) {
        return resourceService.createResource(loginUser, alias, description, type, file, pid, currentDir);
    }

    /**
     * update resource
     *
     * @param loginUser   login user
     * @param alias       alias
     * @param resourceId  resource id
     * @param type        resource type
     * @param description description
     * @param file        resource file
     * @return update result code
     */
    @PutMapping(value = "/{id}")
    @ApiException(UPDATE_RESOURCE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result updateResource(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @PathVariable(value = "id") int resourceId,
                                 @RequestParam(value = "type") ResourceType type,
                                 @RequestParam(value = "name") String alias,
                                 @RequestParam(value = "description", required = false) String description,
                                 @RequestParam(value = "file", required = false) MultipartFile file) {

        return resourceService.updateResource(loginUser, resourceId, alias, description, type, file);

    }

    /**
     * query resources list
     *
     * @param loginUser login user
     * @param type      resource type
     * @return resource list
     */
    @GetMapping(value = "/list")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_RESOURCES_LIST_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryResourceList(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                    @RequestParam(value = "type") ResourceType type
    ) {

        return resourceService.queryResourceList(loginUser, type);
    }

    /**
     * query resources list paging
     *
     * @param loginUser login user
     * @param type resource type
     * @param searchVal search value
     * @param pageNo page number
     * @param pageSize page size
     * @return resource list page
     */
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_RESOURCES_LIST_PAGING)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryResourceListPaging(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                          @RequestParam(value = "type") ResourceType type,
                                          @RequestParam(value = "id") int id,
                                          @RequestParam("pageNo") Integer pageNo,
                                          @RequestParam(value = "searchVal", required = false) String searchVal,
                                          @RequestParam("pageSize") Integer pageSize
    ) {
        Result result = checkPageParams(pageNo, pageSize);
        if (!result.checkResult()) {
            return result;
        }

        searchVal = ParameterUtils.handleEscapes(searchVal);
        result = resourceService.queryResourceListPaging(loginUser, id, type, searchVal, pageNo, pageSize);
        return result;
    }


    /**
     * delete resource
     *
     * @param loginUser login user
     * @param resourceId resource id
     * @return delete result code
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(DELETE_RESOURCE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result deleteResource(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @PathVariable(value = "id") int resourceId
    ) throws Exception {
        return resourceService.delete(loginUser, resourceId);
    }


    /**
     * verify resource by alias and type
     *
     * @param loginUser login user
     * @param fullName resource full name
     * @param type resource type
     * @return true if the resource name not exists, otherwise return false
     */
    @GetMapping(value = "/verify-name")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(VERIFY_RESOURCE_BY_NAME_AND_TYPE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result verifyResourceName(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                     @RequestParam(value = "fullName") String fullName,
                                     @RequestParam(value = "type") ResourceType type
    ) {
        return resourceService.verifyResourceName(fullName, type, loginUser);
    }

    /**
     * query resource by full name and type
     *
     * @param loginUser login user
     * @param fullName resource full name
     * @param type resource type
     * @param id resource id
     * @return true if the resource name not exists, otherwise return false
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(RESOURCE_NOT_EXIST)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryResource(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam(value = "fullName", required = false) String fullName,
                                @PathVariable(value = "id", required = false) Integer id,
                                @RequestParam(value = "type") ResourceType type
    ) {

        return resourceService.queryResource(fullName, id, type);
    }

    /**
     * view resource file online
     *
     * @param loginUser login user
     * @param resourceId resource id
     * @param skipLineNum skip line number
     * @param limit limit
     * @return resource content
     */
    @GetMapping(value = "/{id}/view")
    @ApiException(VIEW_RESOURCE_FILE_ON_LINE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result viewResource(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                               @PathVariable(value = "id") int resourceId,
                               @RequestParam(value = "skipLineNum") int skipLineNum,
                               @RequestParam(value = "limit") int limit
    ) {
        return resourceService.readResource(resourceId, skipLineNum, limit);
    }

    /**
     * create resource file online
     *
     * @return create result code
     */
    @PostMapping(value = "/online-create")
    @ApiException(CREATE_RESOURCE_FILE_ON_LINE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result onlineCreateResource(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                       @RequestParam(value = "type") ResourceType type,
                                       @RequestParam(value = "fileName") String fileName,
                                       @RequestParam(value = "suffix") String fileSuffix,
                                       @RequestParam(value = "description", required = false) String description,
                                       @RequestParam(value = "content") String content,
                                       @RequestParam(value = "pid") int pid,
                                       @RequestParam(value = "currentDir") String currentDir
    ) {
        if (StringUtils.isEmpty(content)) {
            logger.error("resource file contents are not allowed to be empty");
            return error(Status.RESOURCE_FILE_IS_EMPTY.getCode(), RESOURCE_FILE_IS_EMPTY.getMsg());
        }
        return resourceService.onlineCreateResource(loginUser, type, fileName, fileSuffix, description, content, pid, currentDir);
    }

    /**
     * edit resource file online
     *
     * @param loginUser login user
     * @param resourceId resource id
     * @param content content
     * @return update result code
     */
    @PutMapping(value = "/{id}/update-content")
    @ApiException(EDIT_RESOURCE_FILE_ON_LINE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result updateResourceContent(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                        @PathVariable(value = "id") int resourceId,
                                        @RequestParam(value = "content") String content
    ) {
        if (StringUtils.isEmpty(content)) {
            logger.error("The resource file contents are not allowed to be empty");
            return error(Status.RESOURCE_FILE_IS_EMPTY.getCode(), RESOURCE_FILE_IS_EMPTY.getMsg());
        }
        return resourceService.updateResourceContent(resourceId, content);
    }

    /**
     * download resource file
     *
     * @param loginUser login user
     * @param resourceId resource id
     * @return resource content
     */
    @GetMapping(value = "/{id}/download")
    @ResponseBody
    @ApiException(DOWNLOAD_RESOURCE_FILE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public ResponseEntity downloadResource(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                           @PathVariable(value = "id") int resourceId) throws Exception {
        Resource file = resourceService.downloadResource(resourceId);
        if (file == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Status.RESOURCE_NOT_EXIST.getMsg());
        }
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
            .body(file);
    }


    /**
     * authorized file resource list
     *
     * @param loginUser login user
     * @param userId user id
     * @return authorized result
     */
    @GetMapping(value = "/authed-file")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(AUTHORIZED_FILE_RESOURCE_ERROR)
    @AccessLogAnnotation
    public Result<Object> authorizedFile(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                 @RequestParam("userId") Integer userId) {
        return resourceService.authorizedFile(loginUser, userId);
    }


    /**
     * unauthorized file resource list
     *
     * @param loginUser login user
     * @param userId user id
     * @return unauthorized result code
     */
    @GetMapping(value = "/authed-resource-tree")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(AUTHORIZE_RESOURCE_TREE)
    @AccessLogAnnotation
    public Result authorizeResourceTree(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                        @RequestParam("userId") Integer userId) {
        Map<String, Object> result = resourceService.authorizeResourceTree(loginUser, userId);
        return returnDataList(result);
    }

    /**
     * create udf function
     *
     * @param loginUser login user
     * @param funcName function name
     * @param argTypes argument types
     * @param database database
     * @param description description
     * @param className class name
     * @param resourceId resource id
     * @return create result code
     */
    @PostMapping(value = "/{resourceId}/udf-func")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(CREATE_UDF_FUNCTION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result createUdfFunc(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam(value = "funcName") String funcName,
                                @RequestParam(value = "className") String className,
                                @RequestParam(value = "argTypes", required = false) String argTypes,
                                @RequestParam(value = "database", required = false) String database,
                                @RequestParam(value = "description", required = false) String description,
                                @PathVariable(value = "resourceId") int resourceId) {
        return udfFuncService.createUdfFunction(loginUser, funcName, className, argTypes, database, description, resourceId);
    }

    /**
     * view udf function
     *
     * @param id resource id
     * @return udf function detail
     */
    @GetMapping(value = "/{id}/udf-func")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(VIEW_UDF_FUNCTION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result viewUIUdfFunction(@PathVariable("id") int id) {
        Map<String, Object> map = udfFuncService.queryUdfFuncDetail(id);
        return returnDataList(map);
    }

    /**
     * update udf function
     *
     * @param loginUser login user
     * @param funcName function name
     * @param argTypes argument types
     * @param database data base
     * @param description description
     * @param resourceId resource id
     * @param className class name
     * @param udfFuncId udf function id
     * @return update result code
     */
    @PutMapping(value = "/{resourceId}/udf-func/{id}")
    @ApiException(UPDATE_UDF_FUNCTION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result updateUdfFunc(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @PathVariable(value = "id") int udfFuncId,
                                @RequestParam(value = "funcName") String funcName,
                                @RequestParam(value = "className") String className,
                                @RequestParam(value = "argTypes", required = false) String argTypes,
                                @RequestParam(value = "database", required = false) String database,
                                @RequestParam(value = "description", required = false) String description,
                                @PathVariable(value = "resourceId") int resourceId) {
        Map<String, Object> result = udfFuncService.updateUdfFunc(udfFuncId, funcName, className, argTypes, database, description, resourceId);
        return returnDataList(result);
    }

    /**
     * query udf function list paging
     *
     * @param loginUser login user
     * @param searchVal search value
     * @param pageNo page number
     * @param pageSize page size
     * @return udf function list page
     */
    @GetMapping(value = "/udf-func")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_UDF_FUNCTION_LIST_PAGING_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> queryUdfFuncListPaging(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                 @RequestParam("pageNo") Integer pageNo,
                                                 @RequestParam(value = "searchVal", required = false) String searchVal,
                                                 @RequestParam("pageSize") Integer pageSize
    ) {
        Result<Object> result = checkPageParams(pageNo, pageSize);
        if (!result.checkResult()) {
            return result;

        }
        result = udfFuncService.queryUdfFuncListPaging(searchVal, pageNo, pageSize);
        return result;
    }

    /**
     * query udf func list by type
     *
     * @param loginUser login user
     * @return resource list
     */
    @GetMapping(value = "/udf-func/list")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_DATASOURCE_BY_TYPE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> queryUdfFuncList(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        return udfFuncService.queryUdfFuncList();
    }

    /**
     * verify udf function name can use or not
     *
     * @param loginUser login user
     * @param name name
     * @return true if the name can user, otherwise return false
     */
    @GetMapping(value = "/udf-func/verify-name")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(VERIFY_UDF_FUNCTION_NAME_ERROR)
    @AccessLogAnnotation
    public Result verifyUdfFuncName(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                    @RequestParam(value = "name") String name
    ) {

        return udfFuncService.verifyUdfFuncByName(name);
    }

    /**
     * delete udf function
     *
     * @param loginUser login user
     * @param udfFuncId udf function id
     * @return delete result code
     */
    @DeleteMapping(value = "/udf-func/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(DELETE_UDF_FUNCTION_ERROR)
    @AccessLogAnnotation
    public Result deleteUdfFunc(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @PathVariable(value = "id") int udfFuncId
    ) {
        return udfFuncService.delete(udfFuncId);
    }
}
