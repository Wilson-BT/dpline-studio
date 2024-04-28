package com.dpline.dao.generic;

public class PageSize {

    public static int DEFAULT_PAGESIZE = 10;

    protected int page;  //页码

    protected int pageSize = DEFAULT_PAGESIZE; //每页记录数

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
