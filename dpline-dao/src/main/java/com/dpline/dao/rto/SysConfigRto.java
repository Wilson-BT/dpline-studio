package com.dpline.dao.rto;

import com.dpline.dao.domain.DataSourceWriteControl;
import com.dpline.dao.domain.NoticeUser;
import com.dpline.dao.domain.OperMaintenance;
import com.dpline.dao.domain.WhitelistUser;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SysConfigRto {

    @NotNull(message = "作业运维信息不能为空")
    OperMaintenance operMaintenance;

    List<WhitelistUser> whitelists;

    List<NoticeUser> commonNoticeUsers;

    List<NoticeUser> alarmNoticeUsers;

    //资源校验开关
    Boolean resourceValidate;

    /**
     * 前端根据datasourceType来做下来列表，选中
     */
    List<DataSourceWriteControl> dataSourceWriteControl;

    /**
     * 规则告警定时任务增加开关
     */
    Boolean alertGlobalSwitch;

//    Boolean needTwoApprove;
}
