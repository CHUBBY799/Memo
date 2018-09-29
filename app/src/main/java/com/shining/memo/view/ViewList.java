package com.shining.memo.view;

import android.content.Context;

import com.shining.memo.bean.ListBean;


public interface ViewList {
    Context getContext();
    ListBean formatData();
}
