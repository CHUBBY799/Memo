package com.shining.memo.presenter;

import android.content.Context;

import com.shining.memo.bean.ListBean;
import com.shining.memo.model.ListImpl;
import com.shining.memo.model.ListModel;
import com.shining.memo.view.ViewList;

public class ListPresenter {
    private Context context;
    private ViewList listView;
    private ListModel listModel;

    public ListPresenter(ViewList listView){
        this.context = listView.getContext();
        this.listView = listView;
        this.listModel = new ListImpl(context);
    }

    /**
     * 插入数据控制逻辑单元
     */
    public void insertPresenter(ListBean listBean){
        insertData(listBean);
    }

    /**
     * 查询全部数据控制逻辑单元
     */
    public ListBean[] queryPresenter(){
        return queryDataAll();
    }

    /**
     * 查询某一行数据控制逻辑单元
     */
    public ListBean queryPresenter(String title){
        return queryDataByTitle(title);
    }

    /**
     * 删除数据控制逻辑单元
     */
    public void deletePresenter(String title){
        deleteDataByTitle(title);
    }

    /**
     * 更新数据控制逻辑单元
     */
    public void updatePresenter(ListBean listBean){
        updateDataById(listBean);
    }

    private ListBean formatData(){
        if (listView != null){
            return listView.formatData();
        }else {
            return null;
        }
    }

    private void insertData(ListBean listBean){
        listModel.insertData(listBean);
    }

    private ListBean[] queryDataAll(){
        return listModel.queryAllData();
    }

    private ListBean queryDataByTitle(String title){
        return listModel.queryDataByTitle(title);
    }

    private void deleteDataByTitle(String title){
        listModel.deleteDataByTitle(title);
    }

    private void updateDataById(ListBean listBean){
        listModel.updateDataById(listBean);
    }
}
