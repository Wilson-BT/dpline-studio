package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.SysConfig;
import com.dpline.dao.generic.GenericMapper;

@DS("mysql")
public interface SystemConfigMapper extends GenericMapper<SysConfig,Long> {
}
