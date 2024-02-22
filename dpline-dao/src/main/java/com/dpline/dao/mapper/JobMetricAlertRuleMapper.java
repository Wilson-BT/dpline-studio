package com.dpline.dao.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.JobMetricAlertRule;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;

import java.util.List;

@DS("mysql")
public interface JobMetricAlertRuleMapper extends GenericMapper<JobMetricAlertRule, Long> {


    List<JobMetricAlertRule> selectByJobId(Pagination<JobMetricAlertRule> jobMetricAlertRulePagination);
}
