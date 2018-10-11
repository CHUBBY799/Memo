package com.shining.memo.model;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

public class FontType {
    private ForegroundColorSpan mColorSpan;
    private StyleSpan mStyleSpan;
    private UnderlineSpan mUnderlineSpan;
    private StrikethroughSpan mStrikethroughSpan;

    public ForegroundColorSpan getmColorSpan() {
        return mColorSpan;
    }

    public void setmColorSpan(ForegroundColorSpan mColorSpan) {
        this.mColorSpan = mColorSpan;
    }

    public StyleSpan getmStyleSpan() {
        return mStyleSpan;
    }

    public void setmStyleSpan(StyleSpan mStyleSpan) {
        this.mStyleSpan = mStyleSpan;
    }

    public UnderlineSpan getmUnderlineSpan() {
        return mUnderlineSpan;
    }

    public void setmUnderlineSpan(UnderlineSpan mUnderlineSpan) {
        this.mUnderlineSpan = mUnderlineSpan;
    }

    public StrikethroughSpan getmStrikethroughSpan() {
        return mStrikethroughSpan;
    }

    public void setmStrikethroughSpan(StrikethroughSpan mStrikethroughSpan) {
        this.mStrikethroughSpan = mStrikethroughSpan;
    }

    public void disconnectSpan(SpannableString spannableString){
        try{
            if(mColorSpan != null){
                spannableString.setSpan(mColorSpan,spannableString.getSpanStart(mColorSpan),
                        spannableString.getSpanEnd(mColorSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(mStyleSpan != null){
                spannableString.setSpan(mStyleSpan,spannableString.getSpanStart(mStyleSpan),
                        spannableString.getSpanEnd(mStyleSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(mStrikethroughSpan != null){
                spannableString.setSpan(mStrikethroughSpan,spannableString.getSpanStart(mStrikethroughSpan),
                        spannableString.getSpanEnd(mStrikethroughSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(mUnderlineSpan != null){
                spannableString.setSpan(mUnderlineSpan,spannableString.getSpanStart(mUnderlineSpan),
                        spannableString.getSpanEnd(mUnderlineSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
