//
//
//package com.handsome.console.service.impl;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.handsome.common.util.PluginParamsTransfer;
//import org.apache.commons.collections.CollectionUtils;
//import com.handsome.common.enums.Status;
//import com.handsome.console.service.AlertPluginInstanceService;
//import com.handsome.console.util.PageInfo;
//import com.handsome.common.util.Result;
//import com.handsome.dao.dto.AlertPluginInstanceDto;
//import com.handsome.common.Constants;
//import com.handsome.common.util.JSONUtils;
//import com.handsome.dao.entity.AlertPluginInstance;
//import com.handsome.dao.entity.PluginDefine;
//import com.handsome.dao.entity.User;
//import com.handsome.dao.mapper.AlertGroupMapper;
//import com.handsome.dao.mapper.AlertPluginInstanceMapper;
//import com.handsome.dao.mapper.PluginDefineMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * alert plugin instance service impl
// */
//@Service
//@Lazy
//public class AlertPluginInstanceServiceImpl extends BaseServiceImpl implements AlertPluginInstanceService {
//
//    @Autowired
//    private AlertPluginInstanceMapper alertPluginInstanceMapper;
//
//    @Autowired
//    private PluginDefineMapper pluginDefineMapper;
//
//    @Autowired
//    private AlertGroupMapper alertGroupMapper;
//
//    /**
//     * creat alert plugin instance
//     *
//     * @param loginUser login user
//     * @param pluginDefineId plugin define id
//     * @param instanceName instance name
//     * @param pluginInstanceParams plugin instance params
//     */
//    @Override
//    public Map<String, Object> create(User loginUser, int pluginDefineId, String instanceName, String pluginInstanceParams) {
//        AlertPluginInstance alertPluginInstance = new AlertPluginInstance();
//        String paramsMapJson = parsePluginParamsMap(pluginInstanceParams);
//        alertPluginInstance.setPluginInstanceParams(paramsMapJson);
//        alertPluginInstance.setInstanceName(instanceName);
//        alertPluginInstance.setPluginDefineId(pluginDefineId);
//
//        Map<String, Object> result = new HashMap<>();
//
//        if (alertPluginInstanceMapper.existInstanceName(alertPluginInstance.getInstanceName()) == Boolean.TRUE) {
//            putMsg(result, Status.PLUGIN_INSTANCE_ALREADY_EXIT);
//            return result;
//        }
//
//        int i = alertPluginInstanceMapper.insert(alertPluginInstance);
//
//        if (i > 0) {
//            putMsg(result, Status.SUCCESS);
//            return result;
//        }
//        putMsg(result, Status.SAVE_ERROR);
//        return result;
//    }
//
//    /**
//     * update alert plugin instance
//     *
//     * @param loginUser login user
//     * @param pluginInstanceId plugin instance id
//     * @param instanceName instance name
//     * @param pluginInstanceParams plugin instance params
//     */
//    @Override
//    public Map<String, Object> update(User loginUser, int pluginInstanceId, String instanceName, String pluginInstanceParams) {
//
////        String paramsMapJson = parsePluginParamsMap(pluginInstanceParams);
////        AlertPluginInstance alertPluginInstance = new AlertPluginInstance(pluginInstanceId, paramsMapJson, instanceName, new Date());
//
//        Map<String, Object> result = new HashMap<>();
////        int i = alertPluginInstanceMapper.updateById(alertPluginInstance);
//
////        if (i > 0) {
////            putMsg(result, Status.SUCCESS);
////            return result;
////        }
//        putMsg(result, Status.SAVE_ERROR);
//        return result;
//    }
//
//    /**
//     * delete alert plugin instance
//     *
//     * @param loginUser login user
//     * @param id id
//     * @return result
//     */
//    @Override
//    public Map<String, Object> delete(User loginUser, int id) {
//        Map<String, Object> result = new HashMap<>();
//        //check if there is an associated alert group
//        boolean hasAssociatedAlertGroup = checkHasAssociatedAlertGroup(String.valueOf(id));
//        if (hasAssociatedAlertGroup) {
//            putMsg(result, Status.DELETE_ALERT_PLUGIN_INSTANCE_ERROR_HAS_ALERT_GROUP_ASSOCIATED);
//            return result;
//        }
//
//        int i = alertPluginInstanceMapper.deleteById(id);
//        if (i > 0) {
//            putMsg(result, Status.SUCCESS);
//        }
//
//        return result;
//    }
//
//    /**
//     * get alert plugin instance
//     *
//     * @param loginUser login user
//     * @param id get id
//     * @return alert plugin
//     */
//    @Override
//    public Map<String, Object> get(User loginUser, int id) {
//        Map<String, Object> result = new HashMap<>();
//        AlertPluginInstance alertPluginInstance = alertPluginInstanceMapper.selectById(id);
//
//        if (null != alertPluginInstance) {
//            putMsg(result, Status.SUCCESS);
//            result.put(Constants.DATA_LIST, alertPluginInstance);
//        }
//
//        return result;
//    }
//
//    @Override
//    public Map<String, Object> queryAll() {
//        Map<String, Object> result = new HashMap<>();
//        List<AlertPluginInstance> alertPluginInstances = alertPluginInstanceMapper.queryAllAlertPluginInstanceList();
//        List<AlertPluginInstanceDto> AlertPluginInstanceDtoS = buildPluginInstanceVOList(alertPluginInstances);
//        if (null != alertPluginInstances) {
//            putMsg(result, Status.SUCCESS);
//            result.put(Constants.DATA_LIST, AlertPluginInstanceDtoS);
//        }
//        return result;
//    }
//
//    @Override
//    public boolean checkExistPluginInstanceName(String pluginInstanceName) {
//        return alertPluginInstanceMapper.existInstanceName(pluginInstanceName) == Boolean.TRUE;
//    }
//
//    @Override
//    public Result listPaging(User loginUser, String searchVal, int pageNo, int pageSize) {
//
//        Result result = new Result();
//        if (!isAdmin(loginUser)) {
//            putMsg(result,Status.USER_NO_OPERATION_PERM);
//            return result;
//        }
//
//        Page<AlertPluginInstance> page = new Page<>(pageNo, pageSize);
//        IPage<AlertPluginInstance> alertPluginInstanceIPage = alertPluginInstanceMapper.queryByInstanceNamePage(page, searchVal);
//
//        PageInfo<AlertPluginInstanceDto> pageInfo = new PageInfo<>(pageNo, pageSize);
//        pageInfo.setTotal((int) alertPluginInstanceIPage.getTotal());
//        pageInfo.setTotalList(buildPluginInstanceVOList(alertPluginInstanceIPage.getRecords()));
//        result.setData(pageInfo);
//        putMsg(result, Status.SUCCESS);
//        return result;
//    }
//
//    private List<AlertPluginInstanceDto> buildPluginInstanceVOList(List<AlertPluginInstance> alertPluginInstances) {
//        if (CollectionUtils.isEmpty(alertPluginInstances)) {
//            return null;
//        }
//        List<PluginDefine> pluginDefineList = pluginDefineMapper.queryAllPluginDefineList();
//        if (CollectionUtils.isEmpty(pluginDefineList)) {
//            return null;
//        }
//        Map<Integer, PluginDefine> pluginDefineMap = pluginDefineList.stream().collect(Collectors.toMap(PluginDefine::getId, Function.identity()));
//        List<AlertPluginInstanceDto> AlertPluginInstanceDtoS = new ArrayList<>();
//        alertPluginInstances.forEach(alertPluginInstance -> {
//            AlertPluginInstanceDto AlertPluginInstanceDto = new AlertPluginInstanceDto();
//
//            AlertPluginInstanceDto.setCreateTime(alertPluginInstance.getCreateTime());
//            AlertPluginInstanceDto.setUpdateTime(alertPluginInstance.getUpdateTime());
//            AlertPluginInstanceDto.setPluginDefineId(alertPluginInstance.getPluginDefineId());
//            AlertPluginInstanceDto.setInstanceName(alertPluginInstance.getInstanceName());
//            AlertPluginInstanceDto.setId(alertPluginInstance.getId());
//            PluginDefine pluginDefine = pluginDefineMap.get(alertPluginInstance.getPluginDefineId());
//            //FIXME When the user removes the plug-in, this will happen. At this time, maybe we should add a new field to indicate that the plug-in has expired?
//            if (null == pluginDefine) {
//                return;
//            }
//            AlertPluginInstanceDto.setAlertPluginName(pluginDefine.getPluginName());
//            //todo List pages do not recommend returning this parameter
//            String pluginParamsMapString = alertPluginInstance.getPluginInstanceParams();
//            String uiPluginParams = parseToPluginUiParams(pluginParamsMapString, pluginDefine.getPluginParams());
//            AlertPluginInstanceDto.setPluginInstanceParams(uiPluginParams);
//            AlertPluginInstanceDtoS.add(AlertPluginInstanceDto);
//        });
//        return AlertPluginInstanceDtoS;
//
//    }
//
//    /**
//     * Get the parameters actually needed by the plugin
//     *
//     * @param pluginParams Complete parameters(include ui)
//     * @return k, v(json string)
//     */
//    private String parsePluginParamsMap(String pluginParams) {
//        Map<String, String> paramsMap = PluginParamsTransfer.getPluginParamsMap(pluginParams);
//        return JSONUtils.toJsonString(paramsMap);
//    }
//
//    /**
//     * parse To Plugin Ui Params
//     *
//     * @param pluginParamsMapString k-v data
//     * @param pluginUiParams Complete parameters(include ui)
//     * @return Complete parameters list(include ui)
//     */
//    private String parseToPluginUiParams(String pluginParamsMapString, String pluginUiParams) {
//        List<Map<String, Object>> pluginParamsList = PluginParamsTransfer.generatePluginParams(pluginParamsMapString, pluginUiParams);
//        return JSONUtils.toJsonString(pluginParamsList);
//    }
//
//    private boolean checkHasAssociatedAlertGroup(String id) {
//        List<String> idsList = alertGroupMapper.queryInstanceIdsList();
//        if (CollectionUtils.isEmpty(idsList)) {
//            return false;
//        }
//        Optional<String> first = idsList.stream().filter(k -> null != k && Arrays.asList(k.split(",")).contains(id)).findFirst();
//        return first.isPresent();
//    }
//
//}
