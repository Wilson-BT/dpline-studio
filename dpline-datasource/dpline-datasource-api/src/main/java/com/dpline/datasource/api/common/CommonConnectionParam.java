package com.dpline.datasource.api.common;


import com.dpline.datasource.spi.ConnectionParam;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
@Data
public class CommonConnectionParam implements ConnectionParam {

    protected String userName;

    protected String password;

    protected String address;

    protected String database;

    protected String dataSourceUrl;

    protected String driverLocation;

    protected String driverClassName;

    protected String validationQuery;

    protected String other;

    private Map<String, String> props = new HashMap<>();


}
