package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.dto.FileFolderDto;
import com.dpline.dao.dto.FolderDto;
import com.dpline.dao.entity.Folder;
import com.dpline.dao.generic.GenericMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("mysql")
public interface FolderMapper extends GenericMapper<Folder, Long> {

    List<FolderDto> queryFolder(FolderDto folderDto);

    Folder getFolder(Folder folder);

    FileFolderDto searchFolderAndFile(FolderDto folderDto);

    List<FileFolderDto> searchFolder(FolderDto folderDto);

    List<Folder> selectByPid(@Param("parentId") Long id);
}
