package com.dpline.dao.rto;

import com.dpline.dao.entity.FileTag;
import lombok.Data;

@Data
public class FileTagRto extends GenericRto<FileTag>{

    private Long fileTagId;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 注解
     */
    private String remark;

    /**
     * 文件 tag 标记名称
     */
    private String fileTagName;

    /**
     * 比较的类型，file or job
     */
    private String compareSource;

    private String jobName;

}
