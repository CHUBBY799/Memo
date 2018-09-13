package com.shining.memo.model;

import org.json.JSONObject;

import java.util.List;

public interface TaskModel {
    List<Task> getTasksByDate(String date, int limit);
    long addTask(Task task);
    List<JSONObject> getAlarmTasksByUrgentDesc(int urgent);
    List<JSONObject> getNotAlarmTasksByUrgentDesc(int urgent);
}
