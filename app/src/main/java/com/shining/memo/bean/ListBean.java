package com.shining.memo.bean;

public class ListBean {
    private long id;
    private int finished;
    private String title;
    private String itemArr;
    private String date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFinished(){
        return finished;
    }

    public void setFinished(int finished){
        this.finished = finished;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
