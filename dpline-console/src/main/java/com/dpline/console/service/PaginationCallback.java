package com.dpline.console.service;


import com.dpline.dao.generic.Pagination;

import java.util.List;


public interface PaginationCallback<T> {

    List<T> execute(Pagination<T> pagination);
}
