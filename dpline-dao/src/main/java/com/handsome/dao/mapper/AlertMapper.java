

package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.common.enums.AlertStatus;
import com.handsome.dao.entity.Alert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * alert mapper interface
 */
public interface AlertMapper extends BaseMapper<Alert> {

    /**
     * list alert by status
     * @param alertStatus alertStatus
     * @return alert list
     */
    List<Alert> listAlertByStatus(@Param("alertStatus") AlertStatus alertStatus);

    /**
     * Insert server crash alert
     * <p>This method will ensure that there is at most one unsent alert which has the same content in the database.
     */
    void insertAlertWhenServerCrash(@Param("alert") Alert alert);

}
