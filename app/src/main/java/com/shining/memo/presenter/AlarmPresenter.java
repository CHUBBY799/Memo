package com.shining.memo.presenter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.text.Spanned;

import com.shining.memo.model.Alarm;
import com.shining.memo.model.AlarmImpl;
import com.shining.memo.model.AlarmModel;
import com.shining.memo.model.MemoDatabaseHelper;
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
        String title = "";
        db.beginTransaction();
        try{
            alarmObject = alarmModel.getAlarm(taskId,db);
            Spanned spanned = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                spanned = Html.fromHtml(taskModel.getTitle(taskId,db),Html.FROM_HTML_MODE_COMPACT);
            }
            if(spanned.length() > 0)
                title = spanned.subSequence(0,spanned.length() - 1).toString();
            else
                title = "";
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
                intent.putExtra("title",title);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,taskId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(sdf.parse(alarmObject.getDate()+" "+alarmObject.getTime()));
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

}