package com.handsome.common.utiltest;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.handsome.common.util.HttpUtils;
import com.handsome.common.util.JSONUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FileUpload {

    @Test
    public void upload(){

        File jarFile = new File("/Users/wangchunshun/Desktop/be.jar");
        // 如果存在
        String restUploadPath = "http://10.250.148.24:8081/jars/upload";
        // 先进行远程调用一下，首先执行一下所有的门店，所有的门店从第一步
//        FileEntity fileEntity = new FileEntity(jarFile, ContentType.create("multipart/form-data", StandardCharsets.UTF_8));
//        BasicHeader contentType = new BasicHeader("ContentType", "application/java-archive");
//        try {
            String s = HttpUtils.uploadFile(restUploadPath,jarFile );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    @Test
    public void deleteAndUpload() throws URISyntaxException {
        File jarFile = new File("/Users/wangchunshun/Desktop/be.jar");
        String url = "http://10.250.148.24:8081/jars";
        String fileList = HttpUtils.doGet(new URI(url));
        ObjectNode jsonNodes = JSONUtils.parseObject(fileList);
        ArrayNode fileArrayNode = (ArrayNode)jsonNodes.get("files");
        fileArrayNode.forEach(fileNode->{
            if(jarFile.getName().equals(fileNode.get("name").textValue())){
                String idDeleteUrl = url + fileNode.get("id").textValue();
                try {
                    HttpUtils.doDelete(idDeleteUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // 重新上传新文件
        String restUploadPath = "http://10.250.148.24:8081/jars/upload";
        ObjectNode uploadRespNode = JSONUtils.parseObject(HttpUtils.uploadFile(restUploadPath,jarFile));
        String filename = uploadRespNode.get("filename").textValue();
        System.out.println(filename.substring(filename.lastIndexOf("/") + 1));
    }

}
