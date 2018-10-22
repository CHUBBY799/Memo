package com.shining.calendar.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.Toast;

import com.shining.calendar.R;
import com.shining.calendar.adapter.CalendarAdapter;
import com.shining.calendar.adapter.WeekAdapter;
import com.shining.calendar.listener.OnClickWeekViewListener;
import com.shining.calendar.listener.OnWeekCalendarChangedListener;
import com.shining.calendar.utils.Attrs;
import com.shining.calendar.utils.Utils;
import com.shining.calendar.view.CalendarView;
import com.shining.calendar.view.WeekView;

import org.joda.time.LocalDate;

import java.util.List;

public class WeekCalendar extends CalendarPager implements OnClickWeekViewListener {

    private OnWeekCalendarChangedListener onWeekCalendarChangedListener;

    public WeekCalendar(Context context) {
        super(context);
    }

    public WeekCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected CalendarAdapter getCalendarAdapter() {

        mPageSize = Utils.getIntervalWeek(startDate, endDate, Attrs.firstDayOfWeek) + 1;
        mCurrPage = Utils.getIntervalWeek(startDate, mInitialDate, Attrs.firstDayOfWeek);

        return new WeekAdapter(getContext(), mPageSize, mCurrPage, mInitialDate, this);
    }


    private int lastPosition = -1;

    @Override
    protected void initCurrentCalendarView(int position) {

        WeekView currView = (WeekView) calendarAdapter.getCalendarViews().get(position);
        WeekView lastView = (WeekView) calendarAdapter.getCalendarViews().get(position - 1);
        WeekView nextView = (WeekView) calendarAdapter.getCalendarViews().get(position + 1);
        if (currView == null)
            return;

        if (lastView != null)
            lastView.clear();

        if (nextView != null)
            nextView.clear();

        //只处理翻页
        if (lastPosition == -1) {
            currView.setDateAndPoint(mInitialDate, mSelectDateList, pointList);
            mSelectDate = mInitialDate;
            lastSelectDate = mInitialDate;
            if (onWeekCalendarChangedListener != null) {
                onWeekCalendarChangedListener.onWeekCalendarChanged(mSelectDate, mSelectDateList);
            }
        } else if (isPagerChanged) {
            if (isDefaultSelect) {
                //日期越界
                if (mSelectDate.isAfter(endDate)) {
                    mSelectDate = endDate;
                } else if (mSelectDate.isBefore(startDate)) {
                    mSelectDate = startDate;
                }

                currView.setDateAndPoint(mSelectDate, mSelectDateList, pointList);
                if (onWeekCalendarChangedListener != null) {
                    onWeekCalendarChangedListener.onWeekCalendarChanged(mSelectDate, mSelectDateList);
                }
            } else {
                if (Utils.isEqualsMonth(lastSelectDate, mSelectDate)) {
                    currView.setDateAndPoint(lastSelectDate, mSelectDateList, pointList);
                }
            }

        }
        lastPosition = position;
    }

    public void setOnWeekCalendarChangedListener(OnWeekCalendarChangedListener onWeekCalendarChangedListener) {
        this.onWeekCalendarChangedListener = onWeekCalendarChangedListener;
    }


    @Override
    protected void setDate(LocalDate date, List<LocalDate> dateList) {

        if (date.isAfter(endDate) || date.isBefore(startDate)) {
            Toast.makeText(getContext(), R.string.illegal_date, Toast.LENGTH_SHORT).show();
            return;
        }

        SparseArray<CalendarView> calendarViews = calendarAdapter.getCalendarViews();
        if (calendarViews.size() == 0) {
            return;
        }

        isPagerChanged = false;

        WeekView currentWeekView = (WeekView) calendarViews.get(getCurrentItem());

        //不是当周
        if (!currentWeekView.contains(date)) {

            LocalDate initialDate = currentWeekView.getInitialDate();
            int weeks = Utils.getIntervalWeek(initialDate, date, Attrs.firstDayOfWeek);
            int i = getCurrentItem() + weeks;
            setCurrentItem(i, Math.abs(weeks) < 2);
            currentWeekView = (WeekView) calendarViews.get(getCurrentItem());
        }

        currentWeekView.setDateAndPoint(date, mSelectDateList, pointList);
        mSelectDate = date;
        lastSelectDate = date;
        mSelectDateList = dateList;

        isPagerChanged = true;

        if (onWeekCalendarChangedListener != null) {
            onWeekCalendarChangedListener.onWeekCalendarChanged(mSelectDate, mSelectDateList);
        }
    }


    @Override
    public void onClickCurrentWeek(LocalDate date) {

        if (date.isAfter(endDate) || date.isBefore(startDate)) {
            Toast.makeText(getContext(), R.string.illegal_date, Toast.LENGTH_SHORT).show();
            return;
        }

        WeekView weekView = (WeekView) calendarAdapter.getCalendarViews().get(getCurrentItem());
        weekView.setDateAndPoint(date, mSelectDateList, pointList);
        mSelectDate = date;
        lastSelectDate = date;
        if (mSelectDateList.contains(mSelectDate)){
            mSelectDateList.remove(mSelectDate);
        }else {
            mSelectDateList.add(mSelectDate);
        }
        if (onWeekCalendarChangedListener != null) {
            onWeekCalendarChangedListener.onWeekCalendarChanged(date, mSelectDateList);
        }

    }
}
