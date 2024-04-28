package com.dpline.dao.rto;

import com.dpline.dao.entity.JobMetricAlertRule;
import lombok.Data;

@Data
public class JobMetricAlertRto extends GenericRto<JobMetricAlertRule>{

    private Long jobId;

}
