package com.handsome.common.util;

import com.handsome.common.enums.ResFsType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.handsome.common.Constants.*;
import static com.handsome.common.Constants.YYYYMMDDHHMMSS;

/**
 * file utils
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static final String DATA_BASEDIR = PropertyUtils.getProperty(DATA_BASEDIR_PATH, "/tmp/dpline");

    /**
     * 检查目录是否存在
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
     * 检查文件是否存在
     *
     * @param path
     * @param uploadType
     * @return
     * @throws IOException
     */
    public static boolean checkFileExist(String path, ResFsType uploadType) throws IOException {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        if (path.startsWith("file:///") || uploadType.equals(ResFsType.LOCAL)) {
            File file = new File(path);
            return file.exists() && file.isFile();
        }
        return MinioUtils.getInstance().exists(path);
    }

    public static void deleteFile(String path, ResFsType uploadType) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        if (path.startsWith("file:///") || uploadType.equals(ResFsType.LOCAL)) {
            deleteFile(path);
            return;
        }
        try {
            MinioUtils.getInstance().delete(path, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    public static void createDir(String path, ResFsType resFsType) throws IOException {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        if (path.startsWith("file:///") || resFsType.equals(ResFsType.LOCAL)) {
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            return;
        }
        MinioUtils.getInstance().mkdir(path);
    }


    /**
     * copy source InputStream to target file
     *
     * @param destFilename
     */
    public static void copyInputStreamToFile(InputStream fileInputStream, String sourceName, String destFilename) {
        try {
            org.apache.commons.io.FileUtils.copyInputStreamToFile(fileInputStream, new File(destFilename));
        } catch (IOException e) {
            logger.error("failed to copy file , {} is empty file", sourceName, e);
        }
    }

    /**
     * file to resource
     *
     * @param filename file name
     * @return resource
     * @throws MalformedURLException io exceptions
     */
    public static Resource file2Resource(String filename) throws MalformedURLException {
        Path file = Paths.get(filename);

        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            logger.error("file can not read : {}", filename);

        }
        return null;
    }

    public static String file2String(InputStream fileInputStream, String fileName) {
        try {
            return IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("file convert to string failed: {}", fileName);
        }

        return "";
    }

    /**
     * Deletes a file. If file is a directory, delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     *      (java.io.File methods returns a boolean)</li>
     * </ul>
     *
     * @param filename file name
     */
    private static boolean deleteFile(String filename) {
        return deleteQuietly(new File(filename));
    }


    /**
     * @return get suffixes for resource files that support online viewing
     */
    public static String getResourceViewSuffixs() {
        return PropertyUtils.getProperty(RESOURCE_VIEW_SUFFIXS, RESOURCE_VIEW_SUFFIXS_DEFAULT_VALUE);
    }

    public static boolean writeContent2File(String content, String filePath) {
        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists() && !distFile.getParentFile().mkdirs()) {
                logger.error("mkdir parent failed");
                return false;
            }
            IOUtils.write(content, new FileOutputStream(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
