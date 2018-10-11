package com.shining.memo.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.shining.memo.R;

public class MaskImageView extends android.support.v7.widget.AppCompatImageView {

    // 遮罩的范围
    public static final int MASK_LEVEL_BACKGROUND = 1; // 背景图显示遮罩
    public static final int MASK_LEVEL_FOREGROUND = 2; // 前景图显示遮罩
    private boolean mIsIgnoreAlpha = true; // 是否忽略图片的透明度，默认为true,透明部分不显示遮罩

    private boolean mIsShowMaskOnClick = true; // 点击时是否显示遮罩，默认开启
    private int mShadeColor = 0x00ffffff; // 遮罩颜色（argb,需要设置透明度）

    private int mMaskLevel = MASK_LEVEL_FOREGROUND; // 默认为前景图显示遮罩

    ColorMatrix mColorMatrix = new ColorMatrix(); // 颜色矩阵
    ColorFilter mColorFilter;


    public MaskImageView(Context context) {
        this(context, null);
    }

    public MaskImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.MaskImageView);

        mIsIgnoreAlpha = a.getBoolean(R.styleable.MaskImageView_is_ignore_alpha, mIsIgnoreAlpha);
        mIsShowMaskOnClick = a.getBoolean(R.styleable.MaskImageView_is_show_mask_on_click, mIsShowMaskOnClick);
        mShadeColor = a.getColor(R.styleable.MaskImageView_mask_color, mShadeColor);
        mMaskLevel = a.getInt(R.styleable.MaskImageView_mask_level, mMaskLevel);

        // 忽略透明度时的颜色矩阵
        float r = Color.alpha(mShadeColor) / 255f;
        r=r-(1 - r)*0.15f;
        float rr = (1 - r)*1.15f;
        setColorMatrix(new float[]{
                rr, 0, 0, 0, Color.red(mShadeColor) * r,
                0, rr, 0, 0, Color.green(mShadeColor) * r,
                0, 0, rr, 0, Color.blue(mShadeColor) * r,
                0, 0, 0, 1, 0,
        });

        a.recycle();
    }

    private void setColorMatrix(float[] matrix) {
        mColorMatrix.set(matrix);
        mColorFilter = new ColorMatrixColorFilter(mColorMatrix);
    }

    // all drawables instances loaded from  the same resource share a common state
    // 从同一个资源文件获取的drawable对象共享一个状态信息，为了避免修改其中一个drawable导致其他drawable被影响，需要调用mutate()
    // 因为背景图在draw()阶段绘制，所以修改了背景图状态后必须调用invalidateSelf（）刷新
    private void setDrawableColorFilter(ColorFilter colorFilter) {
        if (mMaskLevel == MASK_LEVEL_BACKGROUND) {
            if (getBackground() != null) {
                getBackground().mutate();
                getBackground().setColorFilter(colorFilter);
                getBackground().invalidateSelf();
            }
        } else if (mMaskLevel == MASK_LEVEL_FOREGROUND) {
            if (getDrawable() != null) {
                getDrawable().mutate();
                getDrawable().setColorFilter(colorFilter);
                getDrawable().invalidateSelf();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mIsIgnoreAlpha) { // 忽略透明度
            if (mIsShowMaskOnClick && isPressed()) {
                // 绘制遮罩层
                setDrawableColorFilter(mColorFilter);
            } else {
                setDrawableColorFilter(null);
            }
            super.onDraw(canvas);
        } else { // 不忽略透明度
            setDrawableColorFilter(null);
            if (mMaskLevel == MASK_LEVEL_BACKGROUND) { // 背景图
                if (mIsShowMaskOnClick && isPressed()) {
                    // 绘制遮罩层
                    canvas.drawColor(mShadeColor);
                }
                super.onDraw(canvas);
            } else { // 前景图
                super.onDraw(canvas);
                if (mIsShowMaskOnClick && isPressed()) {
                    // 绘制遮罩层
                    canvas.drawColor(mShadeColor);
                }
            }
        }

    }

    /**
     * view状态改变
     */
    @Override
    protected void drawableStateChanged(){
        super.drawableStateChanged();
        invalidate();
    }

    public boolean isIsIgnoreAlpha() {
        return mIsIgnoreAlpha;
    }

    public void setIsIgnoreAlpha(boolean mIsIgnoreAlpha) {
        this.mIsIgnoreAlpha = mIsIgnoreAlpha;
        invalidate();
    }

    public boolean isIsShowMaskOnClick() {
        return mIsShowMaskOnClick;
    }

    public void setIsShowMaskOnClick(boolean mIsShowMaskOnClick) {
        this.mIsShowMaskOnClick = mIsShowMaskOnClick;
        invalidate();
    }

    public int getShadeColor() {
        return mShadeColor;
    }

    public void setShadeColor(int mShadeColor) {
        this.mShadeColor = mShadeColor;
        // 忽略透明度时的颜色矩阵
        float r = Color.alpha(mShadeColor) / 255f;
        r=r-(1 - r)*0.15f;
        float rr = (1 - r)*1.15f;
        setColorMatrix(new float[]{
                rr, 0, 0, 0, Color.red(mShadeColor) * r,
                0, rr, 0, 0, Color.green(mShadeColor) * r,
                0, 0, rr, 0, Color.blue(mShadeColor) * r,
                0, 0, 0, 1, 0,
        });
        invalidate();
    }

    public int getMaskLevel() {
        return mMaskLevel;
    }

    public void setMaskLevel(int mMaskLevel) {
        this.mMaskLevel = mMaskLevel;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        Drawable d = getDrawable();

        if(d!=null){
            // ceil not round - avoid thin vertical gaps along the left/right edges
            int width = MeasureSpec.getSize(widthMeasureSpec);
            //高度根据使得图片的宽度充满屏幕计算而得
            int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
