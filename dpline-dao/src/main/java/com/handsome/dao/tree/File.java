package com.handsome.dao.tree;

/**
 * 文件资源、属于叶子节点
 */
public class File extends ResourceComponent {
    @Override
    public boolean isDirctory() {
        return true;
    }
}
