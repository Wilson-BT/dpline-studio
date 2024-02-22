package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.handsome.common.enums.UserType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * user
 */
@Data
@TableName("dpline_user")
public class User implements Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

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
     * user state
     */
    private int state;

    /**
     * create time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * update time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;



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
        int result = id;
        result = 31 * result + userName.hashCode();
        return result;
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
            ", state=" + state +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            '}';
    }
}
