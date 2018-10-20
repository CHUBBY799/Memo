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

@SuppressLint("ViewConstructor")
public class WeekView extends CalendarView {

    private Context context;
    private OnClickWeekViewListener mOnClickWeekViewListener;

    public WeekView(Context context, LocalDate date, OnClickWeekViewListener onClickWeekViewListener) {
        super(context);

        this.context = context;
        this.mInitialDate = date;
        Utils.NCalendar weekCalendar = Utils.getWeekCalendar(date, Attrs.firstDayOfWeek);
        dates = weekCalendar.dateList;
        mOnClickWeekViewListener = onClickWeekViewListener;
    }

    @SuppressWarnings("all")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        mRectList.clear();
        Rect rect = null;
        for (int i = 0; i < 7; i++) {
            if (i == 0){
                rect = new Rect(0, 0, i * mWidth / 7 + mWidth / 7, mHeight);
            }else {
                rect = new Rect(mRectList.get(i-1).right, 0, i * mWidth / 7 + mWidth / 7, mHeight);
            }
            mRectList.add(rect);

            //得到当前矩形日期
            LocalDate date = dates.get(i);
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();

            //绘制基线
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;

            if (mSelectDateList != null && mSelectDateList.contains(date)) {
                mPaint.setColor(mSelectColor);
                canvas.drawRect(rect, mPaint);
                mPaint.setColor(Color.WHITE);
                canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mPaint);
                drawPoint(canvas, rect, date, baseline);
            }else if (Utils.isToday(date)) {
                mPaint.setColor(mTodayColor);
                canvas.drawRect(rect, mPaint);
                mPaint.setColor(Color.WHITE);
                canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mPaint);
                drawPoint(canvas, rect, date, baseline);
            }else {
                mPaint.setColor(mCurrentColor);
                canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mPaint);
                mPaint.setColor(mSelectColor);
                drawPoint(canvas, rect, date, baseline);
            }
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.parseColor("#e2e2e2"));
        canvas.drawLine(0, rect.bottom, rect.right, rect.bottom, mPaint);
    }

    public void drawPoint(Canvas canvas, Rect rect, LocalDate date, int baseline) {
        if (pointList != null && pointList.contains(date.toString())) {
            canvas.drawCircle(rect.centerX(), baseline + Utils.dp2px(context,15), mPointSize, mPaint);
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
