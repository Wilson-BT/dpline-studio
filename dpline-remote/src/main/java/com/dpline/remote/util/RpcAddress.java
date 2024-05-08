package com.dpline.remote.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RpcAddress {

    private String ip;

    private int port;

}
