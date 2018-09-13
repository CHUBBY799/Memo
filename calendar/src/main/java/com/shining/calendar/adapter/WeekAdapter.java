package com.shining.calendar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.shining.calendar.listener.OnClickWeekViewListener;
import com.shining.calendar.view.WeekView;

import org.joda.time.LocalDate;

public class WeekAdapter extends CalendarAdapter {

    private OnClickWeekViewListener mOnClickWeekViewListener;

    public WeekAdapter(Context mContext, int count, int curr, LocalDate date, OnClickWeekViewListener onClickWeekViewListener) {
        super(mContext, count, curr, date);
        this.mOnClickWeekViewListener = onClickWeekViewListener;
    }


    @Override
    public @NonNull Object instantiateItem(@NonNull ViewGroup container, int position) {

        WeekView nWeekView = (WeekView) mCalendarViews.get(position);
        if (nWeekView == null) {
            nWeekView = new WeekView(mContext, mDate.plusDays((position - mCurr) * 7),mOnClickWeekViewListener);
            mCalendarViews.put(position, nWeekView);
        }
        container.addView(mCalendarViews.get(position));
        return mCalendarViews.get(position);
    }
}
