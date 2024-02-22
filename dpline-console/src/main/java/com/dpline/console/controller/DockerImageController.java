package com.dpline.console.controller;


import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.impl.DockerImageServiceImpl;
import com.dpline.dao.rto.DockerImageRto;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/system/docker")
public class DockerImageController extends BaseController {

    @Autowired
    private DockerImageServiceImpl dockerImageService;

    @PostMapping("/add")
    @AccessLogAnnotation
    public Result<Object> registerDockerImage(@RequestBody DockerImageRto dockerImageRto) {

        return dockerImageService.createDockerImageInstance(dockerImageRto);
    }

    @PostMapping("/update")
    @AccessLogAnnotation
    public Result<Object> updateDockerImage(@RequestBody DockerImageRto dockerImageRto) {

        return dockerImageService.updateDockerImage(dockerImageRto);
    }

    @PostMapping("/list")
    @AccessLogAnnotation
    public Result<Object> selectDockerImageList(@RequestBody DockerImageRto dockerImageRto){
        return dockerImageService.queryAllImages(dockerImageRto);
    }

    @PostMapping("/delete")
    @AccessLogAnnotation
    public Result<Object> deleteDockerImage(@RequestBody DockerImageRto dockerImageRto){
        return dockerImageService.deleteDockerImage(dockerImageRto);
    }

    @PostMapping("/queryDockerImage")
    @AccessLogAnnotation
    public Result<Object> listDockerImageByVersion(@RequestBody DockerImageRto dockerImageRto){
        return dockerImageService.listDockerImageByVersion(dockerImageRto);
    }

    @ApiOperation(value = "根据ID查询镜像")
    @PostMapping(value = "/selectById")
    @AccessLogAnnotation
    public Result<Object> selectClusterById(@RequestParam("imageId")
                                                Long clusterId){
        return dockerImageService.selectImageById(clusterId);
    }
}
