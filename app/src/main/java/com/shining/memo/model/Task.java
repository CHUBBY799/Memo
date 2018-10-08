package com.shining.memo.model;

import java.sql.Date;

public class Task {
    private long id;
    private String type;   // audio or text
    private String date;
    private String time;
    private int urgent;   // 0：false 1：true
    private int alarm;    // 0：false 1：true
    private String title;
    private int deleted;  // 0：false 1：true
    private int finished; // 0 :false 1:true

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUrgent() {
        return urgent;
    }

    public void setUrgent(int urgent) {
        this.urgent = urgent;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", urgent=" + urgent +
                ", alarm=" + alarm +
                ", title='" + title + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
