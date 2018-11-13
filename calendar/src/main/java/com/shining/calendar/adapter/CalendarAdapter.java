package com.shining.calendar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.shining.calendar.view.CalendarView;

import org.joda.time.LocalDate;

public abstract class CalendarAdapter extends PagerAdapter {


    protected Context mContext;
    protected int mCount;//总页数
    protected int mCurr;//当前位置
    protected SparseArray<CalendarView> mCalendarViews;
    protected LocalDate mDate;

    public CalendarAdapter(Context mContext, int count, int curr, LocalDate date) {
        this.mContext = mContext;
        this.mDate = date;
        this.mCurr = curr;
        this.mCount = count;
        mCalendarViews = new SparseArray<>();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public SparseArray<CalendarView> getCalendarViews() {
        return mCalendarViews;
    }
}
