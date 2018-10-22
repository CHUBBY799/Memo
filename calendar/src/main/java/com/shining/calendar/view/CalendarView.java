package com.shining.calendar.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.shining.calendar.utils.Attrs;

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

    protected int mCurrentColor;//当月的字体颜色
    protected int mHintColor;//不是当月的字体颜色
    protected int mTodayColor;//当天矩形的颜色
    protected int mSelectColor;//选中矩形的颜色

    protected float mTextSize;//字体大小
    protected float mPointSize;//圆点大小
    protected Paint mPaint;

    protected List<Rect> mRectList;//点击用的矩形集合
    protected List<String> pointList;

    public CalendarView(Context context) {
        super(context);
        mCurrentColor = Attrs.currentColor;
        mHintColor = Attrs.hintColor;
        mTodayColor = Attrs.todayColor;
        mSelectColor = Attrs.selectColor;

        mTextSize = Attrs.textSize;
        mPointSize = Attrs.pointSize;

        mRectList = new ArrayList<>();
        mPaint = getPaint(mCurrentColor, mTextSize);
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
