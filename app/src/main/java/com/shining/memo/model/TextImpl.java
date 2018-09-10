package com.shining.memo.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TextImpl implements TextModel{
    private  MemoDatabaseHelper dbHelper;
    private Context mContext;

    public TextImpl(Context context){
        mContext=context;
        dbHelper=new MemoDatabaseHelper(mContext,"memo.db",null,1);
    }
    @Override
    public JSONObject getInfo(String title){
        return null;
    }
    @Override
    public  void  setInfo(JSONObject textInfo){
        try {
            String type = textInfo.getString("type");
            String title =textInfo.getString("title");
            String content = textInfo.getString("content");
            String color = textInfo.getString("color");
            int urgent = textInfo.getInt("urgent");
            String date = textInfo.getString("date");
            String time = textInfo.getString("time");
            Log.d("TextImpl", type + " " + title + " " + content + " " + color + " " + urgent + " " + date + " " + time);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public List<Task> getTasksByDate(String date,int limit) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Task task=null;
        List<Task> tasks=new ArrayList<>();
        Cursor cursor=db.rawQuery("select * from main where deleted = ? and date = ? limit ?"
                ,new String[]{"0",date,String.valueOf(limit)});
        if(cursor.moveToNext()){
             task=new Task();
             task.setId(cursor.getInt(cursor.getColumnIndex("id")));
             task.setType(cursor.getString(cursor.getColumnIndex("type")));
             task.setDate(cursor.getString(cursor.getColumnIndex("date")));
             task.setTime(cursor.getString(cursor.getColumnIndex("time")));
             task.setUrgent(cursor.getInt(cursor.getColumnIndex("urgent")));
             task.setAlarm(cursor.getInt(cursor.getColumnIndex("alarm")));
             task.setTitle(cursor.getString(cursor.getColumnIndex("title")));
             task.setDeleted(0);
             tasks.add(task);
        }
        return tasks;
    }
}
