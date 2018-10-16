package com.shining.calendar.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.shining.calendar.utils.Attrs;
import com.shining.calendar.utils.Utils;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public abstract class CalendarView extends View {

    protected LocalDate mSelectDate;//被选中的date
    protected List<LocalDate> mSelectDateList;
    protected LocalDate mInitialDate;//初始传入的date
    protected int mWidth;
    protected int mHeight;
    protected List<LocalDate> dates;


    protected int mSolarTextColor;//公历字体颜色
    protected int mLunarTextColor;//农历字体颜色
    protected int mHintColor;//不是当月的颜色
    protected float mSolarTextSize;
    protected float mLunarTextSize;
    protected Paint mSolarPaint;
    protected Paint mLunarPaint;
    protected float mSelectCircleRadius;//选中圆的半径
    protected int mToDayColor;
    protected int mSelectCircleColor;//选中圆的颜色
    protected boolean isShowLunar;//是否显示农历

    protected int mHolidayColor;
    protected int mWorkdayColor;

    protected List<Rect> mRectList;//点击用的矩形集合
    protected int mPointColor;//圆点颜色
    protected float mPointSize;//圆点大小

    protected int mHollowCircleColor;//空心圆颜色
    protected float mHollowCircleStroke;//空心圆粗细

    protected boolean isShowHoliday;//是否显示节假日
    protected List<String> holidayList;
    protected List<String> workdayList;
    protected List<String> pointList;

    public CalendarView(Context context) {
        super(context);
        mSolarTextColor = Attrs.solarTextColor;
        mLunarTextColor = Attrs.lunarTextColor;
        mHintColor = Attrs.hintColor;
        mSolarTextSize = Attrs.solarTextSize;
        mLunarTextSize = Attrs.lunarTextSize;
        mSelectCircleRadius = Attrs.selectCircleRadius;
        mToDayColor = Attrs.toDayColor;
        mSelectCircleColor = Attrs.selectCircleColor;
        isShowLunar = Attrs.isShowLunar;

        mPointSize = Attrs.pointSize;
        mPointColor = Attrs.pointColor;
        mHollowCircleColor = Attrs.hollowCircleColor;
        mHollowCircleStroke = Attrs.hollowCircleStroke;

        isShowHoliday = Attrs.isShowHoliday;
        mHolidayColor = Attrs.holidayColor;
        mWorkdayColor = Attrs.workdayColor;

        mRectList = new ArrayList<>();
        mSolarPaint = getPaint(mSolarTextColor, mSolarTextSize);
        mLunarPaint = getPaint(mLunarTextColor, mLunarTextSize);

        holidayList = Utils.getHolidayList(getContext());
        workdayList = Utils.getWorkdayList(getContext());
    }

    private Paint getPaint(int paintColor, float paintSize) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setTextSize(paintSize);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }

    public LocalDate getInitialDate() {
        return mInitialDate;
    }

    public void setDateAndPoint(LocalDate date, List<LocalDate> datesList, List<String> pointList) {
        this.mSelectDate = date;
        this.mSelectDateList = datesList;
        this.pointList = pointList;
        invalidate();
    }

    public void clear() {
        this.mSelectDate = null;
        invalidate();
    }

    public void setPointList(List<String> pointList) {
        this.pointList = pointList;
        invalidate();
    }

}
