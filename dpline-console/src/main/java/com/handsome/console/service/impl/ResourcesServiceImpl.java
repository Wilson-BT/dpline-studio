package com.handsome.console.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.Files;
import com.handsome.common.Constants;
import com.handsome.common.enums.DraftTagType;
import com.handsome.common.enums.ResFsType;
import com.handsome.common.enums.ResourceType;
import com.handsome.common.enums.Status;
import com.handsome.common.util.JSONUtils;
import com.handsome.common.util.MinioUtils;
import com.handsome.common.util.PropertyUtils;
import com.handsome.common.util.RegexUtils;
import com.handsome.dao.entity.*;
import com.handsome.dao.mapper.*;
import com.handsome.dao.tree.ResourceComponent;
import com.handsome.dao.tree.ResourceTreeVisitor;
import com.handsome.dao.tree.TreeVisitor;
import com.handsome.console.exception.ServiceException;
import com.handsome.console.service.ResourcesService;
import com.handsome.common.util.FileUtils;
import com.handsome.console.util.PageInfo;
import com.handsome.common.util.Result;
//import org.apache.commons.beanutils.BeanMap;
import com.handsome.common.util.TaskPath;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.handsome.common.Constants.*;


/**
 * resources service impl
 */
@Service
public class ResourcesServiceImpl extends BaseServiceImpl implements ResourcesService {

    private static final Logger logger = LoggerFactory.getLogger(ResourcesServiceImpl.class);

    @Autowired
    private ResourceMapper resourcesMapper;

    @Autowired
    private UdfFuncMapper udfFunctionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResourceUserMapper resourceUserMapper;

    @Autowired
    private FlinkTagTaskResRelationMapper flinkTagTaskResRelationMapper;

    @Autowired
    private FlinkTaskTagLogMapper flinkTaskTagLogMapper;

    @Autowired
    private FlinkTaskDefinitionMapper flinkTaskDefinitionMapper;

    /**
     * create directory
     *
     * @param loginUser   login user
     * @param name        alias
     * @param description description
     * @param type        type
     * @param pid         parent id
     * @param currentDir  current directory
     * @return create directory result`
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> createDirectory(User loginUser,
                                          String name,
                                          String description,
                                          ResourceType type,
                                          int pid,
                                          String currentDir) {
        Result<Object> result = checkResourceUploadStartupState();
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }
        String fullName = currentDir.equals("/") ? String.format("%s%s", currentDir, name) : String.format("%s/%s", currentDir, name);
        result = verifyResource(loginUser, type, fullName, pid);
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }

        Date now = new Date();

        Resource resource = new Resource(pid, name, fullName, true, description, name, loginUser.getId(), type, 0, now, now);

        try {
            resourcesMapper.insert(resource);
            putMsg(result, Status.SUCCESS);
            Map<String, String> dataMap = JSONUtils.toMap(JSONUtils.toJsonString(resource));
            Map<String, Object> resultMap = new HashMap<>();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                if (!"class".equalsIgnoreCase(entry.getKey().toString())) {
                    resultMap.put(entry.getKey().toString(), entry.getValue());
                }
            }
            result.setData(resultMap);
        } catch (DuplicateKeyException e) {
            logger.error("resource directory {} has exist, can't recreate", fullName);
            putMsg(result, Status.RESOURCE_EXIST);
            return result;
        } catch (Exception e) {
            logger.error("resource already exists, can't recreate ", e);
            throw new ServiceException("resource already exists, can't recreate");
        }
        //create directory in hdfs
        createDirectory(fullName, type, result);
        return result;
    }

    /**
     * create resource
     *
     * @param loginUser  login user
     * @param name       alias
     * @param desc       description
     * @param file       file
     * @param type       type
     * @param pid        parent id
     * @param currentDir current directory
     * @return create result code
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> createResource(User loginUser,
                                         String name,
                                         String desc,
                                         ResourceType type,
                                         MultipartFile file,
                                         int pid,
                                         String currentDir) {
        Result<Object> result = checkResourceUploadStartupState();
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }

        result = verifyPid(loginUser, pid);
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }

        result = verifyFile(name, type, file);
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }

        // check resource name exists
        String fullName = currentDir.equals("/") ? String.format("%s%s", currentDir, name) : String.format("%s/%s", currentDir, name);
        if (checkResourceExists(fullName, 0, type.ordinal())) {
            logger.error("resource {} has exist, can't recreate", RegexUtils.escapeNRT(name));
            putMsg(result, Status.RESOURCE_EXIST);
            return result;
        }

        Date now = new Date();
        Resource resource = new Resource(pid, name, fullName, false, desc, file.getOriginalFilename(), loginUser.getId(), type, file.getSize(), now, now);

        try {
            resourcesMapper.insert(resource);
            putMsg(result, Status.SUCCESS);
            Map<String, String> dataMap = JSONUtils.toMap(JSONUtils.toJsonString(resource));
            Map<String, String> resultMap = new HashMap<>();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                if (!"class".equalsIgnoreCase(entry.getKey().toString())) {
                    resultMap.put(entry.getKey().toString(), entry.getValue());
                }
            }
            result.setData(resultMap);
        } catch (Exception e) {
            logger.error("resource already exists, can't recreate ", e);
            throw new ServiceException("resource already exists, can't recreate");
        }

        // fail upload
        if (!upload(fullName, file, type)) {
            logger.error("upload resource: {} file: {} failed.", RegexUtils.escapeNRT(name), RegexUtils.escapeNRT(file.getOriginalFilename()));
            putMsg(result, Status.S3_OPERATION_ERROR);
            throw new ServiceException(String.format("upload resource: %s file: %s failed.", name, file.getOriginalFilename()));
        }
        return result;
    }

    /**
     * create tenant dir if not exists
     *
     * @throws IOException if hdfs operation exception
     */
    @Override
    public void createDirIfNotExists() throws IOException {
        String resourcePath = MinioUtils.getMinoResDir();
        String udfsPath = MinioUtils.getMinoUdfDir();
        // init resource path and udf path
        MinioUtils.getInstance().mkdir(resourcePath);
        MinioUtils.getInstance().mkdir(udfsPath);
    }

