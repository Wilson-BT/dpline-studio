package com.dpline.console.controller;


import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.impl.MetricAlertServiceImpl;
import com.dpline.dao.rto.JobMetricAlertRto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metric/alert")
public class MetricAlertController {

    @Autowired
    MetricAlertServiceImpl metricAlertServiceImpl;

    @PostMapping("/listAlertRule")
    @AccessLogAnnotation
    public Result<Object> listAlertRule(@RequestBody JobMetricAlertRto jobMetricAlertRto){
        return metricAlertServiceImpl.listAllAlertRules(jobMetricAlertRto);
    }

}
