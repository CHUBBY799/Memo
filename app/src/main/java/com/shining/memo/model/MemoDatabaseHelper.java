package com.shining.memo.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;

public class MemoDatabaseHelper extends SQLiteOpenHelper{
    public static final String CREATE_TASK="create table task("
            +"id integer primary key autoincrement, "
            +"type text, "
            +"date text, "
            +"time text, "
            +"urgent integer, "
            +"alarm integer, "
            +"title text, "
            +"deleted integer)";
    public static final String CREATE_ALARM="create table alarm("
            +"id integer primary key autoincrement,"
            +"taskId integer,"
            +"date text,"
            +"time text,"
            +"path text,"
            +"pop integer)";
    public static final String CREATE_AUDIO="create table audio("
            +"id integer primary key autoincrement,"
            +"taskId integer,"
            +"path text)";
    public static final String CREATE_TEXT="create table text("
            +"id integer primary key autoincrement,"
            +"taskId integer,"
            +"content text,"
            +"color text)";
    private Context mContext;
    public MemoDatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext=context;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TASK);
        sqLiteDatabase.execSQL(CREATE_ALARM);
        sqLiteDatabase.execSQL(CREATE_AUDIO);
        sqLiteDatabase.execSQL(CREATE_TEXT);

    }

}
