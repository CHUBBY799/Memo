package com.shining.memo.model;

import org.json.JSONObject;

import java.util.List;

public interface TextModel {
    JSONObject getInfo(String title);
    void setInfo(JSONObject textInfo);
    List<Task> getTasksByDate(String date,int limit);
    long addTask(Task task);
    List<JSONObject> getAlarmTasksByUrgentDesc(int urgent);
    long addAlarm(Alarm alarm);
    List<JSONObject> getNotAlarmTasksByUrgentDesc(int urgent);
}
