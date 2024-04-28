package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * session
 */
@TableName("dpline_session")
public class Session {

    /**
     * id
     */
    @TableId(value="id", type=IdType.INPUT)
    private String id;

    /**
     * user id
     */
    private Long userId;

    /**
     * last login time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date lastLoginTime;

    /**
     * user login ip
     */
    private String ip;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", userId=" + userId +
                ", ip='" + ip + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Session session = (Session) o;

        if (userId != session.userId) {
            return false;
        }
        if (!id.equals(session.id)) {
            return false;
        }
        if (!lastLoginTime.equals(session.lastLoginTime)) {
            return false;
        }
        return ip.equals(session.ip);
    }

    @Override
    public int hashCode() {
        long result = id.hashCode();
        result = 31 * result + userId;
        result = 31 * result + lastLoginTime.hashCode();
        result = 31 * result + ip.hashCode();
        return (int) result;
    }
}
