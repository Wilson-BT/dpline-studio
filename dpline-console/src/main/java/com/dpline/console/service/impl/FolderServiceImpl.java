package com.dpline.console.service.impl;

import com.dpline.common.Constants;
import com.dpline.common.enums.Status;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.Result;
import com.dpline.console.service.GenericService;
import com.dpline.dao.dto.FolderDto;
import com.dpline.dao.entity.File;
import com.dpline.dao.entity.Folder;
import com.dpline.dao.mapper.FolderMapper;
import com.google.common.base.Preconditions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class FolderServiceImpl extends GenericService<Folder, Long> {

    @Autowired
    FileServiceImpl fileService;

    public FolderServiceImpl(@Autowired FolderMapper folderMapper) {
        super(folderMapper);
    }

    public FolderMapper getMapper() {
        return (FolderMapper) super.genericMapper;
    }

    /**
     * 构建目录树
     *
     * @param folderDto
     * @return
     */
    public Result<Object> queryFolder(FolderDto folderDto) {
        Result<Object> result = new Result<>();
        folderDto.setParentId(0L);
        List<FolderDto> parentFolder = genericFolder(folderDto);
        result.setData(parentFolder);
        result.ok();
        return result;
    }

    /**
     * 遍历结合递归，构建目录树
     * @param folderDto
     * @return
     */
    public List<FolderDto> genericFolder(FolderDto folderDto) {
        List<FolderDto> parentFolder = this.getMapper().queryFolder(folderDto);
        if (!CollectionUtils.isEmpty(parentFolder)) {
            for (FolderDto folder : parentFolder) {
                FolderDto folderDtoChild = new FolderDto();
                folderDtoChild.setProjectId(folder.getProjectId());
                folderDtoChild.setParentId(folder.getId());
                List<FolderDto> child = genericFolder(folderDtoChild);
                if (!CollectionUtils.isEmpty(child)) {
                    folder.setLeaf(false);
                    folder.setChildren(child);
                }
            }
        }
        return parentFolder;
    }

    /**
     * 创建目录
     *
     * @param folderDto
     * @return
     */
    @Transactional
    public Result<Object> addFolder(FolderDto folderDto) {
        Result<Object> result = new Result<>();
        Folder folder = new Folder();
        BeanUtils.copyProperties(folderDto,folder);
        Folder oldFolder = this.getMapper().getFolder(folder);
        if(Asserts.isNotNull(oldFolder)){
            putMsg(result, Status.DIR_EXIST_EXCEPTION);
            return result;
        }
        result.setData(insert(folder));
        result.ok();
        return result;
    }

    /**
     * 删除目录
     * @param folderDto
     * @return
     */
    public Result<Object> deleteFolder(FolderDto folderDto) {
        Result<Object> result = new Result<>();
        List<File> files = fileService.getMapper().selectByFolderId(folderDto.getId());
        List<Folder> folders = this.getMapper().selectByPid(folderDto.getId());
        // 下面有文件或者目录，则不能删除
        if(!files.isEmpty() || !folders.isEmpty()){
            putMsg(result,Status.TASK_DEFINE_EXIST_ERROR,files.get(0).getFileName());
            return result;
        }
        result.setData(this.getMapper().deleteById(folderDto.getId()));
        return result.ok();
    }

    public Result<Object> updateFolder(FolderDto folderDto) {
        Result<Object> result = new Result<>();
        Folder folder = new Folder();
        BeanUtils.copyProperties(folderDto,folder);
        result.ok();
        result.setData(updateSelective(folder));
        return result;
    }

    public Result<Object> searchFolder(FolderDto folderDto) {
        Result<Object> result = new Result<>();
        folderDto.setFolderName(folderDto.getFolderName());
        result.ok();
        result.setData(this.getMapper().searchFolderAndFile(folderDto));
        return result;
    }

    public Result<Object> searchFolders(FolderDto folderDto) {
        Result<Object> result = new Result<>();
        folderDto.setFolderName(folderDto.getFolderName());
        result.ok();
        result.setData(this.getMapper().searchFolder(folderDto));
        return result;
    }

    public void queryFolderIdAndChildFolderId(Long folderId, Set<Long> folderIds) {
        Preconditions.checkNotNull(folderIds,"文件夹集合对象不可以为空");
        if(Objects.isNull(folderId) || Constants.ZERO_FOR_LONG.equals(folderId)){
            return;
        }
        folderIds.add(folderId);
        Folder param = new Folder();
        param.setParentId(folderId);
        List<Folder> folders = selectAll(param);

        if (!CollectionUtils.isEmpty(folders)) {
            for (Folder folder : folders) {
                if (Objects.isNull(folder)) {
                    continue;
                }
                queryFolderIdAndChildFolderId(folder.getId(), folderIds);
            }
        }
    }


//
//    public Integer updateFolder(SdpFolderBO folderBO) {
//        SdpFolder folder = new SdpFolder();
//        BeanUtils.copyProperties(folderBO, folder);
//        folder.setBusinessFlag(null);
//        return updateSelective(folder);
//    }
//
//    public Integer deleteFolder(SdpFolderBO folderBO) {
//        SdpFile file = new SdpFile();
//        file.setFolderId(folderBO.getId());
//        List<SdpFile> files = fileService.selectAll(file);
//        if (!CollectionUtils.isEmpty(files)) {
//            throw new ApplicationException(ResponseCode.FOLDER_NOT_NULL);
//        }
//        return disable(SdpFolder.class, folderBO.getId());
//    }
//
//    public List<FolderVo> searchFolder(SdpFolderBO folderBO) {
//        folderBO.setFolderName(StrUtils.changeWildcard(folderBO.getFolderName()));
//        return this.getMapper().searchFolderAndFile(folderBO);
//    }
//
//    public List<FolderVo> searchFolders(SdpFolderBO folderBO) {
//        folderBO.setFolderName(StrUtils.changeWildcard(folderBO.getFolderName()));
//        return this.getMapper().searchFolder(folderBO);
//    }
//
//    public void queryFolderIdAndChildFolderId(Long folderId, Set<Long> folderIds) {
//        Preconditions.checkNotNull(folderIds, "文件夹集合对象不可以为空");
//        if (Objects.isNull(folderId) || CommonConstant.ZERO_FOR_LONG.equals(folderId)) {
//            return;
//        }
//        folderIds.add(folderId);
//
//        SdpFolder param = new SdpFolder();
//        param.setParentId(folderId);
//        param.setEnabledFlag(EnableType.ENABLE.getCode());
//        List<SdpFolder> sdpFolders = selectAll(param);
//
//        if (!CollectionUtils.isEmpty(sdpFolders)) {
//            for (SdpFolder folder : sdpFolders) {
//                if (Objects.isNull(folder)) {
//                    continue;
//                }
//                queryFolderIdAndChildFolderId(folder.getId(), folderIds);
//            }
//        }
//    }

}
