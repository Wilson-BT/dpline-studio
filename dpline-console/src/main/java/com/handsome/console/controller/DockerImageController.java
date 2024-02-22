package com.handsome.console.controller;


import com.handsome.common.Constants;
import com.handsome.console.aspect.AccessLogAnnotation;
import com.handsome.console.exception.ApiException;
import com.handsome.console.service.DockerImageService;
import com.handsome.common.util.Result;
import com.handsome.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

import static com.handsome.common.enums.Status.DELETE_DOCKER_IMAGE_ERROR;
import static com.handsome.common.enums.Status.DOCKER_IMAGE_CREATE_ERROR;

@RestController
@RequestMapping("docker_image")
public class DockerImageController extends BaseController {

    @Autowired
    private DockerImageService dockerImageService;

    @PostMapping("")
    @ApiException(DOCKER_IMAGE_CREATE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> registerDockerImage(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                              @RequestParam("imageName") String imageName,
                                              @RequestParam("aliasName") String aliasName,
                                              @RequestParam("registerAddress") String registerAddress,
                                              @RequestParam("registerPassword") String registerPassword,
                                              @RequestParam("registerUser") String registerUser) {

        Map<String, Object> result = dockerImageService.createDockerImageInstance(loginUser, imageName, aliasName, registerAddress, registerPassword, registerUser);
        return returnDataList(result);
    }

    @PutMapping("{id}")
    @ApiException(DOCKER_IMAGE_CREATE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> updateDockerImage(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                              @PathVariable("id") int id,
                                              @RequestParam("image_name") String imageName,
                                              @RequestParam("alias_name") String aliasName,
                                              @RequestParam("registerAddress") String registerAddress,
                                              @RequestParam("registerPassword") String registerPassword,
                                              @RequestParam("register_user") String registerUser) {

        return dockerImageService.updateDockerImage(loginUser, id, imageName, aliasName, registerAddress, registerPassword, registerUser);
    }


    @GetMapping("")
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> selectDockerImageList(){
        return dockerImageService.queryAllImages();
    }


    @DeleteMapping("{id}")
    @ApiException(DELETE_DOCKER_IMAGE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> deleteDockerImage(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                            @PathVariable("id") int id){
        return dockerImageService.deleteDockerImage(loginUser,id);
    }




}
