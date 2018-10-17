package com.shining.memo.presenter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.shining.memo.model.Alarm;
import com.shining.memo.model.AlarmImpl;
import com.shining.memo.model.AlarmModel;
import com.shining.memo.model.MemoDatabaseHelper;
import com.shining.memo.model.Task;
import com.shining.memo.model.TaskImpl;
import com.shining.memo.model.TaskModel;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmPresenter {

    private Context context;
    private AlarmModel alarmModel;
    private TaskModel taskModel;
    private MemoDatabaseHelper dbHelper;
    private AlarmManager alarmManager;

    public AlarmPresenter(Context context) {
        this.context = context;
        this.alarmModel = new AlarmImpl();
        this.taskModel = new TaskImpl(context);
        alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
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

    public boolean addAlarm(Alarm alarm){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            alarmModel.addAlarm(alarm,db);
            taskModel.modifyTaskAlarm((int)alarm.getTaskId(),1,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
        }
        return true;
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
            taskModel.modifyTaskAlarm(taskId,0,db);
            alarmCancel(taskId);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
        }
        return true;
    }

    public String getTaskTitle(int taskId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String title = "";
        db.beginTransaction();
        try{
            title = taskModel.getTitle(taskId,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }finally {
            db.endTransaction();
        }
        return title;
    }

    public void setAlarmNotice(int taskId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Alarm alarmObject = null;
        Task task = null;
        db.beginTransaction();
        try{
            alarmObject = alarmModel.getAlarm(taskId,db);
            task = taskModel.getTask(taskId,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
        if(alarmObject != null){
            if(alarmObject.getRingtone() != 0 || alarmObject.getPop() != 0){
                Intent intent = new Intent();
                intent.setAction("com.shining.memo.alarmandnotice");
                intent.setComponent(new ComponentName("com.shining.memo","com.shining.memo.receiver.AlarmReceiver"));
                intent.putExtra("ringtone",alarmObject.getRingtone());
                intent.putExtra("pop",alarmObject.getPop());
                intent.putExtra("taskId",taskId);
                intent.putExtra("title",task.getTitle());
                intent.putExtra("urgent",task.getUrgent());
                intent.putExtra("time",alarmObject.getTime());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,taskId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(sdf.parse(alarmObject.getDate()+" "+alarmObject.getTime()));
                    Log.d("alarmTime",sdf.parse(alarmObject.getDate()+" "+alarmObject.getTime()).toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
            }else {
                Intent intent = new Intent();
                intent.setAction("com.shining.memo.alarmandnotice");
                intent.setComponent(new ComponentName("com.shining.memo","com.shining.memo.receiver.AlarmReceiver"));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,taskId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    public void alarmCancel(int taskId){
        Intent intent = new Intent();
        intent.setAction("com.shining.memo.alarmandnotice");
        intent.setComponent(new ComponentName("com.shining.memo","com.shining.memo.receiver.AlarmReceiver"));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,taskId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

}
