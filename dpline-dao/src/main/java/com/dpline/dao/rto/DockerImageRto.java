package com.dpline.dao.rto;

import com.dpline.dao.entity.DockerImage;
import lombok.Data;


@Data
public class DockerImageRto extends GenericRto<DockerImage> {

    private long id;

    private String shortName;

    private String imageName;

    private String registerAddress;

    private String registerUser;

    private String registerPassword;

    private String description;

    private String motorType;

    private long motorVersionId;


}
