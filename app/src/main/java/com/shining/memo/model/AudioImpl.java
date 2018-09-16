package com.shining.memo.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

public class AudioImpl implements AudioModel{

    private  MemoDatabaseHelper dbHelper;
    private Context mContext;

    public AudioImpl(Context context){
        mContext = context;
        dbHelper=new MemoDatabaseHelper(mContext,"memo.db",null,1);
    }

    @Override
    public JSONObject getAudio() {
        return null;
    }

    @Override
    public void saveAudio(Audio audio) {
        SQLiteDatabase db =  dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            db.execSQL("insert into audio(taskId,path)"+
                    "values(?,?)",new Object[]{audio.getTaskId(),audio.getPath()});
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }
}
