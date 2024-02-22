package com.dpline.console.controller;

import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.FlinkVersionService;
import com.dpline.dao.entity.FlinkVersion;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.rto.FlinkVersionRto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(tags = "flink版本管理")
@RestController
@RequestMapping(value = "/system/motorVersion")
public class FlinkVersionController {

    private static final Logger logger = LoggerFactory.getLogger(FlinkVersionController.class);

    @Autowired
    private FlinkVersionService flinkVersionService;


    @ApiOperation(value = "增加flink版本数据")
    @PostMapping(value = "/add")
    @AccessLogAnnotation
    public Result<Object> create(@RequestBody FlinkVersion flinkVersion) {
        return flinkVersionService.create(flinkVersion);
    }

    @ApiOperation(value = "删除flink版本")
    @PostMapping(value = "/delete")
    @AccessLogAnnotation
    public Result<Integer> delete(@RequestBody FlinkVersion flinkVersion) {
        Result<Integer> data = new Result<>();
        data.ok();
        int deleteFlag = flinkVersionService.delete(flinkVersion);
        data.setData(deleteFlag);
        return data;
    }

    @ApiOperation(value = "修改状态")
    @PostMapping(value = "/updateState")
    @AccessLogAnnotation
    public Result<Integer> updateState(@RequestBody FlinkVersion flinkVersion) {
        Result<Integer> data = new Result<>();
        data.ok();
        int updateFlag = flinkVersionService.updateState(flinkVersion);
        data.setData(updateFlag);
        return data;
    }

    @ApiOperation(value = "修改状态")
    @PostMapping(value = "/updateInfo")
    @AccessLogAnnotation
    public Result<Object> updateInfo(@RequestBody FlinkVersion flinkVersion) {
        return flinkVersionService.updateInfo(flinkVersion);
    }

    @ApiOperation(value = "flink版本列表")
    @PostMapping(value = "/list")
    @AccessLogAnnotation
    public Result<Pagination> list(@RequestBody FlinkVersionRto flinkVersionRto) {
        Result<Pagination> data = new Result<>();
        data.ok();
        Pagination pageData = flinkVersionService.list(flinkVersionRto);
        data.setData(pageData);
        return data;
    }

    @ApiOperation(value = "创建文件，查询可使用的版本")
    @RequestMapping(value = "/enableList", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> flinkVersionList(){
        return flinkVersionService.queryFlinkVersion();
    }

    @ApiOperation(value = "创建文件，查询可使用的版本")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> flinkVersionSearch(@RequestParam("motorType") String motorType){
        return flinkVersionService.searchFlinkVersion(motorType);
    }

    @ApiOperation(value = "创建文件，查询可使用的版本")
    @RequestMapping(value = "/selectById", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> selectFlinkVersionById(@RequestParam("flinkVersionId")
                                                 Long flinkVersionId){
        return flinkVersionService.selectFlinkVersionById(flinkVersionId);
    }
}
