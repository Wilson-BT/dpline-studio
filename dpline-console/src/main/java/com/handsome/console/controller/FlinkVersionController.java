package com.handsome.console.controller;

import com.handsome.common.Constants;
import com.handsome.common.enums.ReleaseState;
import com.handsome.console.aspect.AccessLogAnnotation;
import com.handsome.dao.entity.User;
import com.handsome.console.exception.ApiException;
import com.handsome.console.service.FlinkVersionService;
import com.handsome.common.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


import static com.handsome.common.enums.Status.*;

/**
 * resources controller
 */
@RestController
@RequestMapping("flink_version")
public class FlinkVersionController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(FlinkVersionController.class);


    @Autowired
    private FlinkVersionService flinkVersionService;

    /**
     * @param loginUser   login user
     * @param alias       alias
     * @param description description
     * @return create result code
     */
    @PostMapping()
    @ApiException(CREATE_FLINK_VERSION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> createFlinkVersion(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @RequestParam(value = "name") String alias,
                                             @RequestParam(value = "description", required = false) String description,
                                             @RequestParam(value = "online",required = false) ReleaseState releaseState,
                                             @RequestParam(value = "path") String flinkPath) {
        return flinkVersionService.createFlinkVersion(loginUser, alias, releaseState,description, flinkPath);
    }

    @PutMapping(value = "/{id}")
    @ApiException(UPDATE_FLINK_VERSION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> updateFlinkVersion(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @PathVariable(value = "id") int flinkVersionId,
                                             @RequestParam(value = "online") ReleaseState releaseState,
                                             @RequestParam(value = "name", required = false) String alias,
                                             @RequestParam(value = "description", required = false) String description,
                                             @RequestParam(value = "path", required = false) String flinkPath
    ) {

        return flinkVersionService.updateFlinkVersion(loginUser, flinkVersionId, releaseState, alias, description, flinkPath);
    }

    @DeleteMapping(value = "/{id}")
    @ApiException(DELETE_FLINK_VERSION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> deleteFlinkVersion(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @PathVariable(value = "id") int flinkVersionId
    ) {
        return flinkVersionService.deleteFlinkVersion(loginUser, flinkVersionId);
    }

    @GetMapping()
    @ApiException(LIST_FLINK_VERSION_INSTANCE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> queryFlinkVersionList(@RequestParam(value = "online") ReleaseState releaseState) {
        return flinkVersionService.queryFlinkVersionList(releaseState);
    }



}
