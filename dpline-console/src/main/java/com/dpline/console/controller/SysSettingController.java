package com.dpline.console.controller;

import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.impl.SystemConfigServiceImpl;
import com.dpline.dao.rto.SysConfigRto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <pre>
 * 表现层控制类
 * </pre>
 */
@Api(tags = "系统配置")
@RestController
@RequestMapping(value = "/system")
@Validated
public class SysSettingController {

    @Autowired
    private SystemConfigServiceImpl systemSettingSerivice;


    @ApiOperation(value="系统运维查询")
    @RequestMapping(value="/getSystemSetting",method= RequestMethod.GET)
    @AccessLogAnnotation
    public Result getSysSetting(){
        return systemSettingSerivice.getSystemConfig();
    }



    @ApiOperation(value="系统运维保存")
    @RequestMapping(value="/saveSystemSetting",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result saveSysSetting(@RequestBody @Valid SysConfigRto sysConfigRto) {
        Result data = new Result<>();
        data.ok();
        data.setData(systemSettingSerivice.saveSysConfig(sysConfigRto));
        return  data;
    }

    @ApiOperation(value="当前用户是否是白名单")
    @RequestMapping(value="/isWhiteList",method= RequestMethod.GET)
    @AccessLogAnnotation
    public Result<Object> isWhiteList() {
        return systemSettingSerivice.isWhiteList().ok();
    }

}