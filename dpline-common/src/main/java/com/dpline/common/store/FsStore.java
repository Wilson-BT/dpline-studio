package com.dpline.common.store;

import com.dpline.common.enums.ResUploadType;
import io.minio.messages.Item;
import org.apache.hadoop.fs.FileStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public interface FsStore {
    public boolean WINDOWS =
            System.getProperty("os.name").startsWith("Windows");


    /**
     * @param path
     * @return
     * @throws IOException
     */
    boolean mkdir(String path) throws IOException;


    /**
     * @param fileName
     * @return
     * @throws IOException
     */
    boolean exists(String fileName) throws IOException;

    boolean notExists(String fileName) throws IOException;
    /**
     * delete the resource of  filePath
     * @param filePath
     * @param recursive
     * @return
     * @throws IOException
     */
    boolean delete(String filePath, boolean recursive) throws IOException;

    /**
     * copy the file from srcPath to dstPath
     * @param srcPath
     * @param dstPath
     * @param deleteSource if need to delete the file of srcPath
     * @param overwrite
     * @return
     * @throws IOException
     */
    boolean copy(String srcPath, String dstPath, boolean deleteSource, boolean overwrite) throws IOException;

    /**
     * upload the local srcFile to dstPath
     * @param srcFile
     * @param dstPath
     * @param deleteSource
     * @param overwrite
     * @return
     * @throws IOException
     */
    void upload(String srcFile, String dstPath, boolean deleteSource, boolean overwrite) throws IOException;

    /**
     * download the srcPath to local
     * @param srcFilePath the full path of the srcPath
     * @param dstFile
     * @param deleteSource
     * @throws IOException
     */
    void download(String srcFilePath, String dstFile, boolean deleteSource)throws IOException;

    void upload(InputStream inputStream, String hdfsFilePath) throws IOException;

    InputStream download(String hdfsFilePath) throws IOException;

    long getFileSize(String filePath) throws IOException;


    /**
     * return the storageType
     *
     * @return
     */
    ResUploadType returnStorageType();

    String getFileSystemPrefix();


    List<FileStatus> listAllFiles(String jobCheckPointPath, boolean recursive) throws Exception;
}
