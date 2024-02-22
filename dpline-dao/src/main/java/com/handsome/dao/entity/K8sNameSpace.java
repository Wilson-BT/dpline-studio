package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.handsome.common.enums.ReleaseState;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dpline_k8s_namespace")
public class K8sNameSpace {

    @TableId(value = "id")
    private long id;

    @TableField("name_space")
    private String nameSpace;

    @TableField("kube_path")
    private String kubePath;

    @TableField("description")
    private String description;

    @TableField("selector_lables")
    private String selectorLables;

    @TableField("service_account")
    private String serviceAccount;
    /**
     * If the k8s namespace is online.
     */
    @TableField("release_state")
    private ReleaseState releaseState;

    @TableField("env_type")
    private String envType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public K8sNameSpace(long id,
                        String nameSpace,
                        String kubePath,
                        String desc,
                        String selectorLables,
                        String serviceAccount,
                        Date createTime,
                        Date updateTime,
                        ReleaseState releaseState) {
        this.id = id;
        this.description = desc;
        this.nameSpace = nameSpace;
        this.kubePath = kubePath;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.serviceAccount = serviceAccount;
        this.selectorLables = selectorLables;
        this.releaseState = releaseState;
    }

    public K8sNameSpace() {
    }

    public static K8sNameSpaceBuilder builder() {
        return new K8sNameSpaceBuilder();
    }

    @Override
    public String toString() {
        return "K8sNameSpace{" +
                "id=" + id +
                ", nameSpace='" + nameSpace + '\'' +
                ", kubePath='" + kubePath + '\'' +
                ", description='" + description + '\'' +
                ", selectorLables='" + selectorLables + '\'' +
                ", serviceAccount='" + serviceAccount + '\'' +
                ", releaseState=" + releaseState +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public static class K8sNameSpaceBuilder {

        private long id;

        private String nameSpace;

        private String kubePath;

        private String description;

        private String serviceAccount;

        private String selectorLables;

        private Date createTime;

        private Date updateTime;

        private ReleaseState releaseState;

        public K8sNameSpaceBuilder() {
        }

        public K8sNameSpaceBuilder id(long id) {
            this.id = id;
            return this;
        }
        public K8sNameSpaceBuilder k8sNameSpace(String k8sNameSpace) {
            this.nameSpace = k8sNameSpace;
            return this;
        }

        public K8sNameSpaceBuilder kubePath(String kubePath) {
            this.kubePath = kubePath;
            return this;
        }

        public K8sNameSpaceBuilder desc(String description) {
            this.description = description;
            return this;
        }
        public K8sNameSpaceBuilder serviceAccount(String serviceAccount) {
            this.serviceAccount = serviceAccount;
            return this;
        }
        public K8sNameSpaceBuilder selectorLables(String selectorLables) {
            this.selectorLables = selectorLables;
            return this;
        }

        public K8sNameSpaceBuilder creatTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public K8sNameSpaceBuilder updateTime(Date updateTime) {
            this.updateTime = updateTime;
            return this;
        }
        public K8sNameSpaceBuilder online(ReleaseState releaseState) {
            this.releaseState = releaseState;
            return this;
        }

        public K8sNameSpace build() {
            return new K8sNameSpace(id,nameSpace, kubePath, description,selectorLables,serviceAccount,createTime, updateTime,releaseState);
        }

    }

}
