//package com.handsome.common.utiltest;
//
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.handsome.common.util.JSONUtils;
//import com.handsome.common.enums.MinioResourceType;
//import io.minio.*;
//import io.minio.errors.*;
//import io.minio.messages.Bucket;
//import io.minio.messages.DeleteObject;
//import io.minio.messages.Item;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Optional;
//
//
//public class MinioUtils {
//
//
//    private static final Logger logger = LoggerFactory.getLogger(MinioUtils.class);
//    private static MinioClient minioClient;
//
//    private static String endpoint;
//    private static String bucketName;
//    private static String accessKey;
//    private static String secretKey;
//
//    public static final String SEPARATOR = "/";
//
//    private MinioUtils() {
//    }
//
//    public MinioUtils(String endpoint, String bucketName, String accessKey, String secretKey) {
//        MinioUtils.endpoint = endpoint;
//        MinioUtils.bucketName = bucketName;
//        MinioUtils.accessKey = accessKey;
//        MinioUtils.secretKey = secretKey;
//        createMinioClient();
//    }
//
//    /**
//     * 创建minioClient
//     */
//    public void createMinioClient() {
//        try {
//            if (null == minioClient) {
//                logger.info("minioClient create start");
//                minioClient = MinioClient.builder().endpoint("127.0.0.1",9000,false)
//                        .credentials(accessKey, secretKey)
//                        .region("dpline")
//                        .build();
//                createBucket();
//                logger.info("minioClient create end");
//            }
//        } catch (Exception e) {
//            logger.error("连接MinIO服务器异常：{}", e.getMessage());
//        }
//    }
//
//    /**
//     * 获取上传文件的基础路径
//     *
//     * @return url
//     */
//    public static String getBasisUrl() {
//        return endpoint + SEPARATOR + bucketName + SEPARATOR;
//    }
//
//    /**
//     * 初始化Bucket
//     *
//     * @throws Exception 异常
//     */
//    private static void createBucket()
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
//            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//        }
//    }
//
//    /**
//     * 验证bucketName是否存在
//     *
//     * @return boolean true:存在
//     */
//    public static boolean bucketExists()
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//    }
//
//    /**
//     * 创建bucket
//     *
//     * @param bucketName bucket名称
//     */
//    public static void createBucket(String bucketName)
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
//            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//        }
//    }
//
//    /**
//     * 获取存储桶策略
//     *
//     * @param bucketName 存储桶名称
//     * @return json
//     */
//    private ObjectNode getBucketPolicy(String bucketName)
//            throws IOException, InvalidKeyException, InvalidResponseException, BucketPolicyTooLargeException, NoSuchAlgorithmException, InternalException,  InsufficientDataException, ErrorResponseException, ServerException, XmlParserException {
//        String bucketPolicy = minioClient
//                .getBucketPolicy(GetBucketPolicyArgs.builder().bucket(bucketName).build());
//        return JSONUtils.parseObject(bucketPolicy);
//    }
//
//    /**
//     * 获取全部bucket
//     * <p>
//     * https://docs.minio.io/cn/java-client-api-reference.html#listBuckets
//     */
//    public static List<Bucket> getAllBuckets()
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        return minioClient.listBuckets();
//    }
//
//    /**
//     * 根据bucketName获取信息
//     *
//     * @param bucketName bucket名称
//     */
//    public static Optional<Bucket> getBucket(String bucketName)
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        return minioClient.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
//    }
//
//    /**
//     * 根据bucketName删除信息
//     *
//     * @param bucketName bucket名称
//     */
//    public static void removeBucket(String bucketName)
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
//    }
//
//    /**
//     * 判断文件是否存在
//     *
//     * @param
//     * @param objectName 对象
//     * @return true：存在
//     */
//    public static boolean doesObjectExist(String objectName) {
//        boolean exist = true;
//        try {
//            minioClient
//                    .statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
//        } catch (Exception e) {
//            exist = false;
//        }
//        return exist;
//    }
//
//    /**
//     * 判断文件夹是否存在
//     *
//     * @param
//     * @param objectName 文件夹名称（去掉/）
//     * @return true：存在
//     */
//    public static boolean doesFolderExist(String objectName) {
//        boolean exist = false;
//        objectName = StringUtils.removeStart(objectName, "/");
//        try {
//            Iterable<Result<Item>> results = minioClient.listObjects(
//                    ListObjectsArgs.builder().bucket(bucketName).prefix(objectName).recursive(false).build());
//            for (Result<Item> result : results) {
//                Item item = result.get();
//                // 如果是文件夹，item.objectName 后面会添加 / 作为后缀，两者需要保持一致
//                if (item.isDir()) {
//                    exist = objectName.equals(StringUtils.removeEnd(item.objectName(), "/"));
//                }
//            }
//        } catch (Exception e) {
//            exist = false;
//        }
//        return exist;
//    }
//
//    public static boolean doesExists(String objectName, MinioResourceType fileType){
//        if (fileType.equals(MinioResourceType.FILE)){
//            return doesObjectExist(objectName);
//        }
//        return doesFolderExist(objectName);
//    }
//
//
//    /**
//     * 根据文件前置查询文件
//     *
//     * @param bucketName bucket名称
//     * @param prefix 前缀
//     * @param recursive 是否递归查询
//     * @return MinioItem 列表
//     */
//    public static List<Item> getAllObjectsByPrefix(String bucketName, String prefix,
//                                                   boolean recursive)
//            throws ErrorResponseException, InsufficientDataException, InternalException,  InvalidKeyException, InvalidResponseException,
//            IOException, NoSuchAlgorithmException, XmlParserException, ServerException {
//        List<Item> list = new ArrayList<>();
//        Iterable<Result<Item>> objectsIterator = minioClient.listObjects(
//                ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(recursive).build());
//        if (objectsIterator != null) {
//            for (Result<Item> o : objectsIterator) {
//                Item item = o.get();
//                list.add(item);
//            }
//        }
//        return list;
//    }
//
//    /**
//     * 获取文件流
//     *
//     * @param bucketName bucket名称
//     * @param objectName 文件名称
//     * @return 二进制流
//     */
//    public static InputStream getObject(String bucketName, String objectName)
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        return minioClient
//                .getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
//    }
//
//    /**
//     * 断点下载
//     *
//     * @param bucketName bucket名称
//     * @param objectName 文件名称
//     * @param offset 起始字节的位置
//     * @param length 要读取的长度
//     * @return 流
//     */
//    public InputStream getObject(String bucketName, String objectName, long offset, long length)
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        return minioClient.getObject(
//                GetObjectArgs.builder().bucket(bucketName).object(objectName).offset(offset).length(length)
//                        .build());
//    }
//
//    /**
//     * 获取路径下文件列表
//     *
//     * @param bucketName bucket名称
//     * @param prefix 文件名称
//     * @param recursive 是否递归查找，如果是false,就模拟文件夹结构查找
//     * @return 二进制流
//     */
//    public static Iterable<Result<Item>> listObjects(String bucketName, String prefix,
//                                                     boolean recursive) {
//        return minioClient.listObjects(
//                ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(recursive).build());
//    }
//
//    /**
//     * 通过MultipartFile，上传文件
//     *
//     * @param bucketName 存储桶
//     * @param file 文件
//     * @param objectName 对象名
//     */
//    public static ObjectWriteResponse putObject(String bucketName, MultipartFile file,
//                                                String objectName, String contentType)
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        InputStream inputStream = file.getInputStream();
//        return minioClient.putObject(
//                PutObjectArgs.builder().bucket(bucketName).object(objectName).contentType(contentType)
//                        .stream(
//                                inputStream, inputStream.available(), -1)
//                        .build());
//
//    }
//
//
//
//
//    /**
//     * 上传本地文件
//     *
//     * @param bucketName 存储桶
//     * @param objectName 对象名称
//     * @param fileName 本地文件路径
//     */
//    public static ObjectWriteResponse putObject(String bucketName, String objectName,
//                                                String fileName)
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        return minioClient.uploadObject(
//                UploadObjectArgs.builder()
//                        .bucket(bucketName).object(objectName).filename(fileName).build());
//    }
//
//    /**
//     * 通过流上传文件
//     *
//     * @param objectName 文件对象
//     * @param inputStream 文件流
//     */
//    public static ObjectWriteResponse putObject(String objectName,
//                                                InputStream inputStream)
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        return minioClient.putObject(
//                PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
//                        inputStream, inputStream.available(), -1)
//                        .build());
//    }
//
//    /**
//     * 创建文件夹或目录
//     *
//     * @param objectName 目录路径
//     */
//    public static ObjectWriteResponse putDirObject(String objectName)
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        return minioClient.putObject(
//                PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
//                        new ByteArrayInputStream(new byte[]{}), 0, -1)
//                        .build());
//    }
//
////    /**
////     * 获取文件信息, 如果抛出异常则说明文件不存在
////     *
////     * @param bucketName bucket名称
////     * @param objectName 文件名称
////     */
////    public static ObjectStat statObject(String bucketName, String objectName)
////            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
////        return minioClient
////                .statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
////    }
//
//    /**
//     * 拷贝文件
//     *
//     * @param objectName 原来文件名称
//     * @param srcObjectName 目标文件名称
//     */
//    public static ObjectWriteResponse copyObject(String objectName,
//                                                 String srcObjectName)
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        return minioClient.copyObject(
//                CopyObjectArgs.builder()
//                        .source(CopySource.builder().bucket(bucketName).object(objectName).build())
//                        .bucket(bucketName)
//                        .object(srcObjectName)
//                        .build());
//    }
//
//    /**
//     * 删除文件
//     *
//     * @param objectName 文件名称
//     */
//    public static void removeObject(String objectName)
//            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException,  ErrorResponseException, ServerException, XmlParserException {
//        minioClient
//                .removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
//    }
//
//    /**
//     * 批量删除文件
//     *
//     * @param keys 需要删除的文件列表
//     * @return
//     */
//  /*public static Iterable<Result<DeleteError>> removeObjects(String bucketName, List<String> keys) {
//    List<DeleteObject> objects = new LinkedList<>();
//    keys.forEach(s -> {
//      objects.add(new DeleteObject(s));
//    });
//    return minioClient.removeObjects(
//        RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());
//  }*/
//    public static void removeObjects(List<String> keys) {
//        List<DeleteObject> objects = new LinkedList<>();
//        keys.forEach(s -> {
//            objects.add(new DeleteObject(s));
//            try {
//                removeObject(s);
//            } catch (Exception e) {
//                logger.error("批量删除失败！error:{}",e);
//            }
//        });
//    }
//
////    /**
////     * 获取文件外链
////     *
////     * @param bucketName bucket名称
////     * @param objectName 文件名称
////     * @param expires 过期时间 <=7 秒级
////     * @return url
////     */
////    public static String getPresignedObjectUrl(String bucketName, String objectName,
////                                               Integer expires)
////            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, InternalException, NoSuchAlgorithmException,  ErrorResponseException, ServerException, XmlParserException {
////        return minioClient.getPresignedObjectUrl(
////                GetPresignedObjectUrlArgs.builder()
////                        .method(Method.GET)
////                        .bucket(bucketName)
////                        .object(objectName)
////                        .expiry(2, TimeUnit.HOURS)
////                        .build());
////    }
//
////    /**
////     * 给presigned URL设置策略
////     *
////     * @param bucketName 存储桶
////     * @param objectName 对象名
////     * @param expires 过期策略
////     * @return map
////     */
////    public static Map<String, String> presignedGetObject(String bucketName, String objectName,
////                                                         Integer expires)
////            throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, InvalidExpiresRangeException, InternalException, NoSuchAlgorithmException,  ErrorResponseException, ServerException, XmlParserException {
////        PostPolicy policy = new PostPolicy(bucketName, objectName,
////                ZonedDateTime.now().plusDays(7));
////        policy.setContentType("image/png");
////        return minioClient.presignedPostPolicy(policy);
////    }
//
//
//    /**
//     * 将URLDecoder编码转成UTF8
//     *
//     * @param str
//     * @return
//     * @throws UnsupportedEncodingException
//     */
//    public static String getUtf8ByURLDecoder(String str) throws UnsupportedEncodingException {
//        String url = str.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
//        return URLDecoder.decode(url, "UTF-8");
//    }
//
//}
