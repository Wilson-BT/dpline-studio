

package com.handsome.dao;

import org.apache.commons.lang.StringUtils;
import com.handsome.common.enums.AlertStatus;
import com.handsome.dao.entity.*;
import com.handsome.dao.mapper.AlertGroupMapper;
import com.handsome.dao.mapper.AlertMapper;
import com.handsome.dao.mapper.AlertInstanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlertDao {

    @Autowired
    private AlertMapper alertMapper;

    @Autowired
    private AlertInstanceMapper alertPluginInstanceMapper;

    @Autowired
    private AlertGroupMapper alertGroupMapper;

    /**
     * update alert
     *
     * @param alertStatus alertStatus
     * @param log log
     * @param id id
     * @return update alert result
     */
    public int updateAlert(AlertStatus alertStatus, String log, int id) {
        Alert alert = alertMapper.selectById(id);
        alert.setAlertStatus(alertStatus);
        alert.setUpdateTime(new Date());
        alert.setLog(log);
        return alertMapper.updateById(alert);
    }

    /**
     * List alerts that are pending for execution
     */
    public List<Alert> listPendingAlerts() {
        return alertMapper.listAlertByStatus(AlertStatus.WAIT_EXECUTION);
    }

    /**
     * list all alert plugin instance by alert group id
     *
     * @param alertGroupId alert group id
     * @return AlertPluginInstance list
     */
    public List<AlertInstance> listInstanceByAlertGroupId(int alertGroupId) {
        String alertInstanceIdsParam = alertGroupMapper.queryAlertGroupInstanceIdsById(alertGroupId);
        if (StringUtils.isNotBlank(alertInstanceIdsParam)) {
            String[] idsArray = alertInstanceIdsParam.split(",");
            List<Integer> ids = Arrays.stream(idsArray)
                    .map(s -> Integer.parseInt(s.trim()))
                    .collect(Collectors.toList());
            return alertPluginInstanceMapper.queryByIds(ids);
        }
        return null;
    }
}
