package com.shining.memo.bean;

public class ListBean {
    private long id;
    private int selected;
    private String title;
    private String itemArr;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSelected(){
        return selected;
    }

    public void setSelected(int selected){
        this.selected = selected;
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
