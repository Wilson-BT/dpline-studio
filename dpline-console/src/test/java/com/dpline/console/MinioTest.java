package com.dpline.console;

import com.dpline.common.util.TaskPathResolver;
import com.dpline.console.service.impl.SavePointServiceImpl;
import com.dpline.common.minio.Minio;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ActiveProfiles("api")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MinioTest {

    @Autowired
    Minio minio;

    @Autowired
    SavePointServiceImpl savePointServiceImpl;

    // checkpoint/projectId/jobId/000000000/chk-xxx/
    public static final Pattern REGEX_USER_NAME = Pattern.compile("flink-checkpoints/([a-zA-Z0-9_-]{3,45})/([a-zA-Z0-9_-]{3,40})/chk-([a-zA-Z0-9_-]+)/([a-zA-Z0-9_-]+)");

    @Test
    public void downLoad(){
        String localPath = "/tmp/dpline/task/9640278031136/10136815761696/extended/flink-doris-connector-1.14_2.11-1.0.0-SNAPSHOT.jar";
        String remotePath = "upload/jar/public/CONNECTOR/flink-doris-connector-1.14_2.11-1.0.0-SNAPSHOT/v1/flink-doris-connector-1.14_2.11-1.0.0-SNAPSHOT.jar";
        try {
            minio.downloadFile(remotePath,localPath);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 获取到路径下所有的 对象
     */
    @Test
    public void getAllObject(){

        String path = "flink-checkpoints";
        // 在path 路径下所有的对象
        try {
            List<Item> allObjects = minio.getAllObjectsByPrefix("flink",path, true);
            allObjects.forEach(x->{
                String objectName = x.objectName();
                Matcher matcher = REGEX_USER_NAME.matcher(objectName);
                if (matcher.find()) {
                    System.out.println(objectName);
                }
                return;
            });

            List<Item> collect = allObjects.stream().filter(item -> {
                Matcher matcher = REGEX_USER_NAME.matcher(item.objectName());
                if (matcher.find()) {
                    return true;
                }
                return false;
            }).sorted(new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    if (o1.lastModified().isBefore(o2.lastModified())) {
                        return 1;
                    }
                    if (o1.lastModified().isAfter(o2.lastModified())) {
                        return -1;
                    }
                    return 0;
                }
            }).collect(Collectors.toList());
            collect.forEach(item -> {
                System.out.println(item.objectName() + "<========>" + item.lastModified().toString());
            });
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void convertItemToJobSavepointTest() throws Exception {
        String path = "flink-checkpoints";
        List<Item> allObjects = minio.getAllObjectsByPrefix(path, true);
        allObjects.stream().filter(x -> {
            String objectName = x.objectName();
            Matcher matcher = REGEX_USER_NAME.matcher(objectName);
            if (matcher.find()) {
                System.out.println(matcher.group(1));
                System.out.println(matcher.group(2));
                System.out.println(matcher.group(3));
                System.out.println(matcher.group(4));
                return true;
            }
            return false;
        }).collect(Collectors.toList());
//        collectList.forEach(item -> {
//        });
//        savePointServiceImpl.convertItemToJobSavepoint(collectList,new ArrayList<>());
    }


    @Test
    public void putTmpFileToMinio(){
        String remotePath = "/checkpoint/9640278031136/10136815761696/00000000000000000000000000000000/chk-111/1641262974482.log";
        String localPath = "/Users/wangchunshun/Downloads/1641262974482.log";
        try {
            minio.putObject(localPath, remotePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void jobPathTest(){
        Pattern REGEX_USER_NAME = Pattern.compile("checkpoint/([a-zA-Z0-9_-]{3,20})/([a-zA-Z0-9._-]{3,45})/([a-zA-Z0-9_-]{3,35})/chk-([a-zA-Z0-9_-]+)/([a-zA-Z0-9_-]+)");
        String path = "checkpoint/9640278031136/10136815761696/00000000000000000000000000000000/chk-111/1641262974482.log";
        Matcher matcher = REGEX_USER_NAME.matcher(path);
        if(matcher.find()){
            int start = matcher.start(5);
            System.out.println(path.substring(0,start-1));
        }
    }

    /**
     * 推算 checkpoint 并排序
     *
     */
    @Test
    public void inferLastCheckPointTest(){
        String jobCheckPointDir = TaskPathResolver.getJobDefaultCheckPointDir(9640278031136L,10810350919968L, "8205c3b0592d07abd47d4198f4e8e80b");
        System.out.println(jobCheckPointDir);
        List<Item> items = null;
        try {
            items = savePointServiceImpl.getRemoteCheckPointList(jobCheckPointDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 对所有对象清洗目录，去重，转化为 JobSavepoint
        savePointServiceImpl.convertAndSort(items);
    }

}
