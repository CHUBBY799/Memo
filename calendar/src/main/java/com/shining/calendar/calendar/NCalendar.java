package com.shining.calendar.calendar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.shining.calendar.listener.OnCalendarChangedListener;
import com.shining.calendar.listener.OnMonthCalendarChangedListener;
import com.shining.calendar.listener.OnWeekCalendarChangedListener;
import com.shining.calendar.utils.Attrs;
import com.shining.calendar.view.MonthView;

import org.joda.time.LocalDate;

import java.util.List;

public class NCalendar extends FrameLayout implements NestedScrollingParent, ValueAnimator.AnimatorUpdateListener, OnWeekCalendarChangedListener, OnMonthCalendarChangedListener {

    protected WeekCalendar weekCalendar;
    protected MonthCalendar monthCalendar;
    protected View childView;//NCalendar内部包含的直接子view，直接子view并不一定是NestScrollChild
    protected View targetView;//嵌套滑动的目标view，即RecyclerView等
    public static final int MONTH = 100;
    public static final int WEEK = 200;
    protected int STATE = 100;//默认月
    protected int weekHeight;//周日历的高度
    protected int monthHeight;//月日历的高度,是日历整个的高度，并非是月日历绘制区域的高度

    protected int monthCalendarTop; //月日历的getTop
    protected int childViewTop; // childView的getTop

    protected int duration;//动画时间
    protected int monthCalendarOffset;//月日历需要滑动的距离
    protected ValueAnimator monthValueAnimator;//月日历动画
    protected ValueAnimator childViewValueAnimator;//childView动画

    protected Rect monthRect;//月日历大小的矩形
    protected Rect weekRect;//周日历大小的矩形 ，用于判断点击事件是否在日历的范围内

    protected OnCalendarChangedListener onCalendarChangedListener;



    public NCalendar(Context context) {
        this(context, null);
    }

    public NCalendar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NCalendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //禁止多点触摸
        setMotionEventSplittingEnabled(false);

        monthCalendar = new MonthCalendar(context, attrs);
        weekCalendar = new WeekCalendar(context, attrs);

        duration = Attrs.duration;
        monthHeight = Attrs.monthCalendarHeight;
        STATE = Attrs.defaultCalendar;

