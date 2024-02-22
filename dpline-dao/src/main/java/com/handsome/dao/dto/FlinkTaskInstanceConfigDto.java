package com.handsome.dao.dto;

import com.handsome.common.enums.*;
import com.handsome.common.options.CheckpointOptions;
import com.handsome.common.options.ResourceOptions;
import com.handsome.common.options.RestartOptions;
import com.handsome.common.util.JSONUtils;
import com.handsome.dao.entity.FlinkRunTaskInstance;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class FlinkTaskInstanceConfigDto {

    @NotNull
    private long id;


    @NotNull
    private String taskInstanceName;

    /**
     * run mode
     */
    @NotNull
    private RunModeType runMode;

    /**
     * if open chain
     */

    private Flag openChain;

    /**
     * memory、cpu、slots、parallelism and so on
     * json
     */
    @Valid
    private ResourceOptions resourceOptions;


    private String description;

    /**
     * resolve order
     * default: parent first
     */

    private ResolveOrderType resolveOrder;

    /**
     * restart num
     * default:3 times
     */
    private int restartNum;

    /**
     * checkpoint json options
     */
    private CheckpointOptions checkpointOptions;

    /**
     * k8s namespace id
     */
    private long k8sNamespaceId;

    /**
     * k8s cluster id
     * only used for k8s session mode
     */
    private long K8sSessionClusterId;

    /**
     * k8s pod port use nodeport or clusterId
     * default: nodeport
     * recommend: clusterIp
     */
    private ExposedType exposedType;

    /**
     * warning type
     */
    private AlertType alertType;
    /**
     * warning group
     */
    private int alertInstanceId;

    /**
     * restart options
     */
    private RestartOptions restartOptions;

    public FlinkRunTaskInstance updateFlinkTaskInstance(FlinkRunTaskInstance flinkRunTaskInstance){
        flinkRunTaskInstance.setId(this.id);
        flinkRunTaskInstance.setFlinkTaskInstanceName(this.taskInstanceName);
        flinkRunTaskInstance.setDescription(this.description);
        flinkRunTaskInstance.setRunMode(this.runMode);
        flinkRunTaskInstance.setOpenChain(this.openChain);
        flinkRunTaskInstance.setResourceOptions(JSONUtils.toJsonString(this.resourceOptions));
        flinkRunTaskInstance.setResolveOrder(this.resolveOrder);
        flinkRunTaskInstance.setRestartNum(this.restartNum);
        flinkRunTaskInstance.setAlertType(this.alertType);
        flinkRunTaskInstance.setAlertInstanceId(this.alertInstanceId);
        flinkRunTaskInstance.setCheckpointOptions(JSONUtils.toJsonString(this.checkpointOptions));
        flinkRunTaskInstance.setExposedType(this.exposedType);
        flinkRunTaskInstance.setRestartOptions(JSONUtils.toJsonString(this.restartOptions));
        flinkRunTaskInstance.setK8sNamespaceId(this.k8sNamespaceId);
        flinkRunTaskInstance.setK8sSessionClusterId(this.K8sSessionClusterId);
        flinkRunTaskInstance.setUpdateTime(new Date());
        return flinkRunTaskInstance;
    }

}
