package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;

@TableName("dpline_folder")
@Data
public class Folder extends GenericModel<Long> {
    /**
     * 字段名称：目录名称
     *
     * 数据库字段信息:folder_name VARCHAR(255)
     */
    private String folderName;

    /**
     * 字段名称：父目录ID
     *
     * 数据库字段信息:parent_id BIGINT(19)
     */
    private Long parentId;

    /**
     * 字段名称：项目ID
     *
     * 数据库字段信息:project_id BIGINT(19)
     */
    private Long projectId;

    public Folder() {
    }


}
