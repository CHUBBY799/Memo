package com.shining.memo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shining.memo.R;

public class NoteEditText extends android.support.v7.widget.AppCompatEditText{

    private int color;

    public NoteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        //得到id对应的颜色值
        color = getResources().getColor(R.color.underline_background);
    }

    public NoteEditText(Context context) {
        super(context);
        color = getResources().getColor(R.color.underline_background);
    }

    public NoteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        color = getResources().getColor(R.color.underline_background);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int lineHeight =  this.getLineHeight();
        Paint mPaint = getPaint();
        mPaint.setColor(color);
        mPaint.setStrokeWidth(3);
        int y = lineHeight - 2;
        for(int i=1; i<getLineCount(); i++) {
            canvas.drawLine(0, y, getWidth(), y, mPaint);
            y+=lineHeight;
        }
        canvas.translate(0, 0);
        super.onDraw(canvas);
    }

    public void setImage() {
        String content = "%audio%";
        CustomImageSpan imageSpan = new CustomImageSpan(getContext(), R.drawable.share_btn_48x48px, 2);
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(imageSpan, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Editable edit_text = getEditableText();
        int index = getSelectionStart();
        //插入换行符，使图片单独占一行
        SpannableString newLine = new SpannableString("\n");
        edit_text.insert(index, newLine);
        // 将选择的图片追加到EditText中光标所在位置
        if (index < 0 || index >= edit_text.length()) {
            edit_text.append(spannableString);
        } else {
            edit_text.insert(index, spannableString);
        }
    }

    /**
     * 设置记事本的编辑框背景线条颜色
     * @param color int type【代表颜色的整数】
     */
    public void setBGColor(int color) {
        this.color = color;
        invalidate();
    }

    /**
     * 设置记事本的编辑框背景线条颜色
     * @param colorId int type【代表颜色的资源id】
     */
    public void setBGColorId(int colorId) {
        this.color = getResources().getColor(colorId);
        invalidate();
    }



}
