package com.dpline.dao.generic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Date;


public class GenericModel<PK> implements Serializable {
    private static final long serialVersionUID = 1L;


    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(type = IdType.ASSIGN_ID)
    protected PK id;


    /**
     * 创建人
     */
    protected String createUser;


    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    protected Date createTime;


    /**
     * 修改人
     */
    protected String updateUser;

    /**
     * 修改日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    protected Date updateTime;

    /**
     * 是否可用
     */
    protected Integer enabledFlag = 1;


    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(int enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }


    public PK getId() {
        return id;
    }

    public void setId(PK id) {
        this.id = id;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
        buffer.append("id='").append(getId()).append("'");
        buffer.append("]");

        return buffer.toString();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (!(o instanceof GenericModel)) {
            return false;
        }

        GenericModel other = (GenericModel) o;
        if (getId() != null && other.getId() != null) {
            if (getId() instanceof Comparable) {
                return ((Comparable) getId()).compareTo(other.getId()) == 0;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {

        int result = 17;

        if (getId() instanceof Comparable) {
            result = getId().hashCode();
        }
        return result;
    }
}