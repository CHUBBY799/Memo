package com.shining.memo.model;

public class Alarm {
    private long id;
    private long taskId;
    private String date;
    private String time;
    private int ringtone;
    private int pop;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
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

    public int getRingtone() {
        return ringtone;
    }

    public void setRingtone(int ringtone) {
        this.ringtone = ringtone;
    }

    public int getPop() {
        return pop;
    }

    public void setPop(int pop) {
        this.pop = pop;
    }
}
