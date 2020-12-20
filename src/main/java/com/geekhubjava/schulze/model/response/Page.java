package com.geekhubjava.schulze.model.response;

import java.util.List;

public class Page<T> {

    private int page;
    private int perPage;
    private int itemsTotal;
    private int pagesTotal;
    private List<T> items;

    public Page(int page, int perPage, int itemsTotal, List<T> items) {
        this.page = page;
        this.perPage = perPage;
        this.pagesTotal = (itemsTotal + perPage - 1) / perPage;
        this.itemsTotal = itemsTotal;
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPagesTotal() {
        return pagesTotal;
    }

    public void setPagesTotal(int pagesTotal) {
        this.pagesTotal = pagesTotal;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getItemsTotal() {
        return itemsTotal;
    }

    public void setItemsTotal(int itemsTotal) {
        this.itemsTotal = itemsTotal;
    }
}
