package com.shining.memo.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.EditText;

import com.shining.memo.R;
import com.shining.memo.utils.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

public class TestView extends Activity {

    private MediaPlayer mediaPlayer;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_recording_text);
        EditText editText = (EditText)findViewById(R.id.item_editText);
        String s = "测试测试，再说一遍，这真的是测试文字！";
        BackgroundColorSpan bcspan = new BackgroundColorSpan(Color.parseColor("#FF00AA"));
        SpannableString spannable = new SpannableString(s);
        spannable.setSpan(bcspan,0,9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD),11,14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        String s1 = "测试测试，再说一遍，这真的是测试文字！";
        editText.setText(spannable);
        Editable editable = editText.getEditableText();
        editable.insert(2,"12131231");
        Spanned spannable2 = Html.fromHtml("<p dir=\"ltr\">呵<b>呵</b>呵！</p>",FROM_HTML_MODE_COMPACT);
        editText.setText(spannable2.subSequence(0,spannable2.length() - 1));
        String html = Html.toHtml(spannable2);
        ToastUtils.showLong(this,spannable2);
    }

}
