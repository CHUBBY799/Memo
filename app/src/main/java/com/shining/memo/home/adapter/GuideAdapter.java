package com.shining.memo.home.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class GuideAdapter extends PagerAdapter {

    private ArrayList<View> guideLayouts;

    public GuideAdapter(ArrayList<View> guideLayouts) {
        this.guideLayouts = guideLayouts;
    }

    /**
     * 获取当前要显示对象的数量
     */
    @Override public int getCount(){
        // TODO Auto-generated method stub
        return guideLayouts.size();
    }

    /**
     * 判断是否用对象生成界面
     */
    @Override public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    /**
     * 从ViewGroup中移除当前对象
     */
    @Override public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(guideLayouts.get(position));
    }

    /**
     * 当前要显示的对象
     */
    @Override public @NonNull
    Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(guideLayouts.get(position));
        return guideLayouts.get(position);
    }
}