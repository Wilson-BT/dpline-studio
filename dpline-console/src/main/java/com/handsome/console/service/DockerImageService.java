package com.handsome.console.service;

import com.handsome.common.util.Result;
import com.handsome.dao.entity.User;

import java.util.Map;

public interface DockerImageService {


    Map<String, Object> createDockerImageInstance(User loginUser, String imageName, String aliasName, String registerAddress, String registerPassword, String registerUser);

    Result<Object> queryAllImages();

    Result<Object> updateDockerImage(User loginUser,int id, String imageName, String aliasName, String registerAddress, String registerPassword, String registerUser);

    Result<Object> deleteDockerImage(User loginUser,int id);
}