        weekHeight = monthHeight / 5;
        monthCalendar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, monthHeight));
        weekCalendar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, weekHeight));

        addView(monthCalendar);
        addView(weekCalendar);

        monthCalendar.setOnMonthCalendarChangedListener(this);
        weekCalendar.setOnWeekCalendarChangedListener(this);

        post(new Runnable() {
            @Override
            public void run() {
                weekCalendar.setVisibility(STATE == MONTH ? INVISIBLE : VISIBLE);

                monthRect = new Rect(0, monthCalendar.getTop(), monthCalendar.getWidth(), monthCalendar.getHeight());
                weekRect = new Rect(0, weekCalendar.getTop(), weekCalendar.getWidth(), weekCalendar.getHeight());


            }
        });

        monthValueAnimator = new ValueAnimator();
        childViewValueAnimator = new ValueAnimator();

        monthValueAnimator.addUpdateListener(this);
        childViewValueAnimator.addUpdateListener(this);
        childViewValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int top = childView.getTop();
                if (top == monthHeight) {
                    STATE = MONTH;
                    weekCalendar.setVisibility(INVISIBLE);
                } else {
                    STATE = WEEK;
                    weekCalendar.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        //跟随手势滑动
        move(dy, true, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        //嵌套滑动结束，自动滑动
        scroll();
    }

    /**
     * 手势滑动的主要逻辑
     *
     * @param dy       y方向上的偏移量
     * @param isNest   是否是NestedScrollingChild的滑动
     *
     */
    @SuppressWarnings("deprecation")
    private void move(int dy, boolean isNest, int[] consumed) {

        monthCalendarTop = monthCalendar.getTop();
        childViewTop = childView.getTop();

        //4种情况
        if (dy > 0 && Math.abs(monthCalendarTop) < monthCalendarOffset) {
            //月日历和childView同时上滑
            int offset = getOffset(dy, monthCalendarOffset - Math.abs(monthCalendarTop));
            monthCalendar.offsetTopAndBottom(-offset);
            childView.offsetTopAndBottom(-offset);
            if (isNest) consumed[1] = dy;
        } else if (dy > 0 && childViewTop > weekHeight) {
            //月日历滑动到位置后，childView继续上滑，覆盖一部分月日历
            int offset = getOffset(dy, childViewTop - weekHeight);
            childView.offsetTopAndBottom(-offset);
            if (isNest) consumed[1] = dy;
        } else if (dy < 0 && monthCalendarTop != 0 && !ViewCompat.canScrollVertically(targetView, -1)) {
            //月日历和childView下滑
            int offset = getOffset(Math.abs(dy), Math.abs(monthCalendarTop));
            monthCalendar.offsetTopAndBottom(offset);
            childView.offsetTopAndBottom(offset);
            if (isNest) consumed[1] = dy;
        } else if (dy < 0 && monthCalendarTop == 0 && childViewTop != monthHeight && !ViewCompat.canScrollVertically(targetView, -1)) {
            //月日历滑动到位置后，childView继续下滑
            int offset = getOffset(Math.abs(dy), monthHeight - childViewTop);
            childView.offsetTopAndBottom(offset);
            if (isNest) consumed[1] = dy;
        }

        //childView滑动到周位置后，标记状态，同时周日显示
        if (childViewTop == weekHeight) {
            STATE = WEEK;
            weekCalendar.setVisibility(VISIBLE);
        }

        //周状态，下滑显示月日历，把周日历隐掉
        if (STATE == WEEK && dy < 0 && !ViewCompat.canScrollVertically(targetView, -1)) {
            weekCalendar.setVisibility(INVISIBLE);
        }

        //彻底滑到月日历，标记状态
        if (childViewTop == monthHeight) {
            STATE = MONTH;
        }
    }

    /**
     * 自动滑动的主要逻辑
     */
    private void scroll() {
        //停止滑动的时候，距顶部的距离
        monthCalendarTop = monthCalendar.getTop();
        childViewTop = childView.getTop();

        if (monthCalendarTop == 0 && childViewTop == monthHeight) {
            return;
        }
        if (monthCalendarTop == -monthCalendarOffset && childViewTop == weekHeight) {
            return;
        }

        if (STATE == MONTH) {
            if (monthHeight - childViewTop < weekHeight) {
                autoScroll(monthCalendarTop, 0, childViewTop, monthHeight);
            } else {
                autoScroll(monthCalendarTop, -monthCalendarOffset, childViewTop, weekHeight);
            }

        } else {
            if (childViewTop < weekHeight * 2) {
                autoScroll(monthCalendarTop, -monthCalendarOffset, childViewTop, weekHeight);
            } else {
                autoScroll(monthCalendarTop, 0, childViewTop, monthHeight);
            }
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        //防止快速滑动
        childViewTop = childView.getTop();
        return childViewTop > weekHeight;
    }

    @Override
    public int getNestedScrollAxes() {
        return 0;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams layoutParams = childView.getLayoutParams();
        layoutParams.height = getMeasuredHeight() - weekHeight;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //总共有三个view，0,1,2
        childView = getChildAt(2);
        if (childView instanceof NestedScrollingChild) {
            targetView = childView;
        } else {
            targetView = getNestedScrollingChild(childView);
        }
        if (targetView == null) {
            throw new RuntimeException("NCalendar中的子类中必须要有NestedScrollingChild的实现类！");
        }
    }


    /**
     * 得到NestedScrollingChild的实现类
     *
     */
    private View getNestedScrollingChild(View view) {
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = ((ViewGroup) view).getChildAt(i);
                if (childAt instanceof NestedScrollingChild) {
                    return childAt;
                } else {
                    getNestedScrollingChild(((ViewGroup) view).getChildAt(i));
                }
            }
        }
        return null;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (STATE == MONTH) {
            monthCalendarTop = monthCalendar.getTop();
            childViewTop = childView.getTop() == 0 ? monthHeight : childView.getTop();
        } else {
            monthCalendarTop = -getMonthCalendarOffset();
            childViewTop = childView.getTop() == 0 ? weekHeight : childView.getTop();
        }

        monthCalendar.layout(0, monthCalendarTop, r, monthHeight + monthCalendarTop);
        ViewGroup.LayoutParams layoutParams = childView.getLayoutParams();
        childView.layout(0, childViewTop, r, layoutParams.height + childViewTop);

        weekCalendar.layout(0, 0, r, weekHeight);

    }


    //月日历需要滑动的距离，
    private int getMonthCalendarOffset() {
        MonthView currentMonthView = monthCalendar.getCurrentMonthView();
        //该月有几行
        int rowNum = currentMonthView.getRowNum();
        //现在选中的是第几行
        int selectRowIndex = currentMonthView.getSelectRowIndex();
        //month需要移动selectRowIndex*h/rowNum ,计算时依每个行的中点计算
        return selectRowIndex * currentMonthView.getMonthHeight() / rowNum;
    }

    public MonthCalendar getMonthCalendar() {
        return monthCalendar;
    }

    //自动滑动
    protected void autoScroll(int startMonth, int endMonth, int startChild, int endChild) {
        monthValueAnimator.setIntValues(startMonth, endMonth);
        monthValueAnimator.setDuration(duration);
        monthValueAnimator.start();

        childViewValueAnimator.setIntValues(startChild, endChild);
        childViewValueAnimator.setDuration(duration);
        childViewValueAnimator.start();
    }

    public void setOnCalendarChangedListener(OnCalendarChangedListener onCalendarChangedListener) {
        this.onCalendarChangedListener = onCalendarChangedListener;
    }


    /**
     * 防止滑动过快越界
     *
     */
    protected int getOffset(int offset, int maxOffset) {
        if (offset > maxOffset) {
            return maxOffset;
        }
        return offset;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (animation == monthValueAnimator) {
            int animatedValue = (int) animation.getAnimatedValue();
            int top = monthCalendar.getTop();
            int i = animatedValue - top;
            monthCalendar.offsetTopAndBottom(i);
        } else {
            int animatedValue = (int) animation.getAnimatedValue();
            int top = childView.getTop();
            int i = animatedValue - top;
            childView.offsetTopAndBottom(i);
        }
    }

    @Override
    public void onWeekCalendarChanged(LocalDate date,  List<LocalDate> dateList) {
        if (STATE == WEEK) {
            monthCalendar.setDate(date, dateList);
            requestLayout();
            if (onCalendarChangedListener != null) {
                onCalendarChangedListener.onCalendarChanged(date ,dateList);
            }
        }
    }

    @Override
    public void onMonthCalendarChanged(LocalDate date, List<LocalDate> dateList) {
        //monthCalendarOffset在这里赋值，月日历改变的时候
        monthCalendarOffset = getMonthCalendarOffset();

        if (STATE == MONTH) {
            weekCalendar.setDate(LocalDate.now(), dateList);
            if (onCalendarChangedListener != null) {
                onCalendarChangedListener.onCalendarChanged(date, dateList);
            }
        }
    }


    private int downY;
    private int downX;
    private int lastY;//上次的y
    private int verticalY = 50;//竖直方向上滑动的临界值，大于这个值认为是竖直滑动
    private boolean isFirstScroll = true; //第一次手势滑动，因为第一次滑动的偏移量大于verticalY，会出现猛的一划，这里只对第一次滑动做处理

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                downX = (int) ev.getX();
                lastY = downY;
                break;
            case MotionEvent.ACTION_MOVE:
                int y = (int) ev.getY();
                int absY = Math.abs(downY - y);
                boolean inCalendar = isInCalendar(downX, downY);
                if (absY > verticalY && inCalendar) {
                    //onInterceptTouchEvent返回true，触摸事件交给当前的onTouchEvent处理
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    @SuppressWarnings("all")
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                int y = (int) event.getY();
                int dy = lastY - y;

                if (isFirstScroll) {
                    // 防止第一次的偏移量过大
                    if (dy > verticalY) {
                        dy = dy - verticalY;
                    } else if (dy < -verticalY) {
                        dy = dy + verticalY;
                    }
                    isFirstScroll = false;
                }

                // 跟随手势滑动
                move(dy, false, null);

                lastY = y;

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isFirstScroll = true;
                scroll();
                break;
        }
        return true;
    }


    /**
     * 点击事件是否在日历的范围内
     *
     */
    private boolean isInCalendar(int x, int y) {
        if (STATE == MONTH) {
            return monthRect.contains(x, y);
        } else {
            return weekRect.contains(x, y);
        }
    }

    /**
     * 设置指示圆点
     *
     */
    public void setPoint(List<String> pointList) {
        monthCalendar.setPointList(pointList);
        weekCalendar.setPointList(pointList);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try{
            super.onRestoreInstanceState(state);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
