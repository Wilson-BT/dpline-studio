package com.dpline.dao.domain;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class WhitelistUser {
    @NotNull(message = "userName不能为空")
    private String userName;

    @NotNull(message = "userCode不能为空")
    private String userCode;
}
