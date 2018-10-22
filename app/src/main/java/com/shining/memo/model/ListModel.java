package com.shining.memo.model;

import com.shining.memo.bean.ListBean;

public interface ListModel {
    void insertData(ListBean listBean);
    ListBean[] queryAllData();
    ListBean queryDataByTitle(String title);
    void deleteDataById(String id);
    void updateDataById(ListBean listBean);
    void updateAllDataById(ListBean[] listBeans);
}