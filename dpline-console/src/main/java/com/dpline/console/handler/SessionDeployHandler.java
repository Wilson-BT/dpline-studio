package com.dpline.console.handler;

import com.dpline.dao.dto.JobDto;
import com.dpline.dao.entity.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;

@Deprecated
public class SessionDeployHandler implements DeployHandler {

    private static final Logger logger = LoggerFactory.getLogger(SessionDeployHandler.class);

    public SessionDeployHandler(){
    }

    @Override
    public String deploy(JobDto jobDto) {
        return null;
    }

    @Override
    public void clear(Job job) {
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
//        String fileList = HttpUtils.doGet(new URI(TaskPathResolver.getRestJarGetPath(clusterId)));
//        ObjectNode jsonNodes = JSONUtils.parseObject(fileList);
//        ArrayNode fileArrayNode = (ArrayNode)jsonNodes.get("files");
//        fileArrayNode.forEach(fileNode->{
//            if(jarFile.getName().equals(fileNode.get("name").textValue())){
//                String idDeleteUrl = TaskPathResolver.getRestJarDeletePath(clusterId,
//                    fileNode.get("id").textValue());
//                try {
//                    HttpUtils.doDelete(idDeleteUrl);
//                } catch (IOException e) {
//                    logger.error("Jar File delete Failed, URL:{}",idDeleteUrl);
//                    e.printStackTrace();
//                }
//            }
//        });
//        // 重新上传新文件
//        String restUploadPath = TaskPathResolver.getRestUploadPath(clusterId);
//        ObjectNode uploadRespNode = JSONUtils.parseObject(HttpUtils.uploadFile(restUploadPath,jarFile));
//        String filename = uploadRespNode.get("filename").textValue();
//        return filename.substring(filename.lastIndexOf("/") + 1);
        return null;
    }


}
