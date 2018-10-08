package com.shining.memo.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AlarmImpl implements AlarmModel{
    @Override
    public void addAlarm(Alarm alarm, SQLiteDatabase db) {
        db.execSQL("insert into alarm(taskId,date,time,path,pop) " +
                "values(?,?,?,?,?)",new Object[]{alarm.getTaskId(),alarm.getDate(),alarm.getTime(),alarm.getPath(),alarm.getPop()});
    }

    @Override
    public void modifyAlarm(Alarm alarm, SQLiteDatabase db) {
        db.execSQL("update alarm "
                +"set date = ?, "
                +"time = ?, "
                +"path = ?, "
                +"pop = ? "
                +"where taskId = ?",new Object[]{alarm.getDate(),alarm.getTime(),alarm.getPath(),alarm.getPop(),alarm.getTaskId()});
    }

    @Override
    public void deleteAlarm(int taskId, SQLiteDatabase db) {
        db.execSQL("delete from alarm "
                +"where taskId = ?",new Object[]{taskId});
    }

    @Override
    public Alarm getAlarm(int taskId, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select date,time,path,pop "
                +"from alarm "
                +"where taskId = ?",new String[]{String.valueOf(taskId)});
        if(cursor.moveToFirst()){
            Alarm alarm = new Alarm();
            alarm.setTaskId(taskId);
            alarm.setDate(cursor.getString(cursor.getColumnIndex("date")));
            alarm.setTime(cursor.getString(cursor.getColumnIndex("time")));
            alarm.setPath(cursor.getString(cursor.getColumnIndex("path")));
            alarm.setPop(cursor.getInt(cursor.getColumnIndex("pop")));
            return alarm;
        }
        return null;
    }
}
