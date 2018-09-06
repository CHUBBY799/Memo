package com.shining.memo.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shining.memo.R;
import com.shining.memo.presenter.TextPresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class TextActivity extends AppCompatActivity implements View.OnClickListener,TextView{

    private Button textCancel;
    private Button textUrgent;
    private Button textClock;
    private Button textEdit;
    private Button textConfirm;
    private EditText editTitle;
    private EditText editContent;

    private String title;
    private String content;
    private String color;
    private int urgent;
    //private int alarm;
    //private int deleted;
    private String date;
    private String time;

    private TextPresenter textPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        initView();
        initListener();

        textPresenter = new TextPresenter(this);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        if(title != null){
            textPresenter.requestTextInfo(title);
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.text_cancel:
                clickCancel();
                break;
            case R.id.text_urgent:
                clickUrgent();
                break;
            case R.id.text_alarm:
                clickAlarm();
                break;
            case R.id.text_edit:
                clickEdit();
                break;
            case R.id.text_confirm:
                clickConfirm();
                break;
            default:
                break;
        }
    }

    private void clickCancel(){
        Intent cancelIntent = new Intent(this,MainActivity.class);
        startActivity(cancelIntent);
    }

    private void clickUrgent(){
        if (urgent == 0){
            urgent = 1;
            textUrgent.setActivated(true);
        }else {
            urgent = 0;
            textUrgent.setActivated(false);
        }
    }

    private void clickAlarm(){
        Intent alarmIntent = new Intent(this, AlarmActivity.class);
        startActivity(alarmIntent);
    }

    private void clickEdit(){

    }

    private void clickConfirm(){
        textPresenter.responseTextInfo();
    }

    @Override
    public JSONObject onInfoSave(){
        JSONObject textInfo = new JSONObject();
        getCalendar();
        String type = "text";
        title = editTitle.getText().toString();
        content = editContent.getText().toString();
        color = "#" + Integer.toHexString(editContent.getCurrentTextColor()).substring(2);
        try {
            textInfo.put("type", type);
            textInfo.put("title", title);
            textInfo.put("content", content);
            textInfo.put("color", color);
            textInfo.put("urgent", urgent);
            textInfo.put("date", date);
            textInfo.put("time", time);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return textInfo;
    }

    @Override
    public void onInfoUpdate(JSONObject textInfo){
        try {
            title = textInfo.getString("title");
            content = textInfo.getString("content");
            color = textInfo.getString("color");
            urgent = textInfo.getInt("urgent");
            date = textInfo.getString("date");
            time = textInfo.getString("time");
        }catch (JSONException e){
            e.printStackTrace();
        }
        editTitle.setText(title);
        editContent.setText(content);
        editTitle.setTextColor(Color.parseColor(color));
        editContent.setTextColor(Color.parseColor(color));
    }

    private void getCalendar(){
        Calendar calendar = Calendar.getInstance();
        String year = singleToTen(calendar.get(Calendar.YEAR));
        String month = singleToTen(calendar.get(Calendar.MONTH)+1);
        String day = singleToTen(calendar.get(Calendar.DAY_OF_MONTH));
        String hour = singleToTen(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = singleToTen(calendar.get(Calendar.MINUTE));
        String second = singleToTen(calendar.get(Calendar.SECOND));
        date = year+"年"+month+"月"+day+"日";
        time = hour+":"+minute+":"+second;
    }

    private String singleToTen(int data){
        if (data < 10){
            return "0" + data;
        }
        return String.valueOf(data);
    }

    @Override
    public Context getContext(){
        return this;
    }

    private void initView(){
        textCancel = findViewById(R.id.text_cancel);
        textUrgent = findViewById(R.id.text_urgent);
        textClock = findViewById(R.id.text_alarm);
        textEdit = findViewById(R.id.text_edit);
        textConfirm = findViewById(R.id.text_confirm);
        editTitle = findViewById(R.id.edit_title);
        editContent = findViewById(R.id.edit_content);
    }

    private void initListener(){
        textCancel.setOnClickListener(this);
        textUrgent.setOnClickListener(this);
        textClock.setOnClickListener(this);
        textEdit.setOnClickListener(this);
        textConfirm.setOnClickListener(this);
    }
}