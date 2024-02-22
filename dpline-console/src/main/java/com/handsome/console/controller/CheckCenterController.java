package com.handsome.console.controller;

import com.handsome.common.util.Result;
import com.handsome.console.websocket.WebSocketEndpoint;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/checkcenter")
public class CheckCenterController extends BaseController {

    //推送数据接口
    @ResponseBody
    @RequestMapping("/socket/push/{sid}")
    public Result<Object> pushToWeb(@PathVariable String sid, String message) {
        WebSocketEndpoint.sendMessage(sid,message);
        return Result.success(sid);
    }
}
