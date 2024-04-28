package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.FileTag;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("mysql")
public interface FileTagMapper extends GenericMapper<FileTag,Long> {

    List<FileTag> getTagsByFileId(Pagination<FileTag> fileId);

    FileTag getLastTagsByFileId(@Param("fileId") Long fileId);

    FileTag getTagsByFileIdAndName(@Param("fileId") Long fileId,
                                   @Param("tagName") String fileTagName);

    Integer deleteByFileId(@Param("fileId") Long fileId);

    Integer selectFileTagCount(@Param("fileId") Long fileId);

    List<FileTag> selectAllTagByFileId(@Param("fileId") Long fileId);

    FileTag selectTagByFileIdAndVersion(@Param("fileId") Long fileId,
                                        @Param("tagName") String fileTagName);
}
