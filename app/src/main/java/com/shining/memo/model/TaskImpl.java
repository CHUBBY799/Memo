package com.shining.memo.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskImpl implements TaskModel {
    private  MemoDatabaseHelper dbHelper;
    private Context mContext;
    public TaskImpl(Context context){
        mContext=context;
        dbHelper=new MemoDatabaseHelper(mContext,"memo.db",null,1);
    }

    @Override
    public List<Task> getTasksByDate(String date, int limit) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Task task=null;
        List<Task> tasks=new ArrayList<>();
        Cursor cursor=db.rawQuery("select * from task where deleted = ? and date = ? limit ?"
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
        cursor.close();
        return tasks;
    }

    @Override
    public long addTask(Task task,SQLiteDatabase db) {
        db.execSQL("insert into task(type,date,time,urgent,alarm,title,deleted,finished) " +
                "values(?,date(?,'localtime'),time(?,'localtime'),?,?,?,0,0)",new Object[]{
                task.getType(),"now","now",task.getUrgent(),task.getAlarm(),task.getTitle()
        });
        Cursor cursor=db.rawQuery("select last_insert_rowid() from task",null);
        int strid=-1;
        if(cursor.moveToFirst()){
            strid=cursor.getInt(0);
        }
        return strid;
    }

    private final String selectColNames=" t.id taskId" +
            ",t.type"+
            ",t.title" +
            ",a.id alarmId" +
            ",a.date alarmDate" +
            ",a.time alarmTime ";
    @Override
    public List<JSONObject> getAlarmTasksByUrgentDesc(int urgent) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        List<JSONObject> tasks=new ArrayList<>();
        Cursor cursor=db.rawQuery("select" +selectColNames
                + "from task t inner join alarm a on t.id=a.taskId "
                + "where urgent=? and deleted=0 "
                + "order by alarmDate desc,alarmTime desc",new String[]{String.valueOf(urgent)});
        try{
            while (cursor.moveToNext()){
                JSONObject object=new JSONObject();
                object.put("taskId",cursor.getLong(cursor.getColumnIndex("taskId")));
                object.put("type",cursor.getString(cursor.getColumnIndex("type")));
                object.put("title",cursor.getString(cursor.getColumnIndex("title")));
                object.put("alarmId",cursor.getString(cursor.getColumnIndex("alarmId")));
                object.put("alarmDate",cursor.getString(cursor.getColumnIndex("alarmDate")));
                object.put("alarmTime",cursor.getString(cursor.getColumnIndex("alarmTime")));
                object.put("urgent",urgent);
                object.put("alarm",1);
                tasks.add(object);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
        return tasks;
    }


    /*
     * date : YY-mm-DD  Tasktime: HH:MM:SS   Alarmtime: HH:MM
     */

    /*
     * return id of new insert
     */
    private long returnIdOfNew(SQLiteDatabase db,String tableName){
        Cursor cursor=db.rawQuery("select last_insert_rowid() from "+tableName,null);
        int strid=0;
        if(cursor.moveToFirst()){
            strid=cursor.getInt(0);
        }
        return strid;
    }

    @Override
    public List<JSONObject> getNotAlarmTasksByUrgentDesc(int urgent) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        List<JSONObject> list=new ArrayList<>();
        Cursor cursor=db.rawQuery("select id as taskId,type,title " +
                "from task " +
                "where urgent = ? and deleted =0 and alarm = 0 " +
                "order by date desc,time desc",new String[]{String.valueOf(urgent)});
        try{
            while (cursor.moveToNext()){
                JSONObject object=new JSONObject();
                object.put("taskId",cursor.getLong(cursor.getColumnIndex("taskId")));
                object.put("type",cursor.getString(cursor.getColumnIndex("type")));
                object.put("title",cursor.getString(cursor.getColumnIndex("title")));
                object.put("urgent",urgent);
                object.put("alarm",0);
                list.add(object);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
        return list;
    }
}
