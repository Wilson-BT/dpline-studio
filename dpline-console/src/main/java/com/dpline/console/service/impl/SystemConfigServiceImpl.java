package com.dpline.console.service.impl;

import cn.hutool.core.util.StrUtil;
import com.dpline.common.enums.Flag;
import com.dpline.common.enums.UserType;
import com.dpline.common.util.JSONUtils;
import com.dpline.common.util.Result;
import com.dpline.console.contants.SysConfigConstant;
import com.dpline.console.service.GenericService;
import com.dpline.console.util.Context;
import com.dpline.console.util.ContextUtils;
import com.dpline.dao.domain.DataSourceWriteControl;
import com.dpline.dao.domain.NoticeUser;
import com.dpline.dao.domain.OperMaintenance;
import com.dpline.dao.domain.WhitelistUser;
import com.dpline.dao.entity.SysConfig;
import com.dpline.dao.entity.User;
import com.dpline.dao.mapper.SystemConfigMapper;
import com.dpline.dao.rto.SysConfigRto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO 需要重构、重新设计这里的代码
@Service
public class SystemConfigServiceImpl extends GenericService<SysConfig,Long> {

    @Autowired
    UsersServiceImpl usersService;

    public SystemConfigServiceImpl(@Autowired SystemConfigMapper systemConfigMapper) {
        super(systemConfigMapper);
    }

    public Result<Object> getSystemConfig() {
        Result<Object> result = new Result<>();
        SysConfigRto sysSettingRto = new SysConfigRto();

        //作业运维
        OperMaintenance operMaintenance = Optional.ofNullable(getValByKey(SysConfigConstant.OPERATION_MAINTENANCE_KEY, OperMaintenance.class)).orElse(new OperMaintenance());
        sysSettingRto.setOperMaintenance(operMaintenance);

        //白名单
        List<WhitelistUser> valListByKey = getValListByKey(SysConfigConstant.WHITE_LIST_KEY, WhitelistUser.class);
        sysSettingRto.setWhitelists(valListByKey);

        //普通通知用户
        List<NoticeUser> commonNoticeUsers = getValListByKey(SysConfigConstant.COMMON_NOTICE_USERS, NoticeUser.class);
        sysSettingRto.setCommonNoticeUsers(commonNoticeUsers);

        //告警通知用户
        List<NoticeUser> alarmNoticeUsers = getValListByKey(SysConfigConstant.ALARM_NOTICE_USERS, NoticeUser.class);
        sysSettingRto.setAlarmNoticeUsers(alarmNoticeUsers);

        //数据库写入控制
        List<DataSourceWriteControl> dataSourceWriteContrls = getValListByKey(SysConfigConstant.DATASOURCE_WRITE_CONTRLS, DataSourceWriteControl.class);
        sysSettingRto.setDataSourceWriteControl(dataSourceWriteContrls);

        //集群资源校验开关
        Boolean resourceValidate = getValByKey(SysConfigConstant.RESOURCE_VALIDATE, Boolean.class);
        sysSettingRto.setResourceValidate(resourceValidate);

        //告警全局开关
        Boolean alertSwitch = getValByKey(SysConfigConstant.ALERT_GLOBAL_SWITCH, Boolean.class);
        sysSettingRto.setAlertGlobalSwitch(alertSwitch);

        //告警全局开关
//        Boolean needTwoApprove = getValByKey(SysConfigConstant.NEED_TWO_APPROVE, Boolean.class);
//        sysSettingRto.setNeedTwoApprove(needTwoApprove);
        result.ok();
        result.setData(sysSettingRto);
        return result;
    }


