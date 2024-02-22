package com.handsome.common.util;

import com.handsome.common.Constants;
import com.handsome.common.enums.ResFsType;
import com.handsome.common.enums.ResourceType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * hadoop utils
 * single instance
 */
public class MinioUtils implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(MinioUtils.class);

    private static MinioUtils minioutils;
    private Configuration configuration;
    private FileSystem fs;

    private MinioUtils() {
        init();
    }

    public static MinioUtils getInstance() {
        if (minioutils == null) {
            synchronized (MinioUtils.class) {
                if (minioutils == null) {
                    minioutils = new MinioUtils();
                }
            }
        }
        return minioutils;
    }

    /**
     * init hadoop configuration
     */
    private void init() {
        try {
            configuration = new HdfsConfiguration();
            String resourceStorageType = PropertyUtils.getUpperCaseString(Constants.RESOURCE_STORAGE_TYPE);
            ResFsType resFsType = ResFsType.valueOf(resourceStorageType);
            System.setProperty(Constants.AWS_S3_V4, Constants.STRING_TRUE);
            //文件类型前缀+详细地址 file:/// 或者是 s3a://dpline
            configuration.set(Constants.FS_DEFAULTFS, PropertyUtils.getProperty(Constants.FS_DEFAULTFS));
            if (resFsType == ResFsType.S3) {
                configuration.set(Constants.FS_S3A_ENDPOINT, PropertyUtils.getProperty(Constants.FS_S3A_ENDPOINT));
                configuration.set(Constants.FS_S3A_ACCESS_KEY, PropertyUtils.getProperty(Constants.FS_S3A_ACCESS_KEY));
                configuration.set(Constants.FS_S3A_SECRET_KEY, PropertyUtils.getProperty(Constants.FS_S3A_SECRET_KEY));
            }
            fs = FileSystem.get(configuration);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @return Configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * @return DefaultFS
     */
    public String getDefaultFS() {
        return getConfiguration().get(Constants.FS_DEFAULTFS);
    }

    /**
     * cat file on mino
     *
     * @param minoFilePath mino file path
     * @return byte[] byte array
     * @throws IOException errors
     */
    public byte[] catFile(String minoFilePath) throws IOException {

        if (StringUtils.isBlank(minoFilePath)) {
            logger.error("mino file path:{} is blank", minoFilePath);
            return new byte[0];
        }

        try (FSDataInputStream fsDataInputStream = fs.open(new Path(minoFilePath))) {
            return IOUtils.toByteArray(fsDataInputStream);
        }
    }

    /**
     * cat file on mino
     *
     * @param minoFilePath mino file path
     * @param skipLineNums skip line numbers
     * @param limit        read how many lines
     * @return content of file
     * @throws IOException errors
     */
    public List<String> catFile(String minoFilePath, int skipLineNums, int limit) throws IOException {

        if (StringUtils.isBlank(minoFilePath)) {
            logger.error("mino file path:{} is blank", minoFilePath);
            return Collections.emptyList();
        }

        try (FSDataInputStream in = fs.open(new Path(minoFilePath))) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            Stream<String> stream = br.lines().skip(skipLineNums).limit(limit);
            return stream.collect(Collectors.toList());
        }

    }

    /**
     * make the given file and all non-existent parents into
     * directories. Has the semantics of Unix 'mkdir -p'.
     * Existence of the directory hierarchy is not an error.
     *
     * @param minoPath path to create
     * @return mkdir result
     * @throws IOException errors
     */
    public boolean mkdir(String minoPath) throws IOException {
        return fs.mkdirs(new Path(minoPath));
    }

    /**
     * copy files between FileSystems
     *
     * @param srcPath      source mino path
     * @param dstPath      destination mino path
     * @param deleteSource whether to delete the src
     * @param overwrite    whether to overwrite an existing file
     * @return if success or not
     * @throws IOException errors
     */
    public boolean copy(String srcPath, String dstPath, boolean deleteSource, boolean overwrite) throws IOException {
        return FileUtil.copy(fs, new Path(srcPath), fs, new Path(dstPath), deleteSource, overwrite, fs.getConf());
    }

    /**
     * the src file is on the local disk.  Add it to FS at
     * the given dst name.
     *
     * @param srcFile      local file
     * @param dstMinoPath  destination mino path
     * @param deleteSource whether to delete the src
     * @param overwrite    whether to overwrite an existing file
     * @return if success or not
     * @throws IOException errors
     */
    public boolean copyLocalToMino(String srcFile, String dstMinoPath, boolean deleteSource, boolean overwrite) throws IOException {
        Path srcPath = new Path(srcFile);
        Path dstPath = new Path(dstMinoPath);

        fs.copyFromLocalFile(deleteSource, overwrite, srcPath, dstPath);

        return true;
    }

    /**
     * copy mino file to local
     *
     * @param srcMinoFilePath source mino file path
     * @param dstFile         destination file
     * @param deleteSource    delete source
     * @param overwrite       overwrite
     * @return result of copy mino file to local
     * @throws IOException errors
     */
    public boolean copyMinoToLocal(String srcMinoFilePath, String dstFile, boolean deleteSource, boolean overwrite) throws IOException {
        Path srcPath = new Path(srcMinoFilePath);
        File dstPath = new File(dstFile);

        if (dstPath.exists()) {
            if (dstPath.isFile()) {
                if (overwrite) {
                    Files.delete(dstPath.toPath());
                }
            } else {
                logger.error("destination file must be a file");
            }
        }

        if (!dstPath.getParentFile().exists()) {
            dstPath.getParentFile().mkdirs();
        }

        return FileUtil.copy(fs, srcPath, dstPath, deleteSource, fs.getConf());
    }

    /**
     * delete a file
     *
     * @param minoFilePath the path to delete.
     * @param recursive    if path is a directory and set to
     *                     true, the directory is deleted else throws an exception. In
     *                     case of a file the recursive can be set to either true or false.
     * @return true if delete is successful else false.
     * @throws IOException errors
     */
    public boolean delete(String minoFilePath, boolean recursive) throws IOException {
        return fs.delete(new Path(minoFilePath), recursive);
    }

    /**
     * check if exists
     *
     * @param minoFilePath source file path
     * @return result of exists or not
     * @throws IOException errors
     */
    public boolean exists(String minoFilePath) throws IOException {
        return fs.exists(new Path(minoFilePath));
    }

    /**
     * Gets a list of files in the directory
     *
     * @param filePath file path
     * @return {@link FileStatus} file status
     * @throws Exception errors
     */
    public FileStatus[] listFileStatus(String filePath) throws Exception {
        try {
            return fs.listStatus(new Path(filePath));
        } catch (IOException e) {
            logger.error("Get file list exception", e);
            throw new Exception("Get file list exception", e);
        }
    }

    /**
     * Renames Path src to Path dst.  Can take place on local fs
     * or remote DFS.
     *
     * @param src path to be renamed
     * @param dst new path after rename
     * @return true if rename is successful
     * @throws IOException on failure
     */
    public boolean rename(String src, String dst) throws IOException {
        return fs.rename(new Path(src), new Path(dst));
    }

    /**
     * get data mino path
     *
     * @return data mino path
     */
    public static String getMinoDataBasePath() {
        return "";
    }

    /**
     * mino resource dir
     *
     * @param resourceType resource type
     * @return mino resource dir
     */
    public static String getMinoDir(ResourceType resourceType) {
        String minoDir = "";
        if (resourceType.equals(ResourceType.FILE)) {
            minoDir = getMinoResDir();
        } else if (resourceType.equals(ResourceType.UDF)) {
            minoDir = getMinoUdfDir();
        }
        return minoDir;
    }

    /**
     * mino resource dir
     *
     * @return mino resource dir
     */
    public static String getMinoResDir() {
        return String.format("%s/resources", getMinoDataBasePath());
    }

    /**
     * mino user dir
     *
     * @return mino resource dir
     */
    public static String getMinoTaskDir(String projectCode, String taskName) {
        return String.format("%s/task/%s/%s", getMinoDataBasePath(), projectCode, taskName);
    }

    /**
     * mino udf dir
     *
     * @return get udf dir on mino
     */
    public static String getMinoUdfDir() {
        return String.format("%s/udfs", getMinoDataBasePath());
    }

    /**
     * mino udf dir
     *
     * @return get udf dir on mino
     */
    public static String getMinoCheckPointDir() {
        return String.format("%s/checkpoints", getMinoDataBasePath());
    }

    /**
     * get mino file name
     *
     * @param resourceType resource type
     * @param fileName     file name
     * @return mino file name
     */
    public static String getMinoFileName(ResourceType resourceType, String fileName) {
        if (fileName.startsWith("/")) {
            fileName = fileName.replaceFirst("/", "");
        }
        return String.format("%s/%s", getMinoDir(resourceType), fileName);
    }

    /**
     * get absolute path and name for resource file on mino
     *
     * @param fileName file name
     * @return get absolute path and name for file on mino
     */
    public static String getMinoResourceFileName(String fileName) {
        if (fileName.startsWith("/")) {
            fileName = fileName.replaceFirst("/", "");
        }
        return String.format("%s/%s", getMinoResDir(), fileName);
    }

    /**
     * get absolute path and name for udf file on mino
     *
     * @param fileName file name
     * @return get absolute path and name for udf file on mino
     */
    public static String getMinoUdfFileName(String fileName) {
        if (fileName.startsWith("/")) {
            fileName = fileName.replaceFirst("/", "");
        }
        return String.format("%s/%s", getMinoUdfDir(), fileName);
    }

    /**
     * 获取带有s3 前缀的绝对路径
     *
     * @param path
     * @return
     */
    public static String getRealPathWithBucket(String path){
        if(path.startsWith("/")){
            return PropertyUtils.getProperty(Constants.FS_DEFAULTFS) + path;
        }
        return PropertyUtils.getProperty(Constants.FS_DEFAULTFS) + "/" + path;
    }

    /**
     * 获取checkpoint 地址
     *
     * @param rootPath
     * @param taskName
     * @param taskPipelineId
     * @return
     */
    public static String getMinoCheckPointPath(String rootPath, String taskName, String taskPipelineId) {
        return String.format("%s/%s/%s/%s", getMinoCheckPointDir(), rootPath, taskName, taskPipelineId);
    }

    /**
     * 获取checkpoint 地址
     *
     * @param rootPath
     * @param taskName
     * @param taskPipelineId
     * @return
     */
    public static String getMinoCheckPointPathWithBucket(String rootPath, String taskName, String taskPipelineId) {
        return getRealPathWithBucket(
                    getMinoCheckPointPath(
                        rootPath,taskName,taskPipelineId));
    }

    @Override
    public void close() throws IOException {
        if (fs != null) {
            try {
                fs.close();
            } catch (IOException e) {
                logger.error("Close HadoopUtils instance failed", e);
                throw new IOException("Close HadoopUtils instance failed", e);
            }
        }
    }

    /**
     * 检查路径是否存在
     *
     * @param path
     * @param uploadType
     * @return
     */
    public static boolean checkDirExist(String path, ResFsType uploadType) throws IOException {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        if (path.startsWith("file:///") || uploadType.equals(ResFsType.LOCAL)) {
            File file = new File(path);
            return file.exists() && file.isDirectory();
        }
        return MinioUtils.getInstance().exists(path);
    }

    /**
     * 获取到 最近一次的 check point 的地址
     *
     * @param projectCode
     * @param flinkTaskInstanceName
     */
    public String getLastCheckPointAddress(long projectCode, String flinkTaskInstanceName, String taskPipelineId) throws IOException {
        String minoCheckPointPath = getMinoCheckPointPath(String.valueOf(projectCode), flinkTaskInstanceName, taskPipelineId);
        // 首先获取到下面所有的文件
        RemoteIterator<LocatedFileStatus> locatedFileStatusRemoteIterator = fs.listFiles(new Path(minoCheckPointPath), true);
        long modification_time = 0L;
        Path path = new Path("");
        while (locatedFileStatusRemoteIterator.hasNext()) {
            LocatedFileStatus fileStatus = locatedFileStatusRemoteIterator.next();
            if(fileStatus.isDirectory()){
                continue;
            }

            if (fileStatus.getModificationTime() > modification_time){
                modification_time = fileStatus.getModificationTime();
                path = fileStatus.getPath().getParent();
            }
        }
        return getRealPathWithBucket(path.toString());
    }


    /**
     * 去掉前缀
     *
     * @param checkpointAddress
     */
    public static String formatCheckPointAddress(String checkpointAddress) {
        if (checkpointAddress.startsWith("s3://") || checkpointAddress.startsWith("s3a://")) {
            String replaceCheckpointAddress = checkpointAddress.replace("s3://", "").replace("s3a://", "");
            return replaceCheckpointAddress.substring(replaceCheckpointAddress.indexOf("/"));
        }
        return checkpointAddress;
    }

    /**
     * 增加前缀
     *
     * @param checkpointAddress
     */
    public static String addCheckPointAddressPrefix(String checkpointAddress) {
        if (checkpointAddress.startsWith("s3://")) {
            return checkpointAddress.replace("s3://", "s3a://");
        }
        if (!checkpointAddress.startsWith("s3://") && !checkpointAddress.startsWith("s3a://")) {
            return MinioUtils.getRealPathWithBucket(checkpointAddress);
        }
        return checkpointAddress;
    }
}
