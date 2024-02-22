package com.dpline.console.controller;

import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.ClusterService;
import com.dpline.console.service.ClusterUserService;
import com.dpline.dao.entity.Cluster;
import com.dpline.dao.entity.ClusterUser;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.rto.ClusterRto;
import com.dpline.dao.rto.ClusterUserRto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "集群管理")
@RestController
@RequestMapping(value = "/system/clusterManagement")
public class ClusterController {

    private static final Logger logger = LoggerFactory.getLogger(ClusterController.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ClusterUserService clusterUserService;

    @ApiOperation(value = "增加引擎数据")
    @PostMapping(value = "/add")
    @AccessLogAnnotation
    public Result<Object> create(@RequestBody Cluster cluster) {
        return clusterService.create(cluster);
    }

    @ApiOperation(value = "删除引擎")
    @PostMapping(value = "/delete")
    @AccessLogAnnotation
    public Result<Object> delete(@RequestBody Cluster cluster) {
        return clusterService.delete(cluster);
    }

    @ApiOperation(value = "更改状态")
    @PostMapping(value = "/updateState")
    @AccessLogAnnotation
    public Result<Object> updateState(@RequestBody Cluster cluster) {
        return clusterService.updateState(cluster);
    }

    @ApiOperation(value = "更新引擎")
    @PostMapping(value = "/updateInfo")
    @AccessLogAnnotation
    public Result<Object> updateInfo(@RequestBody Cluster cluster) {
        return clusterService.updateInfo(cluster);
    }

    @ApiOperation(value = "引擎列表")
    @PostMapping(value = "/list")
    @AccessLogAnnotation
    public Result<Pagination> list(@RequestBody ClusterRto clusterRto) {
        Result<Pagination> data = new Result<>();
        data.ok();
        Pagination pageData = clusterService.list(clusterRto);
        data.setData(pageData);
        return data;
    }

    @ApiOperation(value = "引擎关联用户")
    @PostMapping(value = "/listUser")
    public Result<List<ClusterUser>> listUser(@RequestBody ClusterUser clusterUser) {
        Result<List<ClusterUser>> data = new Result<>();
        data.ok();
        List<ClusterUser> list = clusterUserService.list(clusterUser);
        data.setData(list);
        return data;
    }

    @ApiOperation(value = "增加引擎关联用户")
    @PostMapping(value = "/addClusterUser")
    @AccessLogAnnotation
    public Result<Object> addClusterUser(@RequestBody List<ClusterUser> clusterUserList) {
        return clusterUserService.addClusterUser(clusterUserList);
    }

    @ApiOperation(value = "删除引擎关联用户")
    @PostMapping(value = "/deleteClusterUser")
    @AccessLogAnnotation
    public Result<Integer> deleteClusterUser(@RequestBody ClusterUser clusterUser) {
        Result<Integer> data = new Result<>();
        data.ok();
        Integer count = clusterUserService.deleteClusterUser(clusterUser);
        data.setData(count);
        return data;
    }

    @ApiOperation(value = "根据ID查询集群")
    @PostMapping(value = "/selectById")
    @AccessLogAnnotation
    public Result<Object> selectClusterById(@RequestParam("clusterId")
                                                     Long clusterId){
        return clusterService.selectClusterById(clusterId);
    }


}
