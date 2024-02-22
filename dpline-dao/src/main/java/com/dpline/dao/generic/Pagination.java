package com.dpline.dao.generic;

import com.dpline.dao.rto.GenericRto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Pagination<T> extends PageSize {

	private List<T> rows = new ArrayList<>(); //当前返回的记录列表
	private int rowTotal = 0; //总记录数
	private int pageTotal = 0; //总页数

	@JsonIgnore
    private boolean count = true; //是否进行总记录统计

	@JsonIgnore
    private GenericRto criteria;


    public Pagination() {
        this(1, DEFAULT_PAGESIZE,true);
    }

    public static PaginationBuilder newBuilder() {
    	return new PaginationBuilder();
	}

    public static class PaginationBuilder {
		private int page = 0;
		private int pageSize = DEFAULT_PAGESIZE;
		private boolean count = true;
		private GenericRto criteria;
		public PaginationBuilder queryPage(int page, int pageSize) {
			if (page < 0 || pageSize < 1) {
				throw new IllegalArgumentException();
			}
			this.page = page;
			this.pageSize = pageSize;
			return this;
		}
		public PaginationBuilder count() {
			this.count = true;
			return this;
		}
		public PaginationBuilder notCount() {
			this.count = false;
			return this;
		}

		/**
		 * 不支持从 GenericRto 中提取page、pageSize参数
		 * @param criteria 属性命名集合
		 * @return PaginationBuilder
		 */
		public PaginationBuilder withCriteria(GenericRto criteria) {
			this.criteria = criteria;
			return this;
		}

		public <T> Pagination<T> build() {
			Pagination<T> pagination = new Pagination<T>();
			pagination.setPage(page);
			pagination.setPageSize(pageSize);
			pagination.setCount(count);
			pagination.setCriteria(criteria);
			return pagination;
		}
	}


    public Pagination(int page, int pageSize, boolean count) {
		setPage(page);
        setPageSize(pageSize);
        this.count = count;
    }

	public static <T> Pagination<T> getInstance(int page, int pageSize) {
        return new Pagination<T>(page, pageSize, true);
    }


    public static <T> Pagination<T> getInstance2Top(int top) {
        return new Pagination<T>(0, top,false);
    }

    public static <T> Pagination<T> getInstanceFromRto(GenericRto genericRto) {
		Pagination<T> pagination = new Pagination<T>(genericRto.getPage(), genericRto.getPageSize(), true);
		pagination.setCriteria(genericRto);
    	return pagination;
    }

    public static <T> Pagination<T> getInstance2Top4BO(GenericRto genericBO) {
		Pagination<T> pagination = new Pagination<T>(0, genericBO.getPageSize(), false);
		pagination.setCriteria(genericBO);
		return pagination;
    }

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public int getRowTotal() {
		return rowTotal;
	}

	public void setRowTotal(int rowTotal) {
		this.rowTotal = rowTotal;
	}

	public int getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}

    public boolean isCount() {
        return count;
    }

	public void setCount(boolean count) {
		this.count = count;
	}

    public GenericRto getCriteria() {
        return criteria;
    }

    public void setCriteria(GenericRto criteria) {
        this.criteria = criteria;
    }


}
