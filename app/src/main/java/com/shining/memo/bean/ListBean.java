package com.shining.memo.bean;

public class ListBean {
    private long id;
    private boolean state;
    private String title;
    private String itemArr;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getItemArr() {
        return itemArr;
    }

    public void setItemArr(String itemArr) {
        this.itemArr = itemArr;
    }
}
