package com.dpline.dao.rto;

import com.dpline.dao.entity.DplineJobOperateLog;
import lombok.Data;


@Data
public class DplineJobOperateLogRto extends GenericRto<DplineJobOperateLog>{

    private Long jobId;

}
