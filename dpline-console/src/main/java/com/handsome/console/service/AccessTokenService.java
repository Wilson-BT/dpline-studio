package com.handsome.console.service;


import com.handsome.dao.entity.User;
import com.handsome.common.util.Result;

import java.util.Map;

/**
 * access token service
 */
public interface AccessTokenService {

    /**
     * query access token list
     *
     * @param loginUser login user
     * @param searchVal search value
     * @param pageNo page number
     * @param pageSize page size
     * @return token list for page number and page size
     */
    Result queryAccessTokenList(User loginUser, String searchVal, Integer pageNo, Integer pageSize);

    /**
     * query access token for specified user
     *
     * @param loginUser login user
     * @param userId user id
     * @return token list for specified user
     */
    Map<String, Object> queryAccessTokenByUser(User loginUser, Integer userId);

    /**
     * create token
     *
     * @param userId token for user
     * @param expireTime token expire time
     * @param token token string (if it is absent, it will be automatically generated)
     * @return create result code
     */
    Map<String, Object> createToken(User loginUser, int userId, String expireTime, String token);


    /**
     * generate token
     *
     * @param userId token for user
     * @param expireTime token expire time
     * @return token string
     */
    Map<String, Object> generateToken(User loginUser, int userId, String expireTime);

    /**
     * delete access token
     *
     * @param loginUser login user
     * @param id token id
     * @return delete result code
     */
    Map<String, Object> delAccessTokenById(User loginUser, int id);

    /**
     * update token by id
     *
     * @param id token id
     * @param userId token for user
     * @param expireTime token expire time
     * @param token token string (if it is absent, it will be automatically generated)
     * @return updated access token entity
     */
    Map<String, Object> updateToken(User loginUser, int id, int userId, String expireTime, String token);
}
