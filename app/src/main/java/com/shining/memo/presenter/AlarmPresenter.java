package com.shining.memo.presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.shining.memo.model.Alarm;
import com.shining.memo.model.AlarmImpl;
import com.shining.memo.model.AlarmModel;
import com.shining.memo.model.MemoDatabaseHelper;

public class AlarmPresenter {

    private Context context;
    private AlarmModel alarmModel;
    private MemoDatabaseHelper dbHelper;

    public AlarmPresenter(Context context) {
        this.context = context;
        this.alarmModel = new AlarmImpl();
        dbHelper=new MemoDatabaseHelper(context,"memo.db",null,1);
    }

    public Alarm getAlarm(int taskId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Alarm alarm = null;
        db.beginTransaction();
        try{
            alarm = alarmModel.getAlarm(taskId,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            db.endTransaction();
        }
        return alarm;
    }

    public boolean modifyAlarm(Alarm alarm){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            alarmModel.modifyAlarm(alarm,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
        }
        return true;
    }

    public boolean deleteAlarm(int taskId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            alarmModel.deleteAlarm(taskId,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
        }
        return true;
    }
}
