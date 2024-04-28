package com.dpline.operator.service;

import com.dpline.common.enums.FileType;
import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.enums.ResFsType;
import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.request.FlinkDagRequest;
import com.dpline.common.request.FlinkDagResponse;
import com.dpline.common.request.JarResource;
import com.dpline.common.util.*;
import com.dpline.common.minio.Minio;
import com.dpline.flink.api.TaskOperateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;

@Service
public class FileDagService {

    @Autowired
    Minio minio;

    private static Logger logger = LoggerFactory.getLogger(FileDagService.class);

    public synchronized FlinkDagResponse getFileDag(FlinkDagRequest flinkDagRequest){
        System.out.println(JSONUtils.toJsonString(flinkDagRequest));
        FlinkDagResponse flinkDagResponse = new FlinkDagResponse();
        flinkDagResponse.setResponseStatus(ResponseStatus.FAIL);
        if (flinkDagRequest.getFileType().equals(FileType.SQL_STREAM)){
            flinkDagResponse.setMsg("SQL暂时不支持");
            return flinkDagResponse;
        }
        try {
            downLoadJar(flinkDagRequest);
            // 使用代理直接调用，客户端的接口
            flinkDagResponse = (FlinkDagResponse) TaskOperateProxy.execute(OperationsEnum.EXPLAIN, flinkDagRequest);
        } catch (Exception exception){
            String ex = ExceptionUtil.exceptionToString(exception);
            flinkDagResponse.setMsg(ex);
            logger.error("Run flink info error, {}",ex);
        } finally {
            try {
                // 删除 本地目录
                FileUtils.deleteDirectory(new java.io.File(flinkDagRequest.getMainJarResource().getLocalParentPath())
                    .getParentFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flinkDagResponse;
    }

    /**
     * 下载依赖 和 主 jar
     *
     * @param flinkDagRequest
     * @return
     * @throws MalformedURLException
     */
    private void downLoadJar(FlinkDagRequest flinkDagRequest) throws Exception {
        logger.info("DownLoad main jar. [{}]...",flinkDagRequest.getMainJarResource());
        downLoadJar(flinkDagRequest.getMainJarResource());
        logger.info("DownLoad depend jar. [{}]...",flinkDagRequest.getExtendedJarResources());
        for (JarResource jarResource: flinkDagRequest.getExtendedJarResources()) {
            downLoadJar(jarResource);
        }
    }
    /**
     *
     * @return
     */
    private void downLoadJar(JarResource jarResource) throws Exception {
        FileUtils.createDir(jarResource.getLocalParentPath(), ResFsType.LOCAL);
        minio.downloadFile(jarResource.getRemotePath(), jarResource.getJarLocalPath());
        logger.info("Jar => [{}] has been download.", jarResource.getJarLocalPath());
    }
}
