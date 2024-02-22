package com.dpline.dao.dto;

import com.dpline.dao.entity.Folder;
import lombok.Data;

import java.util.List;

@Data
public class FolderDto extends Folder {

    /**
     * 是否是叶子节点
     */
    boolean leaf;

    /**
     * 子节点
     */
    List<FolderDto> children;
}
