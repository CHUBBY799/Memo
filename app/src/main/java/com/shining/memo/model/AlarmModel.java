package com.shining.memo.model;

import android.database.sqlite.SQLiteDatabase;

public interface AlarmModel {
    void addAlarm(Alarm alarm, SQLiteDatabase db);
    void modifyAlarm(Alarm alarm, SQLiteDatabase db);
    void deleteAlarm(int taskId, SQLiteDatabase db);
    Alarm getAlarm(int taskId, SQLiteDatabase db);
}
