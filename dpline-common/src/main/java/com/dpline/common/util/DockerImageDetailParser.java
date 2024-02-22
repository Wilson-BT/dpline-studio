package com.dpline.common.util;

import lombok.Data;

import java.util.Optional;

@Data
public class DockerImageDetailParser {

    /**
     * host
     */
    private String hostName;

    /**
     * image
     */
    private String imageName;

    /**
     * tag
     */
    private String tagName;

    /**
     * 输入的镜像
     */
    private String enterImage;


    /**
     * 完整的 docker image
     */
    private String wholeImage;

    private DockerImageDetailParser() {
    }

    public DockerImageDetailParser(String dockerImage) {
        this.enterImage = dockerImage;
    }

    /**
     * if no host, use params hostname
     * if use host,
     * @param hostName
     * @return
     */
    public Optional<DockerImageDetailParser> parse(String hostName) {
        if(StringUtils.isNotEmpty(hostName) && (hostName.startsWith("http://") || hostName.startsWith("https://"))){
            hostName = hostName.replace("http://","").replace("https://","");
        }
        if (StringUtils.isEmpty(this.enterImage)) {
            return Optional.empty();
        }
        try {
            String[] split = this.enterImage.split(":");
            if (split.length == 1) {
                this.tagName = "last";
            } else {
                this.tagName = split[1];
            }
            int i = split[0].indexOf("/");
            // 如果前面没有 hostName 前缀，需要设置为 host name
            if (i == -1 || !split[0].substring(0,i).equals(hostName)){
                this.hostName = hostName;
                this.imageName = split[0];
            } else {
                this.hostName = split[0].substring(0,i);
                this.imageName = split[0].substring(i+1);
            }
            return Optional.of(this);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    public String getWholeImage() {
        return this.hostName + "/" + this.getImageName() + ":" + this.tagName;
    }

    public static void main(String[] args) {
        Optional<DockerImageDetailParser> parse = new DockerImageDetailParser("data/flinkjob-flink-sync-binlog-retail-gms").parse("http://harbor.bjm6v.belle.lan");
        System.out.println(parse.get().getEnterImage());
        System.out.println(parse.get().getHostName());
        System.out.println(parse.get().getImageName());
        System.out.println(parse.get().getTagName());
        System.out.println(parse.get().getWholeImage());

    }

}
