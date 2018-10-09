package com.shining.memo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shining.memo.bean.ListBean;

public class ListImpl implements ListModel {

    private SQLiteDatabase db;
    private Cursor cursor;

    public ListImpl(Context context){
        MemoDatabaseHelper dbHelper = new MemoDatabaseHelper(context, "memo.db", null, 1);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public void insertData(ListBean listBean){
        ContentValues values = new ContentValues();
        values.put("selected", listBean.getSelected());
        values.put("title", listBean.getTitle());
        values.put("itemArr",listBean.getItemArr());
        db.insert("tb_list", null, values);
    }

    @Override
    public ListBean[] queryAllData(){
        cursor = db.query("tb_list",null,null,null,null,null,null);
        ListBean[] listBeans = new ListBean[cursor.getCount()];
        int m = 0;
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int selected = cursor.getInt(cursor.getColumnIndex("selected"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String itemArr = cursor.getString(cursor.getColumnIndex("itemArr"));

                ListBean listBean = new ListBean();
                listBean.setId(id);
                listBean.setSelected(selected);
                listBean.setTitle(title);
                listBean.setItemArr(itemArr);
                listBeans[m++] = listBean;
            }while (cursor.moveToNext());
        }
        cursor.close();
        return listBeans;
    }

    @Override
    public ListBean queryDataByTitle(String title){
        cursor = db.query("tb_list",null,"title = ?",new String[]{title},null,null,null);
        ListBean listBean = new ListBean();
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String itemArr = cursor.getString(cursor.getColumnIndex("itemArr"));
                listBean.setId(id);
                listBean.setTitle(title);
                listBean.setItemArr(itemArr);

            }while (cursor.moveToNext());
        }
        cursor.close();
        return listBean;
    }

    @Override
    public void deleteDataByTitle(String title){
        db.delete("tb_list","title = ?",new String[]{title});
    }

    @Override
    public void updateDataById(ListBean listBean){
        ContentValues values = new ContentValues();
        values.put("selected", listBean.getSelected());
        values.put("title", listBean.getTitle());
        values.put("itemArr", listBean.getItemArr());
        db.update("tb_list", values, "id = ?", new String[]{String.valueOf(listBean.getId())});
    }

    @Override
    public void updateAllDataById(ListBean[] listBeans){
        for (ListBean listBean : listBeans){
            ContentValues values = new ContentValues();
            values.put("selected", listBean.getSelected());
            values.put("itemArr", listBean.getItemArr());
            db.update("tb_list", values, "id = ?", new String[]{String.valueOf(listBean.getId())});
        }
    }
}
