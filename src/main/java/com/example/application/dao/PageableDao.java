package com.example.application.dao;

import java.util.List;

public interface PageableDao<T> {

  default List<T> getListByPage(List<T> list, int pageSize, int pageNum) {
    return list
        .stream()
        .skip((long) (pageNum - 1) * pageSize)
        .limit(pageSize)
        .toList();
  }
}
