package com.dpline.k8s.operator.watcher;


import com.dpline.k8s.operator.k8s.K8sClusterManager;

public abstract class K8sStatusRemoteTrack implements StatusTrack {

    K8sClusterManager k8sClusterManager;
}
