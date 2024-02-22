package com.handsome.console.service;

import com.handsome.common.util.Result;
import com.handsome.dao.entity.User;

public interface FlinkSessionService {

    Result<Object> createFlinkSessionDefinition(User loginUser, String flinkSessionName, long k8sNamespaceId, int taskmanagerNum,int taskmanagerCpuNum, int taskmanagerMemSize,int taskmanagerSlotNum, int jobmanagerProcessSize, String kubernetesClusterId);

    Result<Object> updateFlinkSessionDefinition(User loginUser, int id,
                                                String flinkSessionName,
                                                long k8sNamespaceId,
                                                int taskmanagerNum,
                                                int taskmanagerCpuNum,
                                                int taskmanagerMemSize,
                                                int taskmanagerSlotNum,
                                                int jobmanagerProcessSize,
                                                String kubernetesClusterId);

    Result<Object> startFlinkSession(int id);

    Result<Object> deleteFlinkSession(int id);

    Result<Object> stopFlinkSession(int id);
}
