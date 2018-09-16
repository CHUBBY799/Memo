package com.shining.memo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.shining.memo.R;

public class RoundProgressBar extends ProgressBar {

    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final int DEFAULT_TEXT_COLOR = 0XFFC4C4C4;
    private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;
    private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 4;
    private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;

    /**
     * painter of all drawing things
     */
    protected Paint mPaint = new Paint();
    /**
     * color of progress number
     */
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    /**
     * size of text (sp)
     */
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    /**
     * offset of draw progress
     */
    protected int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);
    /**
     * height of reached progress bar
     */
    protected int mReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);
    /**
     * color of reached bar
     */
    protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
    /**
     * color of unreached bar
     */
    protected int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
    /**
     * height of unreached progress bar
     */
    protected int mUnReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
    /**
     * view width except padding
     */
    protected int mRealWidth;
    protected boolean mIfDrawText = true;
    protected static final int VISIBLE = 0;
    private int mRadius = dp2px(30);
    private int mMaxPaintWidth;

    public RoundProgressBar(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        obtainStyledAttributes(attrs);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);

        mReachedProgressBarHeight = (int) (mUnReachedProgressBarHeight * 2.5f);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        mRadius = (int) ta.getDimension(R.styleable.RoundProgressBar_radius, mRadius);
        ta.recycle();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        mMaxPaintWidth = Math.max(mReachedProgressBarHeight, mUnReachedProgressBarHeight);
        int expect = mRadius * 2 + mMaxPaintWidth + getPaddingLeft() + getPaddingRight();
        int width = resolveSize(expect, widthMeasureSpec);
        int height = resolveSize(expect, heightMeasureSpec);
        int realWidth = Math.min(width, height);
        mRadius = (realWidth - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2;
        setMeasuredDimension(realWidth, realWidth);
    }

    /**
     * get the styled attributes
     *
     * @param attrs
     */
    private void obtainStyledAttributes(AttributeSet attrs)
    {
        // init values from custom attributes
        final TypedArray attributes = getContext().obtainStyledAttributes(
                attrs, R.styleable.RoundProgressBar);
        mTextColor = attributes
                .getColor(
                        R.styleable.RoundProgressBar_progress_text_color,
                        DEFAULT_TEXT_COLOR);
        mTextSize = (int) attributes.getDimension(
                R.styleable.RoundProgressBar_progress_text_size,
                mTextSize);
        mReachedBarColor = attributes
                .getColor(
                        R.styleable.RoundProgressBar_progress_reached_color,
                        mTextColor);
        mUnReachedBarColor = attributes
                .getColor(
                        R.styleable.RoundProgressBar_progress_unreached_color,
                        DEFAULT_COLOR_UNREACHED_COLOR);
        mReachedProgressBarHeight = (int) attributes
                .getDimension(
                        R.styleable.RoundProgressBar_progress_reached_bar_height,
                        mReachedProgressBarHeight);
        mUnReachedProgressBarHeight = (int) attributes
                .getDimension(
                        R.styleable.RoundProgressBar_progress_unreached_bar_height,
                        mUnReachedProgressBarHeight);
        mTextOffset = (int) attributes
                .getDimension(
                        R.styleable.RoundProgressBar_progress_text_offset,
                        mTextOffset);
        int textVisible = attributes
                .getInt(R.styleable.RoundProgressBar_progress_text_visibility,
                        VISIBLE);
        if (textVisible != VISIBLE)
        {
            mIfDrawText = false;
        }
        attributes.recycle();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas)
    {
        canvas.save();
        canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2, getPaddingTop()
                + mMaxPaintWidth / 2);
        mPaint.setStyle(Paint.Style.STROKE);
        // draw unreaded bar
        mPaint.setColor(mUnReachedBarColor);
        mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        // draw reached bar
        mPaint.setColor(mReachedBarColor);
        mPaint.setStrokeWidth(mReachedProgressBarHeight);
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), -90,
                sweepAngle, false, mPaint);
        // draw text
        mPaint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }


    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }
    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());
    }

}
