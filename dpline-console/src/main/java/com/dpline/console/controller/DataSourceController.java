package com.dpline.console.controller;

import cn.hutool.core.util.StrUtil;
import com.dpline.common.enums.Status;
import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.DataSourceService;
import com.dpline.dao.dto.DataSourceDto;
import com.dpline.dao.entity.DataSource;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.rto.DataSourceRto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "数据源管理")
@RestController
@RequestMapping("/system/dataSource")
public class DataSourceController {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceController.class);

    @Autowired
    private DataSourceService dataSourceService;

    @ApiOperation(value = "增加数据源")
    @PostMapping("/add")
    @AccessLogAnnotation
    public Result<Object> create(@RequestBody DataSource dataSource,
                                 @RequestHeader(value = "projectId", required = false) Long projectId,
                                 @RequestHeader("env") String env) {
        Result<Object> data = new Result<>();

        if (projectId != null && dataSource != null) {
            dataSource.setProjectId(projectId);
        }
        if (StrUtil.isNotBlank(env) && dataSource != null) {
            dataSource.setEnv(env);
        }
        int saveFlag = dataSourceService.create(dataSource);
        if (saveFlag == -1) {
            data.setCode(Status.DATASOURCE_EXIST.getCode());
            data.setMsg(Status.DATASOURCE_EXIST.getMsg());
        } else {
            data.ok();
            data.setData(saveFlag);
        }
        return data;
    }

    @ApiOperation(value = "数据源测试")
    @PostMapping("/checkConnect")
    @AccessLogAnnotation
    public Result<Object> checkConnect(@RequestBody DataSourceDto dataSourceDto) {
        Result<Object> data = new Result<>();
        data.ok();
        Boolean checkConnectFlag = dataSourceService.checkConnect(dataSourceDto);
        data.setData(checkConnectFlag);
        return data;
    }


    @ApiOperation(value = "查询dataSource列表")
    @PostMapping("/list")
    @AccessLogAnnotation
    public Result<Object> list(@RequestBody DataSourceRto dataSourceRto) {
        Result<Object> data = new Result<>();
        data.ok();
        Pagination pageData = dataSourceService.list(dataSourceRto);
        data.setData(pageData);
        return data;
    }

    @ApiOperation(value = "查询dataSource")
    @PostMapping("/search")
    @AccessLogAnnotation
    public Result<Object> searchDataSource(@RequestBody DataSourceRto dataSourceRto) {
        Result<Object> data = new Result<>();
//        data.ok();
//        Pagination pageData = dataSourceService.list(dataSourceRto);
//        data.setData(pageData);
        return data;
    }

}
