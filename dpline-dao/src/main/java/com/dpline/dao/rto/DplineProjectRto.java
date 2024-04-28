package com.dpline.dao.rto;

import com.dpline.dao.entity.OrderByClause;
import com.dpline.dao.entity.Project;
import com.dpline.dao.entity.ProjectUser;
import com.dpline.dao.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class DplineProjectRto extends Project {

    private Long projectId;

    private Integer page;

    private Integer pageSize;
    /**
     * 排序对象
     */
    private List<OrderByClause> orderByClauses;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 出参项目负责人
     */
    private List<ProjectUser> projectLeader;

    /**
     * 出参项目使用者和管理者
     */
    private List<ProjectUser> projectUsers;
}
