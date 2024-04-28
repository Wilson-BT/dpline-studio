package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.dto.DplineFileDto;
import com.dpline.dao.dto.FileDto;
import com.dpline.dao.entity.File;
import com.dpline.dao.generic.GenericMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@DS("mysql")
@Repository
public interface FileMapper extends GenericMapper<File, Long> {

    List<DplineFileDto> getFileHistory4Common(@Param("userCode") String userCode);

    List<DplineFileDto> getFileHistory(@Param("userCode") String userCode);

    @MapKey("id")
    Map<Long,FileDto> selectFiles(FileDto fileDto);

    FileDto getDetailFile(FileDto fileDto);

    File getFileByName(File file);

    File queryFileByName(@Param("fileName") String fileName,
                         @Param("projectId") Long projectId,
                         @Param("id") Long id);
    @MapKey("id")
    Map<Long,File> checkState(List<Long> fileIds);

    FileDto queryBaseInfo(@Param("id") Long fileIds);

    Integer updateFolderId(@Param("fileId") long fileId,
                           @Param("folderId") long folderId);

    List<File> selectByFolderId(@Param("folderId") Long id);
}
