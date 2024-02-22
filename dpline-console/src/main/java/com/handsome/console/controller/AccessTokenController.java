package com.handsome.console.controller;

import com.handsome.common.Constants;
import com.handsome.common.util.ParameterUtils;
import com.handsome.console.aspect.AccessLogAnnotation;
import com.handsome.dao.entity.User;
import com.handsome.console.exception.ApiException;
import com.handsome.console.service.AccessTokenService;
import com.handsome.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


import java.util.Map;

import static com.handsome.common.enums.Status.*;


/**
 * access token controller
 */
@RestController
@RequestMapping("/access-tokens")
public class AccessTokenController extends BaseController {

    @Autowired
    private AccessTokenService accessTokenService;

    /**
     * create token
     *
     * @param loginUser login user
     * @param userId token for user id
     * @param expireTime expire time for the token
     * @param token token string (if it is absent, it will be automatically generated)
     * @return create result state code
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(CREATE_ACCESS_TOKEN_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result createToken(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                              @RequestParam(value = "userId") int userId,
                              @RequestParam(value = "expireTime") String expireTime,
                              @RequestParam(value = "token", required = false) String token) {

        Map<String, Object> result = accessTokenService.createToken(loginUser, userId, expireTime, token);
        return returnDataList(result);
    }

    /**
     * generate token string
     *
     * @param loginUser login user
     * @param userId token for user
     * @param expireTime expire time
     * @return token string
     */
    @PostMapping(value = "/generate")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(GENERATE_TOKEN_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result generateToken(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                @RequestParam(value = "userId") int userId,
                                @RequestParam(value = "expireTime") String expireTime) {
        Map<String, Object> result = accessTokenService.generateToken(loginUser, userId, expireTime);
        return returnDataList(result);
    }

    /**
     * query access token list paging
     *
     * @param loginUser login user
     * @param pageNo page number
     * @param searchVal search value
     * @param pageSize page size
     * @return token list of page number and page size
     */
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_ACCESSTOKEN_LIST_PAGING_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryAccessTokenList(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                       @RequestParam("pageNo") Integer pageNo,
                                       @RequestParam(value = "searchVal", required = false) String searchVal,
                                       @RequestParam("pageSize") Integer pageSize) {

        Result result = checkPageParams(pageNo, pageSize);
        if (!result.checkResult()) {
            return result;
        }
        searchVal = ParameterUtils.handleEscapes(searchVal);
        result = accessTokenService.queryAccessTokenList(loginUser, searchVal, pageNo, pageSize);
        return result;
    }

    /**
     * query access token for specified user
     *
     * @param loginUser login user
     * @param userId user id
     * @return token list for specified user
     */
    @GetMapping(value = "/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_ACCESSTOKEN_BY_USER_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryAccessTokenByUser(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
            @PathVariable("userId") Integer userId) {
        Map<String, Object> result = this.accessTokenService.queryAccessTokenByUser(loginUser, userId);
        return this.returnDataList(result);
    }

    /**
     * delete access token by id
     *
     * @param loginUser login user
     * @param id token id
     * @return delete result code
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(DELETE_ACCESS_TOKEN_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result delAccessTokenById(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                     @PathVariable(value = "id") int id) {
        Map<String, Object> result = accessTokenService.delAccessTokenById(loginUser, id);
        return returnDataList(result);
    }


    /**
     * update token
     *
     * @param loginUser login user
     * @param id token id
     * @param userId token for user
     * @param expireTime token expire time
     * @param token token string (if it is absent, it will be automatically generated)
     * @return updated access token entity
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(UPDATE_ACCESS_TOKEN_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result updateToken(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                              @PathVariable(value = "id") int id,
                              @RequestParam(value = "userId") int userId,
                              @RequestParam(value = "expireTime") String expireTime,
                              @RequestParam(value = "token", required = false) String token) {
        Map<String, Object> result = accessTokenService.updateToken(loginUser, id, userId, expireTime, token);
        return returnDataList(result);
    }

}
