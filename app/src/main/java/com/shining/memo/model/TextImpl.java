package com.shining.memo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.JsonReader;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TextImpl implements TextModel{
    private static final String TAG = "TextImpl";
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
    public long addAlarm(Alarm alarm) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("insert into alarm(taskId,date,time,path,pop) values(?,?,?,?,?)",
                new Object[]{alarm.getTaskId(),alarm.getDate(),alarm.getTime(),alarm.getPath(),
                alarm.getPop()});
        return returnIdOfNew(db,"alarm");
    }


}
