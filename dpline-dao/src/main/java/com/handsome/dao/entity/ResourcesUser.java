package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * resource user relation
 */
@TableName("dpline_relation_resources_user")
public class ResourcesUser {

  /**
   * id
   */
  @TableId(value="id", type=IdType.AUTO)
  private int id;

  /**
   * user id
   */
  private int userId;

  /**
   * resource id
   */
  private int resourcesId;

  /**
   * permission，默认为1(001)，如果是目录，则为读权限 110(4)，目录不可更改；如果为文件，则为读写权限111(7)
   */
  private int perm;

  /**
   * create time
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date createTime;

  /**
   * update time
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date updateTime;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getResourcesId() {
    return resourcesId;
  }

  public void setResourcesId(int resourcesId) {
    this.resourcesId = resourcesId;
  }

  public int getPerm() {
    return perm;
  }

  public void setPerm(int perm) {
    this.perm = perm;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  @Override
  public String toString() {
    return "ResourcesUser{" +
            "id=" + id +
            ", userId=" + userId +
            ", resourcesId=" + resourcesId +
            ", perm=" + perm +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            '}';
  }
}
