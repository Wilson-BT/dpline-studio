package com.handsome.dao.tree;

/**
 * 目录资源，可能为叶子节点
 */
public class Directory extends ResourceComponent {

    @Override
    public boolean isDirctory() {
        return true;
    }
}
