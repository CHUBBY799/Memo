package com.shining.calendar.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.Toast;

import com.shining.calendar.R;
import com.shining.calendar.adapter.CalendarAdapter;
import com.shining.calendar.adapter.MonthAdapter;
import com.shining.calendar.listener.OnClickMonthViewListener;
import com.shining.calendar.listener.OnMonthCalendarChangedListener;
import com.shining.calendar.utils.Utils;
import com.shining.calendar.view.CalendarView;
import com.shining.calendar.view.MonthView;

import org.joda.time.LocalDate;

import java.util.List;

public class MonthCalendar extends CalendarPager implements OnClickMonthViewListener {

    private OnMonthCalendarChangedListener onMonthCalendarChangedListener;
    private int lastPosition = -1;
    private boolean button;

    public MonthCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected CalendarAdapter getCalendarAdapter() {

        mPageSize = Utils.getIntervalMonths(startDate, endDate) + 1;
        mCurrPage = Utils.getIntervalMonths(startDate, mInitialDate);

        return new MonthAdapter(getContext(), mPageSize, mCurrPage, mInitialDate, this);
    }


    @Override
    protected void initCurrentCalendarView(int position) {

        MonthView currView = (MonthView) calendarAdapter.getCalendarViews().get(position);
        MonthView lastView = (MonthView) calendarAdapter.getCalendarViews().get(position - 1);
        MonthView nextView = (MonthView) calendarAdapter.getCalendarViews().get(position + 1);


        if (currView == null) {
            return;
        }

        if (lastView != null)
            lastView.clear();

        if (nextView != null)
            nextView.clear();


        //只处理翻页
        if (lastPosition == -1) {
            currView.setDateAndPoint(mInitialDate, mSelectDateList, pointList);
            mSelectDate = mInitialDate;
            lastSelectDate = mInitialDate;
            if (onMonthCalendarChangedListener != null) {
                onMonthCalendarChangedListener.onMonthCalendarChanged(mSelectDate, mSelectDateList);
            }
        } else if (isPagerChanged) {
            int i = position - lastPosition;
            mSelectDate = mSelectDate.plusMonths(i);
            if (isDefaultSelect) {
                //日期越界
                if (mSelectDate.isAfter(endDate)) {
                    mSelectDate = endDate;
                } else if (mSelectDate.isBefore(startDate)) {
                    mSelectDate = startDate;
                }
                currView.setDateAndPoint(mSelectDate, mSelectDateList, pointList);
                if (onMonthCalendarChangedListener != null) {
                    onMonthCalendarChangedListener.onMonthCalendarChanged(mSelectDate, mSelectDateList);
                }
            } else {
                if (Utils.isEqualsMonth(lastSelectDate, mSelectDate)) {
                    currView.setDateAndPoint(lastSelectDate, mSelectDateList, pointList);
                }
            }

        }
        lastPosition = position;
    }

    public void setOnMonthCalendarChangedListener(OnMonthCalendarChangedListener onMonthCalendarChangedListener) {
        this.onMonthCalendarChangedListener = onMonthCalendarChangedListener;
    }

    @Override
    protected void setDate(LocalDate date, List<LocalDate> dateList) {
        if (date.isAfter(endDate)  || date.isBefore(startDate)) {
            Toast.makeText(getContext(), R.string.illegal_date, Toast.LENGTH_SHORT).show();
            return;
        }

        SparseArray<CalendarView> calendarViews = calendarAdapter.getCalendarViews();
        if (calendarViews.size() == 0) {
            return;
        }

        isPagerChanged = false;

        MonthView currentMonthView = getCurrentMonthView();
        LocalDate initialDate = currentMonthView.getInitialDate();

        //不是当月
        if (!Utils.isEqualsMonth(initialDate, date)) {
            int months = Utils.getIntervalMonths(initialDate, date);
            int i = getCurrentItem() + months;
            setCurrentItem(i, Math.abs(months) < 2);
            currentMonthView = getCurrentMonthView();
        }

        currentMonthView.setDateAndPoint(date, mSelectDateList, pointList);
        mSelectDate = date;
        lastSelectDate = date;
        mSelectDateList = dateList;

        isPagerChanged = true;

        if (onMonthCalendarChangedListener != null) {
            onMonthCalendarChangedListener.onMonthCalendarChanged(mSelectDate, mSelectDateList);
        }


    }

    @Override
    public void onClickCurrentMonth(LocalDate date) {
        dealClickEvent(date, getCurrentItem());
    }

    @Override
    public void onClickLastMonth(LocalDate date) {
        int currentItem = getCurrentItem() - 1;
        dealClickEvent(date, currentItem);
    }

    @Override
    public void onClickLastMonth(LocalDate date, boolean button){
        int currentItem = getCurrentItem() - 1;
        this.button = button;
        dealClickEvent(date, currentItem);
    }

    @Override
    public void onClickNextMonth(LocalDate date) {
        int currentItem = getCurrentItem() + 1;
        dealClickEvent(date, currentItem);
    }

    @Override
    public void onClickNextMonth(LocalDate date, boolean button) {
        int currentItem = getCurrentItem() + 1;
        this.button = button;
        dealClickEvent(date, currentItem);
    }

    private void dealClickEvent(LocalDate date, int currentItem) {

        if (date.isAfter(endDate)  || date.isBefore(startDate)) {
            Toast.makeText(getContext(), R.string.illegal_date, Toast.LENGTH_SHORT).show();
            return;
        }
        isPagerChanged = false;
        setCurrentItem(currentItem, true);
        MonthView nMonthView = getCurrentMonthView();
        nMonthView.setDateAndPoint(date, mSelectDateList, pointList);
        mSelectDate = date;
        lastSelectDate = date;
        if (!button){
            if (mSelectDateList.contains(mSelectDate)){
                mSelectDateList.remove(mSelectDate);
            }else {
                mSelectDateList.add(mSelectDate);
            }
            button = false;
        }

        isPagerChanged = true;

        if (onMonthCalendarChangedListener != null) {
            onMonthCalendarChangedListener.onMonthCalendarChanged(date, mSelectDateList);
        }
    }


    public MonthView getCurrentMonthView() {
        return (MonthView) calendarAdapter.getCalendarViews().get(getCurrentItem());
    }

}
