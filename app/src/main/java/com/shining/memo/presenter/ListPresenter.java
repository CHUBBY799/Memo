package com.shining.memo.presenter;

import android.content.Context;

import com.shining.memo.bean.ListBean;
import com.shining.memo.model.ListImpl;
import com.shining.memo.model.ListModel;
import com.shining.memo.view.ViewList;

public class ListPresenter {
    private ListModel listModel;

    public ListPresenter(ViewList listView){
        Context context = listView.getContext();
        this.listModel = new ListImpl(context);
    }

    /**
     * 插入数据控制逻辑单元
     */
    public void insertPresenter(ListBean listBean){
        insertData(listBean);
    }

    /**
     * 删除数据控制逻辑单元
     */
    public void deletePresenter(String id){
        deleteDataById(id);
    }

    /**
     * 更新数据控制逻辑单元
     */
    public void updatePresenter(ListBean listBean){
        updateDataById(listBean);
    }

    private void insertData(ListBean listBean){
        listModel.insertData(listBean);
    }

    private void deleteDataById(String id){
        listModel.deleteDataById(id);
    }

    private void updateDataById(ListBean listBean){
        listModel.updateDataById(listBean);
    }
}
