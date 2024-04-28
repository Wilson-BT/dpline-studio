package com.dpline.dao.domain;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OperMaintenance {
    /**
     * 启动-恢复开关：0关 1开
     */
    @NotNull(message = "启动/恢复开关不能为空")
    private Integer start  = 1 ;
    /**
     * 停止-暂停开关：0关 1开
     */
    @NotNull(message = "停止/暂停开关不能为空")
    private Integer stop = 1;
}
