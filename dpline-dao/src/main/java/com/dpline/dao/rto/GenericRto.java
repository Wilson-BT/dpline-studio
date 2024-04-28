package com.dpline.dao.rto;

import com.dpline.dao.entity.OrderByClause;

import java.util.List;

public class GenericRto<T> {

    private int page=1;  //页码

    private int pageSize = 10; //每页记录数

    /**
     * 排序对象
     */
    private List<OrderByClause> orderByClauses;

    private T vo;

    public T getVo() {
        return vo;
    }

    public void setVo(T vo) {
        this.vo = vo;
    }

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

//    public String getOrderByClauses() {
//        if (!CollectionUtils.isEmpty(orderByClauses)){
//            StringBuilder stringBuilder = new StringBuilder(" ORDER  BY ");
//            ArrayList<String> stringArrayList = new ArrayList<>();
//            for (OrderByClause orderBy:orderByClauses) {
//                String str;
//                if (orderBy.getOrderByMode() == 0){
//                    str = orderBy.getField() + " ASC";
//                }else {
//                    str = orderBy.getField() + " DESC";
//                }
//                stringArrayList.add(str);
//            }
//            String collect = stringArrayList.stream().collect(Collectors.joining(","));
//            return stringBuilder.append(collect).toString();
//        }
//        return null;
//    }
    public List<OrderByClause> getOrderByClauses() {
        return orderByClauses;
    }


    public void setOrderByClauses(List<OrderByClause> orderByClauses) {
        this.orderByClauses = orderByClauses;
    }
}