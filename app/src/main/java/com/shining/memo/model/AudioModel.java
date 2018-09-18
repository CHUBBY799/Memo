package com.shining.memo.model;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

public interface AudioModel {
    JSONObject getAudio(int taskId,SQLiteDatabase db);
    void saveAudio(Audio audio,SQLiteDatabase db);
    void modifyAudio(int taskId,String newPath,SQLiteDatabase db);
    void deleteAudio(int taskId,SQLiteDatabase db);
}
