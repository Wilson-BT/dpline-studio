package com.dpline.operator.watcher;


import com.dpline.operator.k8s.K8sClusterManager;

public abstract class K8sStatusRemoteTrack implements StatusTrack {

    K8sClusterManager k8sClusterManager;
}