    /**
     * check resource is exists
     *
     * @param fullName fullName
     * @param userId   user id
     * @param type     type
     * @return true if resource exists
     */
    private boolean checkResourceExists(String fullName, int userId, int type) {
        Boolean existResource = resourcesMapper.existResource(fullName, userId, type);
        return existResource == Boolean.TRUE;
    }

    /**
     * update resource
     *
     * @param loginUser  login user
     * @param resourceId resource id
     * @param name       name
     * @param desc       description
     * @param type       resource type
     * @param file       resource file
     * @return update result code
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> updateResource(User loginUser,
                                         int resourceId,
                                         String name,
                                         String desc,
                                         ResourceType type,
                                         MultipartFile file) {
        Result<Object> result = checkResourceUploadStartupState();
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }

        Resource resource = resourcesMapper.selectById(resourceId);
        if (resource == null) {
            putMsg(result, Status.RESOURCE_NOT_EXIST);
            return result;
        }
        if (!hasPerm(loginUser, resource.getUserId())) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }

        if (file == null && name.equals(resource.getAlias()) && desc.equals(resource.getDescription())) {
            putMsg(result, Status.SUCCESS);
            return result;
        }

        //check resource already exists
        String originFullName = resource.getFullName();
        String originResourceName = resource.getAlias();

        String fullName = String.format("%s%s", originFullName.substring(0, originFullName.lastIndexOf("/") + 1), name);
        if (!originResourceName.equals(name) && checkResourceExists(fullName, 0, type.ordinal())) {
            logger.error("resource {} already exists, can't recreate", name);
            putMsg(result, Status.RESOURCE_EXIST);
            return result;
        }

        result = verifyFile(name, type, file);
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }

        // verify whether the resource exists in storage
        // get the path of origin file in storage
        String originHdfsFileName = MinioUtils.getMinoFileName(resource.getType(), originFullName);
        try {
            if (!MinioUtils.getInstance().exists(originHdfsFileName)) {
                logger.error("{} not exist", originHdfsFileName);
                putMsg(result, Status.RESOURCE_NOT_EXIST);
                return result;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(String.format("update resource %s failed.", name));
        }
        // 文件重命名（非目录）
        if (!resource.isDirectory()) {
            //get the origin file suffix
            String originSuffix = Files.getFileExtension(originFullName);
            String suffix = Files.getFileExtension(fullName);
            boolean suffixIsChanged = false;
            if (StringUtils.isBlank(suffix) && StringUtils.isNotBlank(originSuffix)) {
                suffixIsChanged = true;
            }
            if (StringUtils.isNotBlank(suffix) && !suffix.equals(originSuffix)) {
                suffixIsChanged = true;
            }
            // 如果发生了改变
            if (suffixIsChanged) {
                // 如果想要修改的资源后缀发生改变，且已经为其他人赋予了权限，则不允许被更新，否则有可能影响其他人使用
                Map<String, Object> columnMap = new HashMap<>();
                columnMap.put("resources_id", resourceId);
                List<ResourcesUser> resourcesUsers = resourceUserMapper.selectByMap(columnMap);
                if (CollectionUtils.isNotEmpty(resourcesUsers)) {
                    List<Integer> userIds = resourcesUsers.stream().map(ResourcesUser::getUserId).collect(Collectors.toList());
                    List<User> users = userMapper.selectBatchIds(userIds);
                    String userNames = users.stream().map(User::getUserName).collect(Collectors.toList()).toString();
                    logger.error("resource is authorized to user {},suffix not allowed to be modified", userNames);
                    putMsg(result, Status.RESOURCE_IS_AUTHORIZED, userNames);
                    return result;
                }
            }
        }

        // updateResource data
        Date now = new Date();

        resource.setAlias(name);
        resource.setFileName(name);
        resource.setFullName(fullName);
        resource.setDescription(desc);
        resource.setUpdateTime(now);
        if (file != null) {
            resource.setSize(file.getSize());
        }

        try {
            resourcesMapper.updateById(resource);
            if (resource.isDirectory()) {
                List<Integer> childrenResource = listAllChildren(resource, false);
                if (CollectionUtils.isNotEmpty(childrenResource)) {
                    String matcherFullName = Matcher.quoteReplacement(fullName);
                    List<Resource> childResourceList;
                    Integer[] childResIdArray = childrenResource.toArray(new Integer[childrenResource.size()]);
                    List<Resource> resourceList = resourcesMapper.listResourceByIds(childResIdArray);
                    childResourceList = resourceList.stream().map(t -> {
                        t.setFullName(t.getFullName().replaceFirst(originFullName, matcherFullName));
                        t.setUpdateTime(now);
                        return t;
                    }).collect(Collectors.toList());
                    resourcesMapper.batchUpdateResource(childResourceList);

                    if (ResourceType.UDF.equals(resource.getType())) {
// UDF 操作,如果是目录的话。需要将所有的UDF下资源的resourceName进行统一更新
                        List<UdfFunc> udfFuncs = udfFunctionMapper.listUdfByResourceId(childResIdArray);
                        if (CollectionUtils.isNotEmpty(udfFuncs)) {
                            udfFuncs = udfFuncs.stream().map(t -> {
                                t.setResourceName(t.getResourceName().replaceFirst(originFullName, matcherFullName));
                                t.setUpdateTime(now);
                                return t;
                            }).collect(Collectors.toList());
                            udfFunctionMapper.batchUpdateUdfFunc(udfFuncs);
                        }
                    }
                }
            } else if (ResourceType.UDF.equals(resource.getType())) {
// UDF 管理,如果更新的是UDF 资源包 ，需要将所有的UDF下资源的resourceName进行统一更新
                List<UdfFunc> udfFuncs = udfFunctionMapper.listUdfByResourceId(new Integer[]{resourceId});
                if (CollectionUtils.isNotEmpty(udfFuncs)) {
                    udfFuncs = udfFuncs.stream().map(t -> {
                        t.setResourceName(fullName);
                        t.setUpdateTime(now);
                        return t;
                    }).collect(Collectors.toList());
                    udfFunctionMapper.batchUpdateUdfFunc(udfFuncs);
                }
            }

            putMsg(result, Status.SUCCESS);
            Map<String, String> dataMap = JSONUtils.toMap(JSONUtils.toJsonString(resource));
            Map<String, String> resultMap = new HashMap<>();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                if (!Constants.CLASS.equalsIgnoreCase(entry.getKey().toString())) {
                    resultMap.put(entry.getKey().toString(), entry.getValue());
                }
            }
            result.setData(resultMap);
        } catch (Exception e) {
            logger.error(Status.UPDATE_RESOURCE_ERROR.getMsg(), e);
            throw new ServiceException(Status.UPDATE_RESOURCE_ERROR);
        }

        // if name unchanged, return directly without moving on HDFS
        if (originResourceName.equals(name) && file == null) {
            return result;
        }

        if (file != null) {
            // fail upload
            if (!upload(fullName, file, type)) {
                logger.error("upload resource: {} file: {} failed.", name, RegexUtils.escapeNRT(file.getOriginalFilename()));
                putMsg(result, Status.S3_OPERATION_ERROR);
                throw new ServiceException(String.format("upload resource: %s file: %s failed.", name, file.getOriginalFilename()));
            }
            if (!fullName.equals(originFullName)) {
                try {
                    MinioUtils.getInstance().delete(originHdfsFileName, false);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw new ServiceException(String.format("delete resource: %s failed.", originFullName));
                }
            }
            return result;
        }

        // get the path of dest file in hdfs
        String destHdfsFileName = MinioUtils.getMinoFileName(resource.getType(), fullName);

        try {
            logger.info("start hdfs copy {} -> {}", originHdfsFileName, destHdfsFileName);
            MinioUtils.getInstance().copy(originHdfsFileName, destHdfsFileName, true, true);
        } catch (Exception e) {
            logger.error(MessageFormat.format("hdfs copy {0} -> {1} fail", originHdfsFileName, destHdfsFileName), e);
            putMsg(result, Status.HDFS_COPY_FAIL);
            throw new ServiceException(Status.HDFS_COPY_FAIL);
        }

        return result;
    }

    private Result<Object> verifyFile(String name, ResourceType type, MultipartFile file) {
        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        if (file != null) {
            // file is empty
            if (file.isEmpty()) {
                logger.error("file is empty: {}", RegexUtils.escapeNRT(file.getOriginalFilename()));
                putMsg(result, Status.RESOURCE_FILE_IS_EMPTY);
                return result;
            }
            // file suffix
            String fileSuffix = Files.getFileExtension(file.getOriginalFilename());
            String nameSuffix = Files.getFileExtension(name);

            // determine file suffix
            if (!(StringUtils.isNotEmpty(fileSuffix) && fileSuffix.equalsIgnoreCase(nameSuffix))) {
                // 文件名后缀需要保持一致
                logger.error("rename file suffix and original suffix must be consistent: {}", RegexUtils.escapeNRT(file.getOriginalFilename()));
                putMsg(result, Status.RESOURCE_SUFFIX_FORBID_CHANGE);
                return result;
            }
            //If resource type is UDF, only jar packages are allowed to be uploaded, and the suffix must be .jar
            if (Constants.UDF.equals(type.name()) && !JAR.equalsIgnoreCase(fileSuffix)) {
                logger.error(Status.UDF_RESOURCE_SUFFIX_NOT_JAR.getMsg());
                putMsg(result, Status.UDF_RESOURCE_SUFFIX_NOT_JAR);
                return result;
            }
            if (file.getSize() > Constants.MAX_FILE_SIZE) {
                logger.error("file size is too large: {}", RegexUtils.escapeNRT(file.getOriginalFilename()));
                putMsg(result, Status.RESOURCE_SIZE_EXCEED_LIMIT);
                return result;
            }
        }
        return result;
    }

    /**
     * query resources list paging
     *
     * @param loginUser login user
     * @param type      resource type
     * @param searchVal search value
     * @param pageNo    page number
     * @param pageSize  page size
     * @return resource list page
     */
    @Override
    public Result queryResourceListPaging(User loginUser, int directoryId, ResourceType type, String searchVal, Integer pageNo, Integer pageSize) {
        Result result = new Result();
        Page<Resource> page = new Page<>(pageNo, pageSize);
        int userId = loginUser.getId();
        if (isAdmin(loginUser)) {
            userId = 0;
        }
        if (directoryId != -1) {
            Resource directory = resourcesMapper.selectById(directoryId);
            if (directory == null) {
                putMsg(result, Status.RESOURCE_NOT_EXIST);
                return result;
            }
        }

        List<Integer> resourcesIds = resourceUserMapper.queryResourcesIdListByUserIdAndPerm(userId, 0);
        IPage<Resource> resourceIPage = resourcesMapper.queryResourcePaging(page, userId, directoryId, type.ordinal(), searchVal, resourcesIds);
        PageInfo<Resource> pageInfo = new PageInfo<>(pageNo, pageSize);
        pageInfo.setTotal((int) resourceIPage.getTotal());
        pageInfo.setTotalList(resourceIPage.getRecords());
        result.setData(pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * create directory
     *
     * @param fullName  full name
     * @param type      resource type
     * @param result    Result
     */
    private void createDirectory(String fullName, ResourceType type, Result<Object> result) {
        String directoryName = MinioUtils.getMinoFileName(type, fullName);
        String resourceRootPath = MinioUtils.getMinoDir(type);
        try {
            if (!MinioUtils.getInstance().exists(resourceRootPath)) {
                createDirIfNotExists();
            }

            if (!MinioUtils.getInstance().mkdir(directoryName)) {
                logger.error("create resource directory {} of hdfs failed", directoryName);
                putMsg(result, Status.S3_OPERATION_ERROR);
                throw new ServiceException(String.format("create resource directory: %s failed.", directoryName));
            }
        } catch (Exception e) {
            logger.error("create resource directory {} of minio failed", directoryName);
            putMsg(result, Status.S3_OPERATION_ERROR);
            throw new ServiceException(String.format("create resource directory: %s failed.", directoryName));
        }
    }

    /**
     * upload file to hdfs
     *
     * @param fullName full name
     * @param file     file
     */
    private boolean upload(String fullName, MultipartFile file, ResourceType type) {
        // save to local
        String fileSuffix = Files.getFileExtension(file.getOriginalFilename());
        String nameSuffix = Files.getFileExtension(fullName);

        // determine file suffix
        if (!(StringUtils.isNotEmpty(fileSuffix) && fileSuffix.equalsIgnoreCase(nameSuffix))) {
            return false;
        }
        // random file name
        String localFilename = TaskPath.getUploadFilename(UUID.randomUUID().toString());

        // save file to hdfs, and delete original file
        String hdfsFilename = MinioUtils.getMinoFileName(type, fullName);
        String resourcePath = MinioUtils.getMinoDir(type);
        try {
            // if tenant dir not exists
            if (!MinioUtils.getInstance().exists(resourcePath)) {
                createDirIfNotExists();
            }
            FileUtils.copyInputStreamToFile(file.getInputStream(),file.getOriginalFilename(),localFilename);
            MinioUtils.getInstance().copyLocalToMino(localFilename, hdfsFilename, true, true);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            FileUtils.deleteFile(localFilename, ResFsType.LOCAL);
        }
        return false;
    }



    /**
     * query resource list
     *
     * @param loginUser login user
     * @param type      resource type
     * @return resource list
     */
    @Override
    public Result<Object> queryResourceList(User loginUser, ResourceType type) {
        Result<Object> result = new Result<>();

        // 检查名下所有资源、包括被赋权的以及自己创建的。
        List<Resource> allResourceList = queryAuthoredResourceList(loginUser, type);
        TreeVisitor resourceTreeVisitor = new ResourceTreeVisitor(allResourceList);
        putMsg(result, Status.SUCCESS);
        result.setData(resourceTreeVisitor.visit().getChildren());
        return result;
    }


    /**
     * delete resource
     *
     * @param loginUser  login user
     * @param resourceId resource id
     * @return delete result code
     * @throws IOException exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> delete(User loginUser, int resourceId) throws IOException {
        Result<Object> result = checkResourceUploadStartupState();
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }

        // get resource by id
        Resource resource = resourcesMapper.selectById(resourceId);
        if (resource == null) {
            putMsg(result, Status.RESOURCE_NOT_EXIST);
            return result;
        }
        if (!hasPerm(loginUser, resource.getUserId())) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }

        // get all child id over this node
        List<Integer> allChildren = listAllChildren(resource, true);
        Integer[] needDeleteResourceIdArray = allChildren.toArray(new Integer[allChildren.size()]);
        // if res is used by task, can't delete
        Optional<FlinkTagTaskResRelation> firstResValue = flinkTagTaskResRelationMapper
                .queryByResId(allChildren)
                .stream()
                .findFirst();
        if (firstResValue.isPresent()) {
            FlinkTagTaskResRelation tagTaskResRelation = firstResValue.get();
            if (tagTaskResRelation.getDraftTagType().equals(DraftTagType.TASK_TAG)) {
                putMsg(result, Status.TASK_RESOURCE_IS_BOUND,
                        flinkTaskTagLogMapper.queryNameById(tagTaskResRelation.getId()));
                return result;
            }
            putMsg(result, Status.TASK_RESOURCE_IS_BOUND,
                    flinkTaskDefinitionMapper.queryNameById(tagTaskResRelation.getId()));
            return result;
        }
        // if res is used by udf ,can't delete
        Optional<UdfFunc> firstUdfValue = udfFunctionMapper
                .listUdfByResourceId(needDeleteResourceIdArray)
                .stream()
                .findFirst();
        if (firstUdfValue.isPresent()) {
            putMsg(result,Status.UDF_RESOURCE_IS_BOUND,firstUdfValue.get().getFuncName());
            return result;
        }
        // get mino file by type
        String minoFilename = MinioUtils.getMinoFileName(resource.getType(), resource.getFullName());

        //delete data in database
        resourcesMapper.deleteIds(needDeleteResourceIdArray);
        resourceUserMapper.deleteResourceUserArray(0, needDeleteResourceIdArray);

        //delete file on hdfs
        MinioUtils.getInstance().delete(minoFilename, true);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * verify resource by name and type
     *
     * @param loginUser login user
     * @param fullName  resource full name
     * @param type      resource type
     * @return true if the resource name not exists, otherwise return false
     */
    @Override
    public Result<Object> verifyResourceName(String fullName, ResourceType type, User loginUser) {
        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        if (checkResourceExists(fullName, 0, type.ordinal())) {
            logger.error("resource type:{} name:{} has exist, can't create again.", type, RegexUtils.escapeNRT(fullName));
            putMsg(result, Status.RESOURCE_EXIST);
        } else {
            try {
                String hdfsFilename = MinioUtils.getMinoFileName(type, fullName);
                if (MinioUtils.getInstance().exists(hdfsFilename)) {
                    logger.error("resource type:{} name:{} has exist in hdfs {}, can't create again.", type, RegexUtils.escapeNRT(fullName), hdfsFilename);
                    putMsg(result, Status.RESOURCE_FILE_EXIST, hdfsFilename);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                putMsg(result, Status.S3_OPERATION_ERROR);
            }
        }

        return result;
    }

    /**
     * verify resource by full name or pid and type
     *
     * @param fullName resource full name
     * @param id       resource id
     * @param type     resource type
     * @return true if the resource full name or pid not exists, otherwise return false
     */
    @Override
    public Result<Object> queryResource(String fullName, Integer id, ResourceType type) {
        Result<Object> result = new Result<>();
        if (StringUtils.isBlank(fullName) && id == null) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR);
            return result;
        }
        if (StringUtils.isNotBlank(fullName)) {
            List<Resource> resourceList = resourcesMapper.queryResource(fullName, type.ordinal());
            if (CollectionUtils.isEmpty(resourceList)) {
                putMsg(result, Status.RESOURCE_NOT_EXIST);
                return result;
            }
            putMsg(result, Status.SUCCESS);
            result.setData(resourceList.get(0));
        } else {
            Resource resource = resourcesMapper.selectById(id);
            if (resource == null) {
                putMsg(result, Status.RESOURCE_NOT_EXIST);
                return result;
            }
            Resource parentResource = resourcesMapper.selectById(resource.getPid());
            if (parentResource == null) {
                putMsg(result, Status.RESOURCE_NOT_EXIST);
                return result;
            }
            putMsg(result, Status.SUCCESS);
            result.setData(parentResource);
        }
        return result;
    }

    /**
     * view resource file online
     *
     * @param resourceId  resource id
     * @param skipLineNum skip line number
     * @param limit       limit
     * @return resource content
     */
    @Override
    public Result<Object> readResource(int resourceId, int skipLineNum, int limit) {
        Result<Object> result = checkResourceUploadStartupState();
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }

        // get resource by id
        Resource resource = resourcesMapper.selectById(resourceId);
        if (resource == null) {
            putMsg(result, Status.RESOURCE_NOT_EXIST);
            return result;
        }
        //check preview or not by file suffix
        String nameSuffix = Files.getFileExtension(resource.getAlias());
        String resourceViewSuffixs = FileUtils.getResourceViewSuffixs();
        if (StringUtils.isNotEmpty(resourceViewSuffixs)) {
            List<String> strList = Arrays.asList(resourceViewSuffixs.split(","));
            if (!strList.contains(nameSuffix)) {
                logger.error("resource suffix {} not support view,  resource id {}", nameSuffix, resourceId);
                putMsg(result, Status.RESOURCE_SUFFIX_NOT_SUPPORT_VIEW);
                return result;
            }
        }

        // hdfs path
        String hdfsFileName = MinioUtils.getMinoResourceFileName(resource.getFullName());
        logger.info("resource hdfs path is {}", hdfsFileName);
        try {
            if (MinioUtils.getInstance().exists(hdfsFileName)) {
                List<String> content = MinioUtils.getInstance().catFile(hdfsFileName, skipLineNum, limit);

                putMsg(result, Status.SUCCESS);
                Map<String, Object> map = new HashMap<>();
                map.put(ALIAS, resource.getAlias());
                map.put(CONTENT, String.join("\n", content));
                result.setData(map);
            } else {
                logger.error("read file {} not exist in hdfs", hdfsFileName);
                putMsg(result, Status.RESOURCE_FILE_NOT_EXIST, hdfsFileName);
            }

        } catch (Exception e) {
            logger.error("Resource {} read failed", hdfsFileName, e);
            putMsg(result, Status.S3_OPERATION_ERROR);
        }

        return result;
    }

