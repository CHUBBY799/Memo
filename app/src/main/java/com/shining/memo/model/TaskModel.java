package com.shining.memo.model;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

import java.util.List;

public interface TaskModel {
    List<Task> getTasksByDate(String date, int limit);
    List<JSONObject> getAlarmTasksByUrgentDesc(int urgent);
    List<JSONObject> getNotAlarmTasksByUrgentDesc(int urgent);
    long addTask(Task task,SQLiteDatabase db);
    Task getTask(int taskId,SQLiteDatabase db);
    String getTitle(int taskId,SQLiteDatabase db);
    void deleteTask(int taskId,SQLiteDatabase db);
    void modifyTask(Task task,SQLiteDatabase db);
    void modifyTaskUrgent(int taskId,int urgent,SQLiteDatabase db);
    void modifyTaskAlarm(int taskId,int alarm,SQLiteDatabase db);
    void modifyTaskDeleted(int taskId,int deleted,SQLiteDatabase db);
    void modifyTaskFinished(int taskId,int finished,SQLiteDatabase db);
    boolean hasAudioById(long id);
    void finishTaskById(int id);
}
