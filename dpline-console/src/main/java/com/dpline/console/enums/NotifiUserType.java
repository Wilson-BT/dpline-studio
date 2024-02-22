package com.dpline.console.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotifiUserType {
    COMMON_NOTICE_USERS("常规通知名单"),
    ALARM_NOTICE_USERS("告警通知名单");
    private String desc;
}
