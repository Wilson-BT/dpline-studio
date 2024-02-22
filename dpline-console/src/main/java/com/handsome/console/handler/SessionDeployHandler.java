package com.handsome.console.handler;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.handsome.common.util.HttpUtils;
import com.handsome.common.util.JSONUtils;
import com.handsome.dao.entity.FlinkRunTaskInstance;
import com.handsome.dao.entity.FlinkTaskTagLog;
import com.handsome.common.util.TaskPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SessionDeployHandler implements DeployHandler {

    private static final Logger logger = LoggerFactory.getLogger(SessionDeployHandler.class);

    public SessionDeployHandler(){
    }

    public String deploy(FlinkTaskTagLog flinkTaskTagLog, FlinkRunTaskInstance flinkRunTaskInstance) throws URISyntaxException {
// 直接提交本地jar包到远程，返回jarId，后面需要根据jarId上传
//                Optional<KubernetesClient> k8sClientInstance = K8sClientManager.getK8sClientInstance(flinkRunTaskInstance.getKubePath(), flinkRunTaskInstance.getNameSpace());
//                // 判断 session 是否存在，然后将所有的目标
//                Boolean isExist = k8sClientInstance.map(client -> {
//                    Deployment deployment = client.apps().deployments().withName(flinkRunTaskInstance.getFlinkSessionName()).get();
//                    if (deployment == null) {
//                        logger.error("Session cluster [{}] is not appear on k8s NameSpace [{}]",
//                            flinkRunTaskInstance.getFlinkSessionName(),
//                            flinkRunTaskInstance.getFlinkSessionName());
//                        return false;
//                    }
//                    return true;
//                }).orElse(false);
        File jarFile = new File(flinkTaskTagLog.getMainJarPath());
        return uploadFileToSession(jarFile,flinkRunTaskInstance.getFlinkSessionName());
    }


    @Override
    public void clear(FlinkRunTaskInstance flinkRunTaskInstance) {
        logger.info("Session Mode deploy is over.");
    }

    /**
     * 更新文件到 Session
     *
     * @param jarFile
     * @param clusterId
     * @throws URISyntaxException
     */
    private String uploadFileToSession(File jarFile, String clusterId) throws URISyntaxException {
        // 先判断上传文件文件名是否存在，如果存在就删除
        String fileList = HttpUtils.doGet(new URI(TaskPath.getRestJarGetPath(clusterId)));
        ObjectNode jsonNodes = JSONUtils.parseObject(fileList);
        ArrayNode fileArrayNode = (ArrayNode)jsonNodes.get("files");
        fileArrayNode.forEach(fileNode->{
            if(jarFile.getName().equals(fileNode.get("name").textValue())){
                String idDeleteUrl = TaskPath.getRestJarDeletePath(clusterId,
                    fileNode.get("id").textValue());
                try {
                    HttpUtils.doDelete(idDeleteUrl);
                } catch (IOException e) {
                    logger.error("Jar File delete Failed, URL:{}",idDeleteUrl);
                    e.printStackTrace();
                }
            }
        });
        // 重新上传新文件
        String restUploadPath = TaskPath.getRestUploadPath(clusterId);
        ObjectNode uploadRespNode = JSONUtils.parseObject(HttpUtils.uploadFile(restUploadPath,jarFile));
        String filename = uploadRespNode.get("filename").textValue();
        return filename.substring(filename.lastIndexOf("/") + 1);
    }


}
