package com.dpline.console.controller;

import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.impl.AlertServiceImpl;
import com.dpline.dao.entity.AlertInstance;
import com.dpline.dao.rto.AlertInstanceRto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/alertManagement")
public class AlertController {

    @Autowired
    AlertServiceImpl alertServiceImpl;

    @PostMapping("/add")
    @AccessLogAnnotation
    public Result<Object> add(@RequestBody AlertInstance alertInstance){
        return alertServiceImpl.addAlertInstance(alertInstance);
    }

    @PostMapping("/delete")
    @AccessLogAnnotation
    public Result<Object> delete(@RequestParam("id") Long id){
        return alertServiceImpl.deleteAlertInstance(id);
    }

    @PostMapping("/update")
    @AccessLogAnnotation
    public Result<Object> update(@RequestBody AlertInstance alertInstance){
        return alertServiceImpl.updateAlertInstance(alertInstance);
    }

    @PostMapping("/list")
    @AccessLogAnnotation
    public Result<Object> list(@RequestBody AlertInstanceRto alertInstanceRto){
        return alertServiceImpl.list(alertInstanceRto);
    }

    @PostMapping("/search")
    @AccessLogAnnotation
    public Result<Object> search(@RequestBody AlertInstance alertInstance){
        return alertServiceImpl.query(alertInstance);
    }

    @PostMapping("/updateState")
    @AccessLogAnnotation
    public Result<Object> updateState(@RequestParam("id") Long id,
                                 @RequestParam("enabledFlag") int enabledFlag){
        return alertServiceImpl.updateState(id,enabledFlag);
    }

}