    /**
     * create resource file online
     * <p>
     * 在线创建文件
     *
     * @param loginUser  login user
     * @param type       resource type
     * @param fileName   file name
     * @param fileSuffix file suffix
     * @param desc       description
     * @param content    content
     * @param pid        pid
     * @param currentDir current directory
     * @return create result code
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> onlineCreateResource(User loginUser, ResourceType type, String fileName, String fileSuffix, String desc, String content, int pid, String currentDir) {
        Result<Object> result = checkResourceUploadStartupState();
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }

        //check file suffix
        String nameSuffix = fileSuffix.trim();
        String resourceViewSuffixs = FileUtils.getResourceViewSuffixs();
        if (StringUtils.isNotEmpty(resourceViewSuffixs)) {
            List<String> strList = Arrays.asList(resourceViewSuffixs.split(","));
            if (!strList.contains(nameSuffix)) {
                logger.error("resource suffix {} not support create", nameSuffix);
                putMsg(result, Status.RESOURCE_SUFFIX_NOT_SUPPORT_VIEW);
                return result;
            }
        }

        String name = fileName.trim() + "." + nameSuffix;
        String fullName = currentDir.equals("/") ? String.format("%s%s", currentDir, name) : String.format("%s/%s", currentDir, name);
        result = verifyResource(loginUser, type, fullName, pid);
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }

        // save data
        Date now = new Date();
        Resource resource = new Resource(pid, name, fullName, false, desc, name, loginUser.getId(), type, content.getBytes().length, now, now);

        resourcesMapper.insert(resource);

        putMsg(result, Status.SUCCESS);
        Map<String, String> dataMap = JSONUtils.toMap(JSONUtils.toJsonString(resource));
        Map<String, String> resultMap = new HashMap<>();
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            if (!Constants.CLASS.equalsIgnoreCase(entry.getKey().toString())) {
                resultMap.put(entry.getKey().toString(), entry.getValue());
            }
        }
        result.setData(resultMap);

        result = uploadContentToMino(fullName, content);
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            throw new ServiceException(result.getMsg());
        }
        return result;
    }

    private Result<Object> checkResourceUploadStartupState() {
        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        // if resource upload startup
        if (!PropertyUtils.getResRemoteType()) {
            logger.error("resource upload startup state: {}", PropertyUtils.getResRemoteType());
            putMsg(result, Status.S3_NOT_STARTUP);
            return result;
        }
        return result;
    }

    /**
     * 校验资源文件minio中是否存在
     *
     * @param loginUser
     * @param type
     * @param fullName
     * @param pid
     * @return
     */
    private Result<Object> verifyResource(User loginUser, ResourceType type, String fullName, int pid) {
        Result<Object> result = verifyResourceName(fullName, type, loginUser);
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }
        return verifyPid(loginUser, pid);
    }

    private Result<Object> verifyPid(User loginUser, int pid) {
        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        if (pid != -1) {
            Resource parentResource = resourcesMapper.selectById(pid);
            if (parentResource == null) {
                putMsg(result, Status.PARENT_RESOURCE_NOT_EXIST);
                return result;
            }
            if (!hasPerm(loginUser, parentResource.getUserId())) {
                putMsg(result, Status.USER_NO_OPERATION_PERM);
                return result;
            }
        }
        return result;
    }

    /**
     * updateProcessInstance resource
     *
     * @param resourceId resource id
     * @param content    content
     * @return update result cod
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> updateResourceContent(int resourceId, String content) {
        Result<Object> result = checkResourceUploadStartupState();
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            return result;
        }

        Resource resource = resourcesMapper.selectById(resourceId);
        if (resource == null) {
            logger.error("read file not exist,  resource id {}", resourceId);
            putMsg(result, Status.RESOURCE_NOT_EXIST);
            return result;
        }
        //check can edit by file suffix
        String nameSuffix = Files.getFileExtension(resource.getAlias());
        String resourceViewSuffixs = FileUtils.getResourceViewSuffixs();
        if (StringUtils.isNotEmpty(resourceViewSuffixs)) {
            List<String> strList = Arrays.asList(resourceViewSuffixs.split(","));
            if (!strList.contains(nameSuffix)) {
                logger.error("resource suffix {} not support updateProcessInstance,  resource id {}", nameSuffix, resourceId);
                putMsg(result, Status.RESOURCE_SUFFIX_NOT_SUPPORT_VIEW);
                return result;
            }
        }

        resource.setSize(content.getBytes().length);
        resource.setUpdateTime(new Date());
        resourcesMapper.updateById(resource);

        result = uploadContentToMino(resource.getFullName(), content);
        if (!result.getCode().equals(Status.SUCCESS.getCode())) {
            throw new ServiceException(result.getMsg());
        }
        return result;
    }

    /**
     * @param resourceName resource name
     * @param content      content
     * @return result
     */
    private Result<Object> uploadContentToMino(String resourceName, String content) {
        Result<Object> result = new Result<>();
        String localFilename = "";
        String hdfsFileName = "";
        try {
            localFilename = TaskPath.getUploadFilename(UUID.randomUUID().toString());

            if (!FileUtils.writeContent2File(content, localFilename)) {
                // write file fail
                logger.error("file {} fail, content is {}", localFilename, RegexUtils.escapeNRT(content));
                putMsg(result, Status.RESOURCE_NOT_EXIST);
                return result;
            }

            // get resource file hdfs path
            hdfsFileName = MinioUtils.getMinoResourceFileName(resourceName);
            String resourcePath = MinioUtils.getMinoResDir();
            logger.info("resource hdfs path is {}, resource dir is {}", hdfsFileName, resourcePath);

            MinioUtils minioUtils = MinioUtils.getInstance();
            if (!minioUtils.exists(resourcePath)) {
                // create if tenant dir not exists
                createDirIfNotExists();
            }
            if (minioUtils.exists(hdfsFileName)) {
                minioUtils.delete(hdfsFileName, false);
            }

            minioUtils.copyLocalToMino(localFilename, hdfsFileName, true, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setCode(Status.S3_OPERATION_ERROR.getCode());
            result.setMsg(String.format("copy %s to hdfs %s fail", localFilename, hdfsFileName));
            return result;
        }
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * download file
     *
     * @param resourceId resource id
     * @return resource content
     * @throws IOException exception
     */
    @Override
    public org.springframework.core.io.Resource downloadResource(int resourceId) throws IOException {
        // if resource upload startup
        if (!PropertyUtils.getResRemoteType()) {
            logger.error("resource upload startup state: {}", PropertyUtils.getResRemoteType());
            throw new ServiceException("hdfs not startup");
        }

        Resource resource = resourcesMapper.selectById(resourceId);
        if (resource == null) {
            logger.error("download file not exist,  resource id {}", resourceId);
            return null;
        }
        if (resource.isDirectory()) {
            logger.error("resource id {} is directory,can't download it", resourceId);
            throw new ServiceException("can't download directory");
        }

        int userId = resource.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            logger.error("user id {} not exists", userId);
            throw new ServiceException(String.format("resource owner id %d not exist", userId));
        }

        String hdfsFileName = MinioUtils.getMinoFileName(resource.getType(), resource.getFullName());

        String localFileName = TaskPath.getDownloadFilename(resource.getAlias());
        logger.info("resource hdfs path is {}, download local filename is {}", hdfsFileName, localFileName);

        MinioUtils.getInstance().copyMinoToLocal(hdfsFileName, localFileName, false, true);
        return FileUtils.file2Resource(localFileName);
    }

    /**
     * list all file
     *
     * @param loginUser login user
     * @param userId    user id
     * @return
     */
    @Override
    public Map<String, Object> authorizeResourceTree(User loginUser, Integer userId) {
        HashMap<String, Object> result = new HashMap<>();
        if (isNotAdmin(loginUser, result)) {
            return result;
        }
        List<ResourceComponent> list;
        List<Resource> resourceList = resourcesMapper.queryResourceExceptUserId(loginUser.getId());
        if (CollectionUtils.isNotEmpty(resourceList)) {
            TreeVisitor treeVisitor = new ResourceTreeVisitor(resourceList);
            list = treeVisitor.visit().getChildren();
        } else {
            list = new ArrayList<>(0);
        }
        result.put(Constants.DATA_LIST, list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 获取所有未授权的资源树，首先获取到非自己创建的资源，然后去掉资源用户关系表中有权限的资源
     *
     * @param loginUser login user
     * @param userId    user id
     * @return
     */
    @Override
    public Map<String, Object> unauthorizedFile(User loginUser, Integer userId) {
        Map<String, Object> result = new HashMap<>();
        if (isNotAdmin(loginUser, result)) {
            return result;
        }
        List<Resource> resourceList = resourcesMapper.queryResourceExceptUserId(userId);
        List<Resource> list;
        if (resourceList != null && !resourceList.isEmpty()) {
            Set<Resource> resourceSet = new HashSet<>(resourceList);
            // 获取到在user、资源关系表中 有所有权限的资源列表（只有非目录可写）
            List<Resource> authedResourceList = queryResourceList(userId, AUTHORIZE_ALL_PERM);
            // 在非作者的list列表中去除这部分
            getAuthorizedResourceList(resourceSet, authedResourceList);
            list = new ArrayList<>(resourceSet);
        } else {
            list = new ArrayList<>(0);
        }
        TreeVisitor visitor = new ResourceTreeVisitor(list);
        result.put(Constants.DATA_LIST, visitor.visit().getChildren());
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 获取到赋权的文件
     *
     * @param loginUser login user
     * @param userId    user id
     * @return
     */
    @Override
    public Result<Object> authorizedFile(User loginUser, Integer userId) {
        Result<Object> result = new Result<>();
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }
        List<Resource> resourcesList = queryResourceList(userId, AUTHORIZE_ALL_PERM);
        ResourceTreeVisitor resourceTreeVisitor = new ResourceTreeVisitor(resourcesList);
        String jsonTreeStr = JSONUtils.toJsonString(resourceTreeVisitor.visit().getChildren(), SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        logger.info(jsonTreeStr);
        result.setData(resourceTreeVisitor.visit().getChildren());
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * get authorized resource list
     *
     * @param resourceSet        resource set
     * @param authedResourceList authorized resource list
     */
    private void getAuthorizedResourceList(Set<?> resourceSet, List<?> authedResourceList) {
        Set<?> authedResourceSet;
        if (CollectionUtils.isNotEmpty(authedResourceList)) {
            authedResourceSet = new HashSet<>(authedResourceList);
            resourceSet.removeAll(authedResourceSet);
        }
    }

    /**
     * list all children id
     *
     * @param resource    resource
     * @param containSelf whether add self to children list
     * @return all children id
     */
    List<Integer> listAllChildren(Resource resource, boolean containSelf) {
        List<Integer> childList = new ArrayList<>();
        if (resource.getId() != -1 && containSelf) {
            childList.add(resource.getId());
        }

        if (resource.isDirectory()) {
            listAllChildren(resource.getId(), childList);
        }
        return childList;
    }

    /**
     * list all children id
     *
     * @param resourceId resource id
     * @param childList  child list
     */
    void listAllChildren(int resourceId, List<Integer> childList) {
        List<Integer> children = resourcesMapper.listChildren(resourceId);
        for (int childId : children) {
            childList.add(childId);
            listAllChildren(childId, childList);
        }
    }

    /**
     * 已经赋权的 和 自己创建的
     * query authored resource list (own and authorized)
     *
     * @param loginUser login user
     * @param type      ResourceType
     * @return all authored resource list
     */
    private List<Resource> queryAuthoredResourceList(User loginUser, ResourceType type) {
        List<Resource> relationResources;
        int userId = loginUser.getId();
        if (isAdmin(loginUser)) {
            userId = 0;
            relationResources = new ArrayList<>();
        } else {
            // 先查找赋予了权限的资源
            relationResources = queryResourceList(userId, 0);
        }
        // 再查找 自己创作的资源
        List<Resource> ownResourceList = resourcesMapper.queryResourceListAuthored(userId, type.ordinal());
        // 两个合并
        ownResourceList.addAll(relationResources);

        return ownResourceList;
    }

    /**
     * perm 为 0 ，则 直接使用userId，perm不为0，则 使用perm 和 userId ，目录为读权限、文件为读写权限
     * <p>
     * query resource list by userId and perm
     * 获取所有有权限的资源ID列表
     *
     * @param userId userId
     * @param perm   perm
     * @return resource list
     */
    private List<Resource> queryResourceList(Integer userId, int perm) {
        List<Integer> resIds = resourceUserMapper.queryResourcesIdListByUserIdAndPerm(userId, perm);
        return CollectionUtils.isEmpty(resIds) ? new ArrayList<>() : resourcesMapper.queryResourceListById(resIds);
    }

}
