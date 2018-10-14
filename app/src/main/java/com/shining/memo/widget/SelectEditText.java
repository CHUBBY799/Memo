package com.shining.memo.widget;

import android.content.Context;
import android.util.AttributeSet;


public class SelectEditText extends android.support.v7.widget.AppCompatEditText {

    private EditTextSelectChange editTextSelectChange;

    public void setEditTextSelectChange(EditTextSelectChange editTextSelectChange) {
        this.editTextSelectChange = editTextSelectChange;
    }

    public SelectEditText(Context context) {
        super(context);
    }

    public SelectEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (this.editTextSelectChange != null) {
            editTextSelectChange.change(selStart,selEnd);
        }
    }

    /**
     * 编辑框光标改变监听接口
     */
    public interface EditTextSelectChange {
        void change(int start, int end);
    }
}

