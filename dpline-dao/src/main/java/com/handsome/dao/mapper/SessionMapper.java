package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.Session;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * session mapper interface
 */
public interface SessionMapper extends BaseMapper<Session> {

    /**
     * query session list by userId
     * @param userId userId
     * @return session list
     */
    List<Session> queryByUserId(@Param("userId") int userId);

    /**
     * query session by userId and Ip
     * @param userId userId
     * @param ip ip
     * @return session
     */
    Session queryByUserIdAndIp(@Param("userId") int userId,@Param("ip") String ip);

}
