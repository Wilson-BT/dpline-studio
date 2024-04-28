package com.dpline.dao.domain;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 常规通知人员
 */
@Data
public class NoticeUser {
    @NotNull(message = "userName不能为空")
    private String userName;

    @NotNull(message = "userCode不能为空")
    private String userCode;

}
