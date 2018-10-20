package com.shining.calendar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.shining.calendar.listener.OnClickMonthViewListener;
import com.shining.calendar.utils.Attrs;
import com.shining.calendar.utils.Utils;
import org.joda.time.LocalDate;

@SuppressLint("ViewConstructor")
public class MonthView extends CalendarView {

    private Context context;
    private int mRowNum;
    private OnClickMonthViewListener mOnClickMonthViewListener;

    public MonthView(Context context, LocalDate date, OnClickMonthViewListener onClickMonthViewListener) {
        super(context);

        this.context = context;
        this.mInitialDate = date;
        Utils.NCalendar nCalendar = Utils.getMonthCalendar(date, Attrs.firstDayOfWeek);
        mOnClickMonthViewListener = onClickMonthViewListener;
        dates = nCalendar.dateList;
        mRowNum = dates.size() / 7;
    }

    @SuppressWarnings("all")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getMonthHeight();
        mRectList.clear();

        Rect rect = null;
        //循环绘制每月的日期（7*5的矩形集合）
        for (int i = 0; i < mRowNum; i++) {
            for (int j = 0; j < 7; j++) {
                //定义每个区域的矩形
                if (i == 0 && j == 0){
                    rect = new Rect(0, 0, mWidth / 7, mHeight / mRowNum);
                }else if(j == 0){
                    rect = new Rect(0, mRectList.get(i * 7 - 1).bottom,  mWidth / 7, i * mHeight / mRowNum + mHeight / mRowNum);
                }else {
                    rect = new Rect(mRectList.get(i * 7 + j - 1).right, mRectList.get(i * 7 + j - 1).top,  j * mWidth / 7 + mWidth / 7, mRectList.get(i * 7 + j - 1).bottom);
                }
                mRectList.add(rect);

                //得到当前矩形的日期
                LocalDate date = dates.get(i * 7 + j);
                Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();

                //绘制基线
                int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;

                //当月和上下月的颜色不同
                if (Utils.isEqualsMonth(date, mInitialDate)) {
                    if (mSelectDateList != null && mSelectDateList.contains(date)) { //绘制选中的日期
                        mPaint.setColor(mSelectColor);
                        canvas.drawRect(rect, mPaint);
                        mPaint.setColor(Color.WHITE);
                        canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mPaint);
                        drawPoint(canvas, rect, date, baseline);
                    } else if (Utils.isToday(date)) {
                        mPaint.setColor(mTodayColor);
                        canvas.drawRect(rect, mPaint);
                        mPaint.setColor(Color.WHITE);
                        canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mPaint);
                        drawPoint(canvas, rect, date, baseline);
                    }
                    else {
                        mPaint.setColor(mCurrentColor);
                        canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mPaint);
                        mPaint.setColor(mSelectColor);
                        drawPoint(canvas, rect, date, baseline);
                    }

                } else {
                    mPaint.setColor(mHintColor);
                    canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mPaint);
                }
            }
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.parseColor("#DDDDDD"));
        canvas.drawLine(0, rect.bottom, rect.right, rect.bottom, mPaint);
    }

    /**
     * 月日历高度
     *
     */
    public int getMonthHeight() {
        return Attrs.monthCalendarHeight;
    }

    /**
     * 绘制圆点
     */
    public void drawPoint(Canvas canvas, Rect rect, LocalDate date, int baseline) {
        if (pointList != null && pointList.contains(date.toString())) {
            canvas.drawCircle(rect.centerX(), baseline + Utils.dp2px(context,15), mPointSize, mPaint);
        }
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
                    if (Utils.isLastMonth(selectDate, mInitialDate)) {
                        mOnClickMonthViewListener.onClickLastMonth(selectDate);
                    } else if (Utils.isNextMonth(selectDate, mInitialDate)) {
                        mOnClickMonthViewListener.onClickNextMonth(selectDate);
                    } else {
                        mOnClickMonthViewListener.onClickCurrentMonth(selectDate);
                    }
                    break;
                }
            }
            return true;
        }
    });

    @Override
    @SuppressWarnings("all")
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public int getRowNum() {
        return mRowNum;
    }

    public int getSelectRowIndex() {
        if (mSelectDateList.size() == 0){
            return dates.indexOf(LocalDate.now()) / 7;
        }else {
            return dates.indexOf(mSelectDateList.get(mSelectDateList.size() - 1)) / 7;
        }
    }
}
