package com.dpline.console.service.impl;

import com.dpline.common.util.Result;
import com.dpline.console.service.GenericService;
import com.dpline.dao.entity.FlinkVersion;
import com.dpline.dao.entity.JobMetricAlertRule;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.JobMetricAlertRuleMapper;
import com.dpline.dao.rto.JobMetricAlertRto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetricAlertServiceImpl  extends GenericService<JobMetricAlertRule, Long> {

    public MetricAlertServiceImpl(@Autowired JobMetricAlertRuleMapper jobMetricAlertRuleMapper) {
        super(jobMetricAlertRuleMapper);
    }

    public JobMetricAlertRuleMapper getMapper(){
        return (JobMetricAlertRuleMapper) this.genericMapper;
    }


    public Result<Object> listAllAlertRules(JobMetricAlertRto jobMetricAlertRto) {
        Result<Object> result = new Result<>();
        Pagination<JobMetricAlertRule> jobMetricAlertRulePagination = Pagination.getInstanceFromRto(jobMetricAlertRto);
        this.executePagination(x -> this.getMapper().selectByJobId(x), jobMetricAlertRulePagination);
        return result.ok().setData(jobMetricAlertRulePagination);
    }
}
