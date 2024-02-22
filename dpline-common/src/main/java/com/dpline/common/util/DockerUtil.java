package com.dpline.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.*;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DockerUtil {

    private DockerImageDetailParser dockerImageParser;

    private static final String USER_NAME = "principal";

    private static final String PASS_WORD = "password";

    private static final String REPOSITORY_KEY = "repository";

    private static final String REPOSITORY_NAME = "repository_name";

    private static final String LOGIN_URL = "https://%s/c/login";

    private static final String IMAGE_TAG_URL = "https://%s/api/repositories/%s/tags";

    private static final String IMAGE_SEARCH_URL = "https://%s/api/search";

    private static final Logger logger = LoggerFactory.getLogger(DockerUtil.class);

    /**
     * 获取docker client
     *
     * @return
     */
//    public static DockerClient getDefaultInstance() throws InterruptedException {
//        DefaultDockerClientConfig dockerClientConf = DefaultDockerClientConfig
//                .createDefaultConfigBuilder()
//                .withDockerHost("tcp://10.250.36.64:2375")
//                .withDockerTlsVerify(false)
//                .withRegistryUrl("harbor.bjm6v.belle.lan")
//                .withRegistryUsername("data")
//                .withRegistryPassword("Data12345")
//                .build();
//
//        AuthConfig authConfig = new AuthConfig()
//                .withRegistryAddress("harbor.bjm6v.belle.lan")
//                .withUsername("data")
//                .withPassword("Data12345");
//
//        ApacheDockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
//                .dockerHost(dockerClientConf.getDockerHost())
//                .sslConfig(dockerClientConf.getSSLConfig())
//                .maxConnections(100)
//                .connectionTimeout(Duration.ofSeconds(30))
//                .responseTimeout(Duration.ofSeconds(45))
//                .build();
//
////        new DefaultDockerCmdExecFactory(dockerHttpClient, dockerClientConf.getObjectMapper());
//        DockerClient dockerClient = DockerClientImpl.getInstance(dockerClientConf, dockerHttpClient);
//
//        return null;
//    }
    public boolean checkAndPullImage(DockerClient dockerClient, AuthConfig authConfig, String imageName) {
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(imageName).withAuthConfig(authConfig);
        try {
            pullImageCmd.exec(
                    new PullImageResultCallback()
            ).awaitCompletion();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * @return
     */
    public AtomicBoolean checkDockerHarborImage(String user, String password, String hostName, String imageName) throws IOException {
        Optional<DockerImageDetailParser> dockerImageOptional = parseHostAndVersion(hostName,imageName);
        AtomicBoolean exists = new AtomicBoolean(false);
        if (dockerImageOptional.isPresent()) {
            // 保存好镜像对象
            this.dockerImageParser = dockerImageOptional.get();
                Map<String, String> postParams = new HashMap<>();
            String formatUrl = String.format(LOGIN_URL, this.dockerImageParser.getHostName());
            URI searchURI = null;
            try {
                searchURI = new URIBuilder(String.format(IMAGE_SEARCH_URL,this.dockerImageParser.getHostName()))
                    .addParameter("q", this.dockerImageParser.getImageName()).build();
                exists = whetherContainsImage(HttpUtils.doGet(searchURI), this.dockerImageParser);
            } catch (Exception e) {
                logger.warn("{} request failed,reason:{}", e.toString(), formatUrl);
                postParams.putIfAbsent(USER_NAME, user);
                postParams.putIfAbsent(PASS_WORD, password);
                HttpUtils.doPost(String.format(LOGIN_URL, hostName), postParams, null);
                exists = whetherContainsImage(HttpUtils.doGet(searchURI), dockerImageParser);
            }
        }
        return exists;
    }

    public DockerImageDetailParser getDockerImage() {
        return dockerImageParser;
    }

    /**
     * get parse image object
     *
     * @param imageName
     */
    private static Optional<DockerImageDetailParser> parseHostAndVersion(String hostName, String imageName) {
        return new DockerImageDetailParser(imageName).parse(hostName);
    }

    /**
     * judge whether exists
     *
     * @param content
     * @param dockerImage
     * @return
     */
    private static AtomicBoolean whetherContainsImage(String content, DockerImageDetailParser dockerImage) {
        AtomicBoolean exists = new AtomicBoolean(false);
        JsonNode repository = JSONUtils.parseObject(content).findPath(REPOSITORY_KEY);
        Iterator<JsonNode> iterator = repository.iterator();
        while (iterator.hasNext()) {
            JsonNode nextNode = iterator.next();
            if (nextNode.findPath(REPOSITORY_NAME).asText().equals(dockerImage.getImageName())) {
                // if image same, need judge Label/tag
                try {
                    String tagContent = HttpUtils.doGet(new URIBuilder(String.format(IMAGE_TAG_URL, dockerImage.getHostName(), dockerImage.getImageName())).addParameter("detail","1").build());
                    ArrayNode jsonNodes = JSONUtils.parseArray(tagContent);
                    Iterator<JsonNode> ite = jsonNodes.iterator();
                    while (ite.hasNext()) {
                        if (ite.next().findPath("name").asText().equals(dockerImage.getTagName())) {
                            exists.set(true);
                            break;
                        }
                    }
                    break;
                } catch (URISyntaxException e) {
                    logger.error("image [{}] on tag [{}] request failed", dockerImage.getEnterImage(), dockerImage.getTagName());
                    e.printStackTrace();
                }
            }
        }
        return exists;
    }

}
