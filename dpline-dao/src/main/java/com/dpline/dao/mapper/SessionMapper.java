package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.Session;
import com.dpline.dao.generic.GenericMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * session mapper interface
 */
@DS("mysql")
public interface SessionMapper extends GenericMapper<Session,Long> {

    /**
     * query session list by userId
     * @param userId userId
     * @return session list
     */
    List<Session> queryByUserId(@Param("userId") Long userId);

    /**
     * query session by userId and Ip
     * @param userId userId
     * @param ip ip
     * @return session
     */
    Session queryByUserIdAndIp(@Param("userId") Long userId,@Param("ip") String ip);

}
