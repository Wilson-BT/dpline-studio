package com.dpline.dao.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源文件
 */
@Data
public class SourceConfig {

    private List<MainResourceDepend> jarDependList = new ArrayList<>();

    @Data
    public static class MainResourceDepend {

        Long mainResourceId;

        String name;

        String jarFunctionType;

    }

}
