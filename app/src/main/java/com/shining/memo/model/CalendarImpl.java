package com.shining.memo.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shining.memo.utils.Utils;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;

public class  CalendarImpl implements CalendarModel{

    private SQLiteDatabase db;

    public CalendarImpl(Context context){
        MemoDatabaseHelper dbHelper = new MemoDatabaseHelper(context, "memo.db", null, 1);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public HashSet<String> queryData(String year_month){
        HashSet<String> dateList = new HashSet<>();
        Cursor cursor = db.query("task", new String[]{"date"},"date like ? and finished = ?",new String[]{year_month + "-__", "0"},null,null,null);
        if(cursor.moveToFirst()){
            do{
                dateList.add(cursor.getString(cursor.getColumnIndex("date")));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return dateList;
    }

    @Override
    public JSONArray queryData(List<LocalDate> dateList){
        JSONArray taskDataArr = new JSONArray();
        int length = dateList.size();
        for(int i = 0 ; i < length ; i++){
            Cursor cursor = db.query("task", new String[]{"id", "title"},"date = ? and finished = ?",new String[]{dateList.get(i).toString(), "0"},null,null,null);
            if(cursor.moveToFirst()){
                do{
                    try{
                        JSONObject taskData = new JSONObject();
                        taskData.put("id", cursor.getInt(cursor.getColumnIndex("id")));
                        taskData.put("title", cursor.getString(cursor.getColumnIndex("title")));
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
