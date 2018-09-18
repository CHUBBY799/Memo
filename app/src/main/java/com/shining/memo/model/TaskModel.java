package com.shining.memo.model;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

import java.util.List;

public interface TaskModel {
    List<Task> getTasksByDate(String date, int limit);
    List<JSONObject> getAlarmTasksByUrgentDesc(int urgent);
    List<JSONObject> getNotAlarmTasksByUrgentDesc(int urgent);
    long addTask(Task task,SQLiteDatabase db);
}
