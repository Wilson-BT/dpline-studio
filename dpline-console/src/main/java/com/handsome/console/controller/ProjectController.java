package com.handsome.console.controller;

import com.handsome.common.Constants;
import com.handsome.common.util.ParameterUtils;
import com.handsome.console.aspect.AccessLogAnnotation;
import com.handsome.dao.entity.User;
import com.handsome.console.exception.ApiException;
import com.handsome.console.service.ProjectService;
import com.handsome.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


import java.util.Map;

import static com.handsome.common.enums.Status.*;


/**
 * project controller
 */

@RestController
@RequestMapping("projects")
public class ProjectController extends BaseController {

    @Autowired
    private ProjectService projectService;

    /**
     * create project
     *
     * @param loginUser login user
     * @param projectName project name
     * @param description description
     * @return returns an error if it exists
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(CREATE_PROJECT_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result createProject(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam("projectName") String projectName,
                                @RequestParam(value = "description", required = false) String description) {
        Map<String, Object> result = projectService.createProject(loginUser, projectName, description);
        return returnDataList(result);
    }

    /**
     * update project
     *
     * @param loginUser login user
     * @param code project code
     * @param projectName project name
     * @param description description
     * @return update result code
     */
    @PutMapping(value = "/{code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(UPDATE_PROJECT_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result updateProject(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @PathVariable("code") Long code,
                                @RequestParam("projectName") String projectName,
                                @RequestParam(value = "description", required = false) String description,
                                @RequestParam(value = "userName") String userName) {
        Map<String, Object> result = projectService.update(loginUser, code, projectName, description, userName);
        return returnDataList(result);
    }

    /**
     * query project details by code
     *
     * @param loginUser login user
     * @param code project code
     * @return project detail information
     */
    @GetMapping(value = "/{code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_PROJECT_DETAILS_BY_CODE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryProjectByCode(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                     @PathVariable("code") long code) {
        Map<String, Object> result = projectService.queryByCode(loginUser, code);
        return returnDataList(result);
    }

    /**
     * query project list paging
     *
     * @param loginUser login user
     * @param searchVal search value
     * @param pageSize page size
     * @param pageNo page number
     * @return project list which the login user have permission to see
     */
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @ApiException(LOGIN_USER_QUERY_PROJECT_LIST_PAGING_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryProjectListPaging(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                         @RequestParam(value = "searchVal", required = false) String searchVal,
                                         @RequestParam("pageSize") Integer pageSize,
                                         @RequestParam("pageNo") Integer pageNo
    ) {

        Result result = checkPageParams(pageNo, pageSize);
        if (!result.checkResult()) {
            return result;
        }
        searchVal = ParameterUtils.handleEscapes(searchVal);
        result = projectService.queryProjectListPaging(loginUser, pageSize, pageNo, searchVal);
        return result;
    }

    /**
     * delete project by code
     *
     * @param loginUser login user
     * @param code project code
     * @return delete result code
     */
    @DeleteMapping(value = "/{code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(DELETE_PROJECT_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result deleteProject(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @PathVariable("code") Long code) {
        Map<String, Object> result = projectService.deleteProject(loginUser, code);
        return returnDataList(result);
    }

    /**
     * query unauthorized project
     *
     * @param loginUser login user
     * @param userId user id
     * @return the projects which user have not permission to see
     */
    @GetMapping(value = "/unauth-project")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_UNAUTHORIZED_PROJECT_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryUnauthorizedProject(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                           @RequestParam("userId") Integer userId) {
        Map<String, Object> result = projectService.queryUnauthorizedProject(loginUser, userId);
        return returnDataList(result);
    }

    /**
     * query authorized project
     *
     * @param loginUser login user
     * @param userId user id
     * @return projects which the user have permission to see, Except for items created by this user
     */
    @GetMapping(value = "/authed-project")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_AUTHORIZED_PROJECT)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryAuthorizedProject(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                         @RequestParam("userId") Integer userId) {
        Map<String, Object> result = projectService.queryAuthorizedProject(loginUser, userId);
        return returnDataList(result);
    }

    /**
     * query authorized user
     *
     * @param loginUser     login user
     * @param projectCode   project code
     * @return users        who have permission for the specified project
     */
    @GetMapping(value = "/authed-user")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_AUTHORIZED_USER)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryAuthorizedUser(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
            @RequestParam("projectCode") Long projectCode) {
        Map<String, Object> result = this.projectService.queryAuthorizedUser(loginUser, projectCode);
        return this.returnDataList(result);
    }

    /**
     * query authorized and user created project
     *
     * @param loginUser login user
     * @return projects which the user create and authorized
     */
    @GetMapping(value = "/created-and-authed")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_AUTHORIZED_AND_USER_CREATED_PROJECT_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryProjectCreatedAndAuthorizedByUser(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Map<String, Object> result = projectService.queryProjectCreatedAndAuthorizedByUser(loginUser);
        return returnDataList(result);
    }

    /**
     * query all project list
     *
     * @param loginUser login user
     * @return all project list
     *
     */
    @GetMapping(value = "/list")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(LOGIN_USER_QUERY_PROJECT_LIST_PAGING_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryAllProjectList(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Map<String, Object> result = projectService.queryAllProjectList();
        return returnDataList(result);
    }
}
