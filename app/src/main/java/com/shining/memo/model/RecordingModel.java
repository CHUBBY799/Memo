package com.shining.memo.model;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

public interface RecordingModel {
    void addRecording(Recording recording, SQLiteDatabase db);
    void updateRecording(Recording recording, SQLiteDatabase db);
    void deleteRecording(int taskId, SQLiteDatabase db);
    Recording getRecording(int taskId, SQLiteDatabase db);
}
