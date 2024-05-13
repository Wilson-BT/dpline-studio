package com.dpline.common.store;

import com.dpline.common.Constants;
import com.dpline.common.enums.ResUploadType;
import com.dpline.common.params.CommonProperties;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.common.util.HadoopUtil;
import com.dpline.common.util.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HdfsStore implements FsStore {

    private FileSystem fs;

    private Configuration configuration;

    private String hdfsUser;

    private static final Logger logger = LoggerFactory.getLogger(HdfsStore.class);



    public HdfsStore() {
        try {
            switch (returnStorageType()){
                case HDFS:
                    fs = createHdfsFileSystem(CommonProperties.getHadoopConfigDir());
                    break;
                case S3:
                    fs = createS3FileSystem(new HdfsConfiguration());
                    break;
                case NONE:
                    fs = createLocalFileSystem(new HdfsConfiguration());
                    break;
                default:
                    throw new RuntimeException("Unsupported storage type");
            }

        } catch (Exception exception) {
            logger.error(ExceptionUtil.exceptionToString(exception));
        }
    }
    public  FileSystem createHdfsFileSystem(String hadoopConfigDir){
        try {
            hdfsUser = PropertyUtils.getProperty(Constants.HDFS_ROOT_USER);
            configuration = HadoopUtil.initHadoopConfig(hadoopConfigDir);
            if (CommonProperties.loadKerberosConf(configuration)) {
                hdfsUser = "";
            }
            String defaultFS = CommonProperties.getDefaultFS();

            // first get key from core-site.xml hdfs-site.xml ,if null ,then try to get from properties file
            // the default is the local file system
            if (StringUtils.isBlank(defaultFS)) {
                logger.error("property:{} can not to be empty, please set!", Constants.FS_DEFAULTFS);
            } else {
                Map<String, String> fsRelatedProps = PropertyUtils.getPrefixedProperties("fs.");
                configuration.set(Constants.FS_DEFAULTFS, defaultFS);
                fsRelatedProps.forEach((key, value) -> configuration.set(key, value));
            }
            if (StringUtils.isEmpty(hdfsUser)) {
                logger.warn("resource.hdfs.root.user is not set value!");
                fs = FileSystem.get(configuration);
            } else {
                UserGroupInformation ugi = UserGroupInformation.createRemoteUser(hdfsUser);
                ugi.doAs((PrivilegedExceptionAction<Boolean>) () -> {
                    fs = FileSystem.get(configuration);
                    return true;
                });
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return fs;
    }

    public FileSystem createS3FileSystem(HdfsConfiguration configuration) throws IOException {
        this.configuration = configuration;
        System.setProperty(Constants.AWS_S3_V4, Constants.STRING_TRUE);
        configuration.set(Constants.FS_DEFAULTFS, PropertyUtils.getProperty(Constants.FS_DEFAULTFS));
        configuration.set(Constants.FS_S3A_ENDPOINT, PropertyUtils.getProperty(Constants.FS_S3A_ENDPOINT));
        configuration.set(Constants.FS_S3A_ACCESS_KEY, PropertyUtils.getProperty(Constants.FS_S3A_ACCESS_KEY));
        configuration.set(Constants.FS_S3A_SECRET_KEY, PropertyUtils.getProperty(Constants.FS_S3A_SECRET_KEY));
        return FileSystem.get(configuration);
    }

    /**
     * 创建本地文件系统
     * @return
     */
    public FileSystem createLocalFileSystem(Configuration configuration) throws IOException {
        String filePreffix = PropertyUtils.getProperty(Constants.FS_DEFAULTFS);
        if(filePreffix.startsWith("file")){
            configuration.set(Constants.FS_DEFAULTFS,filePreffix);
            return FileSystem.get(configuration);
        }
        throw new IllegalArgumentException("Resource.storage.type is None, but fs.defaultFs is not start with file.");
    }


    @Override
    public boolean mkdir(String path) throws IOException {
        return fs.mkdirs(new Path(path));
    }

    @Override
    public boolean exists(String fileName) throws IOException {
        return fs.exists(new Path(fileName));
    }

    @Override
    public boolean delete(String filePath, boolean recursive) throws IOException {
        return fs.delete(new Path(filePath), recursive);
    }

    @Override
    public boolean copy(String srcPath, String dstPath, boolean deleteSource, boolean overwrite) throws IOException {
        return FileUtil.copy(fs, new Path(srcPath),fs, new Path(dstPath), deleteSource, overwrite, configuration);
    }

    /**
     * @param srcFile
     * @param dstPath
     * @param deleteSource
     * @param overwrite
     * @throws IOException
     */
    @Override
    public void upload(String srcFile, String dstPath, boolean deleteSource, boolean overwrite) throws IOException {
        if(WINDOWS && !srcFile.startsWith("file:")){
            srcFile = String.format("file:\\%s",srcFile);
        }
        fs.copyFromLocalFile(deleteSource, overwrite, new Path(srcFile), new Path(dstPath));
    }

    @Override
    public void download(String srcFilePath, String dstFile, boolean deleteSource) throws IOException {
        if(WINDOWS && !dstFile.startsWith("file:")){
            dstFile = String.format("file:\\%s",dstFile);
        }
        fs.copyToLocalFile(deleteSource,new Path(srcFilePath), new Path(dstFile));
    }

    @Override
    public void upload(InputStream inputStream, String hdfsFilePath) throws IOException {
        try( FSDataOutputStream fsDataOutputStream = fs.create(new Path(hdfsFilePath))){
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                fsDataOutputStream.write(buffer, 0, bytesRead);
            }
            logger.info("File saved to HDFS {} successfully", hdfsFilePath);
        } catch (Exception e){
          logger.error(ExceptionUtil.exceptionToString(e));
        }
    }

    /**
     * download from remote
     * @param hdfsFilePath
     * @return
     * @throws IOException
     */
    @Override
    public InputStream download(String hdfsFilePath) throws IOException {
        return fs.open(new Path(hdfsFilePath));
    }

    @Override
    public long getFileSize(String filePath) throws IOException {
        return fs.getFileStatus(new Path(filePath)).getLen();
    }

    @Override
    public ResUploadType returnStorageType() {
        return CommonProperties.getResourceStorageType();
    }

    /**
     * get file system prefix
     * @return
     */
    @Override
    public String getFileSystemPrefix() {
        return CommonProperties.getDefaultFS();
    }

    @Override
    public List<FileStatus> listAllFiles(String jobCheckPointPath,boolean recursive) throws Exception {
        ArrayList<FileStatus> filePath = new ArrayList<>();
        if(recursive){
            return listFilesRecursively(jobCheckPointPath,filePath);
        }

        return listFilesWithNoRecursively(jobCheckPointPath,filePath);
    }

    public List<FileStatus> listFilesRecursively(String jobCheckPointPath, List<FileStatus> filePath) throws Exception {
        FileStatus[] fileStatuses = fs.listStatus(new Path(jobCheckPointPath));
        for (FileStatus fileStatus : fileStatuses) {
            if (fileStatus.isDirectory()) {
                listFilesRecursively(fileStatus.getPath().toString(),filePath); // 递归调用，继续遍历子目录
            } else {
                filePath.add(fileStatus);
            }
        }
        return filePath;
    }

    public List<FileStatus> listFilesWithNoRecursively(String jobCheckPointPath, List<FileStatus> filePath) throws Exception {
        FileStatus[] fileStatuses = fs.listStatus(new Path(jobCheckPointPath));
        for (FileStatus fileStatus : fileStatuses) {
            if (fileStatus.isDirectory()) {
                continue;
            } else {
                filePath.add(fileStatus);
            }
        }
        return filePath;
    }

}
