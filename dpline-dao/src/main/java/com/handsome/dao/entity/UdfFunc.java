
package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.handsome.common.util.JSONUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Date;

/**
 * udf function
 */
@TableName("dpline_udfs")
public class UdfFunc {
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
     * udf function name
     */
    private String funcName;

    /**
     * udf class name
     */
    private String className;

    /**
     * udf argument types
     */
    private String argTypes;

    /**
     * udf data base
     */
    private String database;

    /**
     * udf description
     */
    private String description;

    /**
     * resource id
     */
    private int resourceId;

    /**
     * resource name
     */
    private String resourceName;


    private String flinkVersionId;

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

    public String getFlinkVersionId() {
        return flinkVersionId;
    }

    public void setFlinkVersionId(String flinkVersionId) {
        this.flinkVersionId = flinkVersionId;
    }

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

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(String argTypes) {
        this.argTypes = argTypes;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UdfFunc udfFunc = (UdfFunc) o;

        if (id != udfFunc.id) {
            return false;
        }
        return !(funcName != null ? !funcName.equals(udfFunc.funcName) : udfFunc.funcName != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (funcName != null ? funcName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return JSONUtils.toJsonString(this);
    }

    public static  class UdfFuncDeserializer extends KeyDeserializer {

        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
            if (StringUtils.isBlank(key)) {
                return null;
            }
            return JSONUtils.parseObject(key, UdfFunc.class);
        }
    }
}
