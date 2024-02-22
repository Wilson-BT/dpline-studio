package com.dpline.operator.k8s;

import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.ArrayList;

public class K8sClientLoopList {

    private Integer capacity;

    private static Integer number = 0;


    private ArrayList<KubernetesClient> arrayList = new ArrayList<>();

    public K8sClientLoopList(Integer capacity) {
        this.capacity = capacity;
    }


    public KubernetesClient poll() {
        return arrayList.get(numberIncrAndGet());
    }

    public boolean add(KubernetesClient kubernetesClient) {
        return arrayList.add(kubernetesClient);
    }
    /**
     * 如果 数据是自增，那就获取一下
     *
     * @return
     */
    public synchronized int numberIncrAndGet() {
        int i = number ++;
        if (i >= capacity) {
            number = 0;
            i = number;
        }
        return i;
    }

}
