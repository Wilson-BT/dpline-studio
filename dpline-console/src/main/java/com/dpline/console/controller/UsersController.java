package com.dpline.console.controller;

import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.exception.ApiException;
import com.dpline.console.service.impl.UsersServiceImpl;
import com.dpline.console.util.ContextUtils;
import com.dpline.dao.entity.User;
import com.dpline.dao.rto.DplineUserRto;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.dpline.common.enums.Status.*;


@RestController
@RequestMapping("/users")
public class UsersController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UsersServiceImpl usersService;


    /**
     * user list no paging
     *
     * @return user list
     */
    @PostMapping(value = "/list")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(USER_LIST_ERROR)
    @AccessLogAnnotation
    public Result listUser() {
        Map<String, Object> result = usersService.queryUserList(ContextUtils.get().getUser());
        return returnDataList(result);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping(value = "/getUserInfo")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(GET_USER_INFO_ERROR)
    @AccessLogAnnotation
    public Result getUserInfo() {
        Map<String, Object> result = usersService.getUserInfo(ContextUtils.get().getUser());
        return returnDataList(result);
    }


    @ApiOperation(value = "用户管理页面-用户列表")
    @RequestMapping(value ="/getUserList",method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result getUserList(@RequestBody DplineUserRto dplineUserRto) {
        return usersService.getUserDetail(dplineUserRto);
    }


    @ApiOperation(value = "用户管理-添加")
    @RequestMapping(value ="/addUser",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result addUser(@RequestBody User user) {
        Result data = new Result<>();
        data.setData(this.usersService.insertUser(user));
        data.ok();
        return data;
    }

    @ApiOperation(value = "用户管理-修改")
    @RequestMapping(value ="/updateUser",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> updateUser(@RequestBody User user) {
        return this.usersService.updateUser(user);
    }



    @ApiOperation(value = "用户管理-删除用户")
    @RequestMapping(value ="/deleteUser",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> deleteUser(@RequestBody User user) {
        return this.usersService.deleteUser(user);
    }


    @ApiOperation(value = "用户管理-根据姓名或者工号查询用户")
    @RequestMapping(value ="/searchUser",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> searchUser(@RequestParam("nameOrCode") String nameOrCode) {
        return this.usersService.searchUserByNameOrCode(nameOrCode);
    }


}
