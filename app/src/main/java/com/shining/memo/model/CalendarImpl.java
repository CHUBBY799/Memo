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

    /**
     * 查询当月的全部完成数据
     * @param year_month 当前的月份
     * @param type 表格类型
     * @return 日期集合
     */
    @Override
    public HashSet<String> queryData(String year_month, String type){
        HashSet<String> dateList = new HashSet<>();
        Cursor cursor;
        switch (type){
            case "list":
                cursor = db.query("tb_list", new String[]{"date"},"date like ? and finished = ?", new String[]{year_month + "-__", "1"},null,null,null);
                if(cursor.moveToFirst()){
                    do{
                        dateList.add(cursor.getString(cursor.getColumnIndex("date")));
                    }while (cursor.moveToNext());
                }
                cursor.close();
                break;

            case "task":
                cursor = db.query("task", new String[]{"date"},"date like ? and finished = ? and category = ?", new String[]{year_month + "-__", "1", type},null,null,null);
                if(cursor.moveToFirst()){
                    do{
                        dateList.add(cursor.getString(cursor.getColumnIndex("date")));
                    }while (cursor.moveToNext());
                }
                cursor.close();
                break;

            case "note":
                cursor = db.query("task", new String[]{"date"},"date like ? and category = ?", new String[]{year_month + "-__", type},null,null,null);
                if(cursor.moveToFirst()){
                    do{
                        dateList.add(cursor.getString(cursor.getColumnIndex("date")));
                    }while (cursor.moveToNext());
                }
                cursor.close();
                break;

            default:
        }
        return dateList;
    }

    @Override
    public JSONArray queryData(List<LocalDate> dateList, String type){
        JSONArray taskDataArr = new JSONArray();
        int length = dateList.size();
        Cursor cursor;
        switch (type){
            case "list":
                for(int i = 0 ; i < length ; i++){
                    cursor = db.query("tb_list", new String[]{"id", "title", "itemArr"},"date = ? and finished = ?", new String[]{dateList.get(i).toString(), "1"},null,null,null);
                    if(cursor.moveToFirst()){
                        do{
                            try{
                                JSONObject taskData = new JSONObject();
                                taskData.put("id", cursor.getInt(cursor.getColumnIndex("id")));
                                taskData.put("title", cursor.getString(cursor.getColumnIndex("title")));
                                taskData.put("itemArr", cursor.getString(cursor.getColumnIndex("itemArr")));
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
                break;

            case "task":
                for(int i = 0 ; i < length ; i++){
                    cursor = db.query("task", new String[]{"id", "title"},"date = ? and finished = ? and category = ?", new String[]{dateList.get(i).toString(), "1", type},null,null,null);
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
                break;

            case "note":
                for(int i = 0 ; i < length ; i++){
                    cursor = db.query("task", new String[]{"id", "title"},"date = ? and category = ?", new String[]{dateList.get(i).toString(), type},null,null,null);
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
                break;
            default:
        }

        return taskDataArr;
    }
}
