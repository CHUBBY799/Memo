package com.shining.memo.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MemoDatabaseHelper extends SQLiteOpenHelper{
    public static final String CREATE_TASK="create table task("
            +"id integer primary key autoincrement, "
            +"type text, "
            +"date text, "
            +"time text, "
            +"urgent integer, "
            +"alarm integer, "
            +"title text, "
            +"deleted integer, "
            +"finished integer)";
    public static final String CREATE_ALARM="create table alarm("
            +"id integer primary key autoincrement,"
            +"taskId integer,"
            +"date text,"
            +"time text,"
            +"path text,"
            +"pop integer)";
    public static final String CREATE_RECORDING="create table recording("
            +"id integer primary key autoincrement,"
            +"taskId integer,"
            +"recordingInfo text)";
    public static final String CREATE_TB_LIST="create table tb_list("
            +"id integer primary key autoincrement,"
            +"state integer,"
            +"title text,"
            +"itemArr text)";
    public MemoDatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("SQLiteDatabase", "onCreate:() ");
        sqLiteDatabase.execSQL(CREATE_TASK);
        sqLiteDatabase.execSQL(CREATE_ALARM);
        sqLiteDatabase.execSQL(CREATE_RECORDING);
        sqLiteDatabase.execSQL(CREATE_TB_LIST);
    }
}
