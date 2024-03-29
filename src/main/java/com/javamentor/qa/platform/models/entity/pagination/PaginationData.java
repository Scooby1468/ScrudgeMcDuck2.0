package com.javamentor.qa.platform.models.entity.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationData {
    private int currentPage;
    private int itemsOnPage;
    private Map<String, Object> props = new HashMap<>();
    private String daoName;
    private String filter;

    public PaginationData(int currentPage, int itemsOnPage, String daoName) {
        this.currentPage = currentPage;
        this.itemsOnPage = itemsOnPage;
        this.daoName = daoName;
        this.props = new HashMap<>();
    }

    public PaginationData(int currentPage, int itemsOnPage, String daoName, String filter) {
        this.currentPage = currentPage;
        this.itemsOnPage = itemsOnPage;
        this.daoName = daoName;
        this.props = new HashMap<>();
        this.filter = filter;
    }
}
