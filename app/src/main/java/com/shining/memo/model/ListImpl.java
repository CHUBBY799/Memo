package com.shining.memo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shining.memo.bean.ListBean;

public class ListImpl implements ListModel {

    private SQLiteDatabase db;

    public ListImpl(Context context){
        MemoDatabaseHelper dbHelper = new MemoDatabaseHelper(context, "memo.db", null, 1);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public void insertData(ListBean listBean){
        ContentValues values = new ContentValues();
        values.put("finished", listBean.getFinished());
        values.put("title", listBean.getTitle());
        values.put("itemArr",listBean.getItemArr());
        values.put("date", listBean.getDate());
        db.insert("tb_list", null, values);
    }

    @Override
    public ListBean[] queryAllData(){
        Cursor cursor = db.query("tb_list",null,null,null,null,null,null);
        ListBean[] listBeans = new ListBean[cursor.getCount()];
        int m = 0;
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int finished = cursor.getInt(cursor.getColumnIndex("finished"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String itemArr = cursor.getString(cursor.getColumnIndex("itemArr"));
                String date = cursor.getString(cursor.getColumnIndex("date"));

                ListBean listBean = new ListBean();
                listBean.setId(id);
                listBean.setFinished(finished);
                listBean.setTitle(title);
                listBean.setItemArr(itemArr);
                listBean.setDate(date);
                listBeans[m++] = listBean;
            }while (cursor.moveToNext());
        }
        cursor.close();
        return listBeans;
    }

    @Override
    public void deleteDataById(String id){
        db.delete("tb_list","id = ?", new String[]{id});
    }

    @Override
    public void updateDataById(ListBean listBean){
        ContentValues values = new ContentValues();
        values.put("finished", listBean.getFinished());
        values.put("title", listBean.getTitle());
        values.put("itemArr", listBean.getItemArr());
        values.put("date", listBean.getDate());
        db.update("tb_list", values, "id = ?", new String[]{String.valueOf(listBean.getId())});
    }

    @Override
    public void updateAllDataById(ListBean[] listBeans){
        for (ListBean listBean : listBeans){
            ContentValues values = new ContentValues();
            values.put("finished", listBean.getFinished());
            values.put("itemArr", listBean.getItemArr());
            values.put("date", listBean.getDate());
            db.update("tb_list", values, "id = ?", new String[]{String.valueOf(listBean.getId())});
        }
    }

}
