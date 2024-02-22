package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.FlinkVersion;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;

import java.util.List;

@DS("mysql")
public interface FlinkVersionMapper extends GenericMapper<FlinkVersion, Long> {

    List<FlinkVersion> list(Pagination<FlinkVersion> pagination);

    List<FlinkVersion> queryFlinkVersion();

    List<FlinkVersion> selectByMotorType(String motorType);

}
