package com.dpline.dao.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@TableName("dpline_cluster_user")
@EqualsAndHashCode(callSuper=true)
public class ClusterUser extends GenericModel<Long> implements Serializable {


    private Long clusterId;
    /**
     * 工号
     */
    private String userCode;

    /**
     * 名称
     */
    @TableField(exist = false)
    private String userName;
}
