package com.example.application.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Page<T> {
  private int pageNumber;
  private int pageSize;
  private List<T> items;
}
