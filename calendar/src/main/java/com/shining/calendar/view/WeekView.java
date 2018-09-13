package com.shining.calendar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.shining.calendar.listener.OnClickWeekViewListener;
import com.shining.calendar.utils.Attrs;
import com.shining.calendar.utils.Utils;

import org.joda.time.LocalDate;

import java.util.List;

@SuppressLint("ViewConstructor")
public class WeekView extends CalendarView {


    private OnClickWeekViewListener mOnClickWeekViewListener;
    private List<String> lunarList;

    public WeekView(Context context, LocalDate date, OnClickWeekViewListener onClickWeekViewListener) {
        super(context);

        this.mInitialDate = date;
        Utils.NCalendar weekCalendar2 = Utils.getWeekCalendar(date, Attrs.firstDayOfWeek);

        dates = weekCalendar2.dateList;
        lunarList = weekCalendar2.lunarList;
        mOnClickWeekViewListener = onClickWeekViewListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        //mHeight = getHeight();
        //为了与月日历保持一致，往上压缩一下,5倍的关系
        mHeight = (int) (getHeight() - Utils.dp2px(getContext(), 2));
        mRectList.clear();

        for (int i = 0; i < 7; i++) {
            @SuppressWarnings("all")
            Rect rect = new Rect(i * mWidth / 7, 0, i * mWidth / 7 + mWidth / 7, mHeight);
            mRectList.add(rect);
            LocalDate date = dates.get(i);
            Paint.FontMetricsInt fontMetrics = mSolarPaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;

            if (Utils.isToday(date)) {
                mSolarPaint.setColor(mSelectCircleColor);
                canvas.drawCircle(rect.centerX(), rect.centerY(), mSelectCircleRadius, mSolarPaint);
                mSolarPaint.setColor(Color.WHITE);
                canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mSolarPaint);
            } else if (mSelectDate != null && date.equals(mSelectDate)) {
                mSolarPaint.setColor(mSelectCircleColor);
                canvas.drawCircle(rect.centerX(), rect.centerY(), mSelectCircleRadius, mSolarPaint);
                mSolarPaint.setColor(mHollowCircleColor);
                canvas.drawCircle(rect.centerX(), rect.centerY(), mSelectCircleRadius - mHollowCircleStroke, mSolarPaint);
                mSolarPaint.setColor(mSolarTextColor);
                canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mSolarPaint);
            } else {
                mSolarPaint.setColor(mSolarTextColor);
                canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mSolarPaint);
                //绘制农历
                drawLunar(canvas, rect, baseline,i);
                //绘制节假日
                drawHolidays(canvas, rect, date, baseline);
                //绘制圆点
                drawPoint(canvas, rect, date, baseline);

            }
        }
    }

    private void drawLunar(Canvas canvas, Rect rect, int baseline, int i) {
        if (isShowLunar) {
            mLunarPaint.setColor(mLunarTextColor);
            String lunar = lunarList.get(i);
            canvas.drawText(lunar, rect.centerX(), baseline + getHeight() / 4, mLunarPaint);
        }
    }


    private void drawHolidays(Canvas canvas, Rect rect, LocalDate date, int baseline) {
        if (isShowHoliday) {
            if (holidayList.contains(date.toString())) {
                mLunarPaint.setColor(mHolidayColor);
                canvas.drawText("休", rect.centerX() + rect.width() / 4, baseline - getHeight() / 4, mLunarPaint);

            } else if (workdayList.contains(date.toString())) {
                mLunarPaint.setColor(mWorkdayColor);
                canvas.drawText("班", rect.centerX() + rect.width() / 4, baseline - getHeight() / 4, mLunarPaint);
            }
        }
    }

    public void drawPoint(Canvas canvas, Rect rect, LocalDate date, int baseline) {
        if (pointList != null && pointList.contains(date.toString())) {
            mLunarPaint.setColor(mPointColor);
            canvas.drawCircle(rect.centerX(), baseline - getHeight() / 3, mPointSize, mLunarPaint);
        }
    }


    @Override
    @SuppressWarnings("all")
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (int i = 0; i < mRectList.size(); i++) {
                Rect rect = mRectList.get(i);
                if (rect.contains((int) e.getX(), (int) e.getY())) {
                    LocalDate selectDate = dates.get(i);
                    mOnClickWeekViewListener.onClickCurrentWeek(selectDate);
                    break;
                }
            }
            return true;
        }
    });


    public boolean contains(LocalDate date) {
        return dates.contains(date);
    }
}
