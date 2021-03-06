package com.shining.calendar.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import com.shining.calendar.R;
import com.shining.calendar.adapter.CalendarAdapter;
import com.shining.calendar.utils.Attrs;
import com.shining.calendar.utils.Utils;
import com.shining.calendar.view.CalendarView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public abstract class CalendarPager extends ViewPager {

    protected CalendarAdapter calendarAdapter;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected int mPageSize;
    protected int mCurrPage;
    protected LocalDate mInitialDate;//日历初始化date，即今天
    protected LocalDate mSelectDate;//当前页面选中的date
    protected List<LocalDate> mSelectDateList = new ArrayList<>();
    protected List<String> pointList;//圆点

    protected boolean isPagerChanged = true;//是否是手动翻页
    protected LocalDate lastSelectDate;//上次选中的date
    protected boolean isDefaultSelect = true;//是否默认选中


    private OnPageChangeListener onPageChangeListener;

    public CalendarPager(Context context) {
        this(context, null);
    }

    public CalendarPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarPager);
        Attrs.currentColor = ta.getColor(R.styleable.CalendarPager_currentColor, ContextCompat.getColor(context, R.color.currentColor));
        Attrs.hintColor = ta.getColor(R.styleable.CalendarPager_hintColor, ContextCompat.getColor(context, R.color.hintColor));
        Attrs.todayColor = ta.getColor(R.styleable.CalendarPager_todayColor, ContextCompat.getColor(context, R.color.todayColor));
        Attrs.selectColor = ta.getColor(R.styleable.CalendarPager_selectColor, ContextCompat.getColor(context, R.color.selectColor));

        Attrs.textSize = ta.getDimension(R.styleable.CalendarPager_textSize, Utils.sp2px(context, 18));
        Attrs.pointSize = ta.getDimension(R.styleable.CalendarPager_pointSize, (int) Utils.dp2px(context, 3));

        Attrs.monthCalendarHeight = (int) ta.getDimension(R.styleable.CalendarPager_calendarHeight, Utils.dp2px(context, 340));

        Attrs.duration = ta.getInt(R.styleable.CalendarPager_duration, 240);
        Attrs.backgroundColor = ta.getColor(R.styleable.CalendarPager_backgroundColor, ContextCompat.getColor(context, R.color.white));

        String firstDayOfWeek = ta.getString(R.styleable.CalendarPager_firstDayOfWeek);
        String defaultCalendar = ta.getString(R.styleable.CalendarPager_defaultCalendar);

        String startString = ta.getString(R.styleable.CalendarPager_startDate);
        String endString = ta.getString(R.styleable.CalendarPager_endDate);

        Attrs.firstDayOfWeek = "Monday".equals(firstDayOfWeek) ? 1 : 0;
        Attrs.defaultCalendar = "Week".equals(defaultCalendar) ? NCalendar.WEEK : NCalendar.MONTH;

        ta.recycle();

        mInitialDate = new LocalDate();

        startDate = new LocalDate(startString == null ? "1994-02-08" : startString);
        endDate = new LocalDate(endString == null ? "2094-12-31" : endString);

        setDateInterval(null, null);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initCurrentCalendarView(mCurrPage);
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        setBackgroundColor(Attrs.backgroundColor);
    }

    public void setDateInterval(String startString,String endString) {
        if (startString != null && !"".equals(startString)) {
            startDate = new LocalDate(startString);
        }
        if (endString != null && !"".equals(endString)) {
            endDate = new LocalDate(endString);
        }

        if (mInitialDate.isBefore(startDate) || mInitialDate.isAfter(endDate)) {
            throw new RuntimeException(getResources().getString(R.string.range_date));
        }

        calendarAdapter = getCalendarAdapter();
        setAdapter(calendarAdapter);
        setCurrentItem(mCurrPage);


        if (onPageChangeListener != null) {
            removeOnPageChangeListener(onPageChangeListener);
        }

        onPageChangeListener = new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                initCurrentCalendarView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        addOnPageChangeListener(onPageChangeListener);
    }

    protected abstract CalendarAdapter getCalendarAdapter();

    protected abstract void initCurrentCalendarView(int position);

    protected abstract void setDate(LocalDate date, List<LocalDate> dateList);

    public void setPointList(List<String> pointList) {

        List<String> formatList = new ArrayList<>();
        for (int i = 0; i < pointList.size(); i++) {
            String format = new LocalDate(pointList.get(i)).toString("yyyy-MM-dd");
            formatList.add(format);
        }

        this.pointList = formatList;
        CalendarView calendarView = calendarAdapter.getCalendarViews().get(getCurrentItem());
        if (calendarView == null) {
            return;
        }
        calendarView.setPointList(formatList);
    }
}
