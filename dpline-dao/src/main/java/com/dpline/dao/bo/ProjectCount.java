package com.dpline.dao.bo;

import com.dpline.dao.dto.DplineProjectDto;
import com.dpline.dao.generic.Pagination;
import lombok.Data;

@Data
public class ProjectCount extends Pagination<DplineProjectDto> {

    private static final long serialVersionUID = 1L;
    /**
     * 项目总数
     */
    private Long projectTotal;
    /**
     * job总数
     */
    private Long jobTotal;
    /**
     * 用户总数
     */
    private Long userTotal;
}
