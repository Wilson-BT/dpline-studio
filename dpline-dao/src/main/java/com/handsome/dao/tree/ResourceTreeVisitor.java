package com.handsome.dao.tree;

import com.handsome.dao.entity.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * resource 树的组织类
 */
public class ResourceTreeVisitor implements TreeVisitor {

    private List<Resource> resourceList;

    public ResourceTreeVisitor(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    /**
     * 将所有资源形成树状结构
     *
     * @return
     */
    @Override
    public ResourceComponent visit() {
        //创建一个家目录
        ResourceComponent rootDirectory = new Directory();
        for (Resource resource : resourceList) {
            // 是否是家目录，如果是家目录的话
            if (isRootNode(resource)) {
                ResourceComponent tempResourceComponent = getResourceComponent(resource);
                rootDirectory.add(tempResourceComponent);
                tempResourceComponent.setChildren(setChildren(tempResourceComponent.getId(), resourceList));
            }
        }
        return rootDirectory;
    }

    /**
     * 每个资源下面的所有资源列表
     *
     * @param id           每个父节点（有可能是叶子结点）
     * @param resourceList 剩下的资源
     * @return
     */
    private static List<ResourceComponent> setChildren(int id, List<Resource> resourceList) {
        List<ResourceComponent> childList = new ArrayList<>();
        for (Resource resource : resourceList) {
            // 如果资源列表中有相应资源的 pid 为目前的 资源id，name就将这个资源id挂到当前的资源childList上
            if (id == resource.getPid()) {
                ResourceComponent tmpResourceComponent = getResourceComponent(resource);
                childList.add(tmpResourceComponent);
                // 列表中移除这个 资源
                resourceList.remove(resource);
            }
        }
        // 对childList中的每一个资源进行遍历，分别进行递归，知道childList为空，这个资源就是个叶子节点了，此时就可以返回一个空的arrayList
        for (ResourceComponent resourceComponent : childList) {
            resourceComponent.setChildren(setChildren(resourceComponent.getId(), resourceList));
        }
        if (childList.size() == 0) {
            return new ArrayList<>();
        }
        return childList;
    }

    /**
     * 获取下面的所有资源
     *
     * @param resource
     * @return
     */
    private static ResourceComponent getResourceComponent(Resource resource) {
        ResourceComponent tempResourceComponent;
        if (resource.isDirectory()) {
            tempResourceComponent = new Directory();
        } else {
            tempResourceComponent = new File();
        }
        tempResourceComponent.setName(resource.getAlias());
        tempResourceComponent.setFullName(resource.getFullName().replaceFirst("/", ""));
        tempResourceComponent.setId(resource.getId());
        tempResourceComponent.setPid(resource.getPid());
        tempResourceComponent.setIdValue(resource.getId(), resource.isDirectory());
        tempResourceComponent.setDescription(resource.getDescription());
        tempResourceComponent.setType(resource.getType());
        return tempResourceComponent;
    }

    /**
     * 是否是家目录，
     * 1、如果pid == -1，则为家目录
     * 2、如果pid != -1, pid所属的的资源不在这些资源列表里面，或者说是，有这个目录（文件）的权限，但是没有该资源上一层级的权限
     *
     * @param resource
     * @return
     */
    private boolean isRootNode(Resource resource) {
        boolean isRootNode = true;
        if (resource.getPid() != -1) {
            for (Resource res : resourceList) {
                if (res.getPid() == resource.getId()) {
                    isRootNode = false;
                    break;
                }
            }
        }
        return isRootNode;
    }
}
