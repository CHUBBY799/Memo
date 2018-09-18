package com.shining.memo.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class AudioImpl implements AudioModel{

    private Context mContext;

    public AudioImpl(Context context){
        mContext = context;
    }

    @Override
    public JSONObject getAudio(int taskId,SQLiteDatabase db) {
        JSONObject object = null;
        Cursor cursor = db.rawQuery("select id,path " +
                "from audio " +
                "where taskId = ? " +
                "order by id desc ",new String[]{String.valueOf(taskId)});
        if(cursor.moveToFirst()) {
            try {
                object = new JSONObject();
                object.put("id", cursor.getLong(cursor.getColumnIndex("id")));
                object.put("path", cursor.getLong(cursor.getColumnIndex("path")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    @Override
    public void saveAudio(Audio audio,SQLiteDatabase db) {
        db.execSQL("insert into audio(taskId,path) "+
                    "values(?,?) ",new Object[]{audio.getTaskId(),audio.getPath()});
    }

    @Override
    public void modifyAudio(int taskId,String newPath,SQLiteDatabase db) {
        db.execSQL("update audio " +
                    "set path = ? " +
                    "where taskId = ? ",new Object[]{newPath,taskId});
    }

    @Override
    public void deleteAudio(int taskId,SQLiteDatabase db) {
        db.execSQL("delete from audio " +
                "where taskId = ? ",new Object[]{taskId});

    }
}
