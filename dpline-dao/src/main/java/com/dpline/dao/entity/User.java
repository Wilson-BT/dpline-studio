package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * user
 */
@Data
@TableName("dpline_user")
public class User extends GenericModel<Long> implements Serializable {

    /**
     * user name
     */
    private String userName;

    /**
     * user code
     */
    private String userCode;

    /**
     * user password
     */
    private String password;

    /**
     * mail
     */
    private String email;

    /**
     * phone
     */
    private String phone;

    /**
     * user type
     */
    private int isAdmin;


    /**
     * last visit or update project
     */
    private Long projectId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (id != user.id) {
            return false;
        }
        return userName.equals(user.userName);

    }

    @Override
    public int hashCode() {
        long result = this.id;
        result = 31 * result + userName.hashCode();
        return (int)result;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", userName='" + userName + '\'' +
            ", userPassword='" + password + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", isAdmin=" + isAdmin +
            ", enabledFlag=" + enabledFlag +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            '}';
    }
}
