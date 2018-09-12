package com.shining.calendar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.shining.calendar.listener.OnClickMonthViewListener;
import com.shining.calendar.view.MonthView;

import org.joda.time.LocalDate;

public class MonthAdapter extends CalendarAdapter {

    private OnClickMonthViewListener mOnClickMonthViewListener;

    public MonthAdapter(Context mContext, int count, int curr, LocalDate date, OnClickMonthViewListener onClickMonthViewListener) {
        super(mContext, count, curr, date);
        this.mOnClickMonthViewListener = onClickMonthViewListener;
    }

    @Override
    public @NonNull Object instantiateItem(@NonNull ViewGroup container, int position) {

        MonthView nMonthView = (MonthView) mCalendarViews.get(position);
        if (nMonthView == null) {
            int i = position - mCurr;
            LocalDate date = this.mDate.plusMonths(i);
            nMonthView = new MonthView(mContext, date, mOnClickMonthViewListener);
            mCalendarViews.put(position, nMonthView);
        }
        container.addView(nMonthView);
        return nMonthView;
    }
}
