package com.shining.memo.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shining.memo.utils.Utils;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CalendarImpl implements CalendarModel{

    private SQLiteDatabase db;

    public CalendarImpl(Context context){
        MemoDatabaseHelper dbHelper = new MemoDatabaseHelper(context, "memo.db", null, 1);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public JSONArray queryData(List<LocalDate> dateList){
        JSONArray taskDataArr = new JSONArray();
        int length = dateList.size();
        for(int i = 0 ; i < length ; i++){
            Cursor cursor = db.query("task", new String[]{"id", "title", "finished"},"date = ?",new String[]{dateList.get(i).toString()},null,null,null);
            if(cursor.moveToFirst()){
                do{
                    try{
                        JSONObject taskData = new JSONObject();
                        taskData.put("id", cursor.getInt(cursor.getColumnIndex("id")));
                        taskData.put("title", cursor.getString(cursor.getColumnIndex("title")));
                        taskData.put("finished", cursor.getInt(cursor.getColumnIndex("finished")));
                        taskData.put("day", Utils.formatTimeUnit(dateList.get(i).getDayOfMonth()));
                        taskData.put("month", Utils.formatMonthSimUS(dateList.get(i).getMonthOfYear()));
                        taskDataArr.put(taskData);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        return taskDataArr;
    }
}