    /**
     * 系统运维保存
     * @param sysSettingRto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SysConfigRto saveSysConfig(SysConfigRto sysSettingRto){
        //作业运维保存
        this.upsertByKey(SysConfigConstant.OPERATION_MAINTENANCE_KEY, sysSettingRto.getOperMaintenance());

        //白名单保存
        this.upsertByKey(SysConfigConstant.WHITE_LIST_KEY, sysSettingRto.getWhitelists());

        //常规通知用户
        this.upsertByKey(SysConfigConstant.COMMON_NOTICE_USERS, sysSettingRto.getCommonNoticeUsers());

        //告警通知用户
        this.upsertByKey(SysConfigConstant.ALARM_NOTICE_USERS, sysSettingRto.getAlarmNoticeUsers());

        //数据库写入控制
        this.upsertByKey(SysConfigConstant.DATASOURCE_WRITE_CONTRLS, sysSettingRto.getDataSourceWriteControl());

        //集群资源校验开关
        this.upsertByKey(SysConfigConstant.RESOURCE_VALIDATE, sysSettingRto.getResourceValidate());

        //告警全局开关
        this.upsertByKey(SysConfigConstant.ALERT_GLOBAL_SWITCH, sysSettingRto.getAlertGlobalSwitch());

        //是否需要二级审批
//        this.upsertByKey(SysConfigConstant.NEED_TWO_APPROVE, sysSettingRto.getNeedTwoApprove());

        return sysSettingRto;
    }

    /**
     * 获取某个val的具体数组
     * @param key
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> getValListByKey(String key, Class<T> clazz){
        SysConfig sysConfig = queryByKey(key);
        if (Objects.nonNull(sysConfig) && StrUtil.isNotBlank(sysConfig.getConfigValue())) {
            String configValue = sysConfig.getConfigValue();
            return JSONUtils.parseArray(configValue, clazz);
        }else {
            return new ArrayList<T>();
        }
    }


    private SysConfig queryByKey(String configKey){
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(configKey);
        sysConfig.setEnabledFlag(Flag.YES.getCode());
        List<SysConfig> sysConfigs = selectAll(sysConfig);
        return  CollectionUtils.isEmpty(sysConfigs) ? null : sysConfigs.get(0);
    }
    /**
     * 获取某个val的具体对象
     * @param key
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public  <T> T getValByKey(String key,Class<T> clazz)  {
        SysConfig sysConfig = queryByKey(key);
        if (Objects.nonNull(sysConfig) && StrUtil.isNotBlank(sysConfig.getConfigValue())) {
            String configValue = sysConfig.getConfigValue();
            return JSONUtils.parseObject(configValue, clazz);
        }else {
            return null;
        }
    }

    /**
     * 插入或者更新
     * @param key
     * @param val
     */
    private void upsertByKey(String key,Object val) {
        SysConfig sysConfig = queryByKey(key);
        if (Objects.nonNull(sysConfig)) {
            sysConfig.setConfigValue(JSONUtils.toJsonString(val));
            updateSelective(sysConfig);
        }else {
            sysConfig = new SysConfig();
            sysConfig.setConfigKey(key);
            sysConfig.setEnabledFlag(Flag.YES.getCode());
            sysConfig.setConfigValue(JSONUtils.toJsonString(val));
            insertSelective(sysConfig);
        }
    }

    private SystemConfigMapper getMapper() {
        return (SystemConfigMapper) this.genericMapper;
    }

    public Result<Object> isWhiteList() {
        Result<Object> data = new Result<>();
        Context context = ContextUtils.get();
        User user = context.getUser();
        if(UserType.ADMIN_USER.getCode() == context.getUser().getIsAdmin()){
            return data.setData(true);
        }
        List<WhitelistUser> whitelists = getValListByKey(SysConfigConstant.WHITE_LIST_KEY, WhitelistUser.class);
        List<String> whitelistAccounts = Optional.ofNullable(whitelists).orElse(new ArrayList<>()).stream().map(WhitelistUser::getUserCode).collect(Collectors.toList());
        if(whitelistAccounts.contains(user.getUserCode())){
            return data.setData(true);
        }
        return data.setData(false);
    }
}
