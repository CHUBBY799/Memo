package com.shining.memo.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shining.memo.R;
import com.shining.memo.widget.DatePickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener{

    private Button alarm_cancel;
    private Button alarm_save;
    private DatePickerView month_pv;
    private DatePickerView day_pv;
    private DatePickerView hour_pv;
    private DatePickerView minute_pv;

    private String year;
    private String month;
    private String day;
    private String hour;
    private String minute;

    private List<String> monthList;
    private List<String> dayList;
    private List<String> hourList;
    private List<String> minuteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        iniView();
        initComponent();
        initParameter();
        initTimer();
        addListener();
        setSelectedTime();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.alarm_cancel:
                Intent textIntent = new Intent(this, TextActivity.class);
                startActivity(textIntent);
                break;
            case R.id.alarm_save:

                break;
            default:
                break;
        }

    }

    public void setSelectedTime() {
        month_pv.setSelected(month);
        day_pv.setSelected(day + "th");
        hour_pv.setSelected(hour);
        minute_pv.setSelected(minute);
    }

    private void initTimer() {
        initArrayList();
        initMonth();
        initDay();
        initHour();
        initMinute();
        loadComponent();
    }

    private void initMonth(){
        for(int i = 0 ; i < 12 ; i++){
            monthList.add(formatTimeUnit(i+1));
        }
    }

    private void initDay(){
        dayList.clear();
        int intYear = Integer.parseInt(year);
        int intMonth = Integer.parseInt(month);
        for (int i = 0 ; i < 28 ; i++){
            dayList.add(formatTimeUnit(i+1) + "th");
        }
        if (intMonth == 1 || intMonth == 3 || intMonth == 5 || intMonth == 7 || intMonth == 8 || intMonth == 10 || intMonth == 12) {
            dayList.add(29 + "th");
            dayList.add(30 + "th");
            dayList.add(31 + "th");
        }
        else if (intMonth == 4 || intMonth == 6 || intMonth == 9 || intMonth == 11) {
            dayList.add(29 + "th");
            dayList.add(30 + "th");
        }
        else if ((intYear % 4 == 0 && intYear % 100 != 0) || intYear % 400 == 0) {
                dayList.add(29 + "th");
        }
    }

    private void initHour(){
        for(int i = 0 ; i < 24 ; i++){
            hourList.add(formatTimeUnit(i));
        }
    }

    private void initMinute(){
        for(int i = 0 ; i < 60 ; i++){
            minuteList.add(formatTimeUnit(i));
        }
    }

    private void initArrayList() {
        if (monthList == null) monthList = new ArrayList<>();
        if (dayList == null) dayList = new ArrayList<>();
        if (hourList == null) hourList= new ArrayList<>();
        if (minuteList == null) minuteList = new ArrayList<>();
        monthList.clear();
        dayList.clear();
        hourList.clear();
        minuteList.clear();
    }

    private void loadComponent() {
        month_pv.setData(monthList);
        day_pv.setData(dayList);
        hour_pv.setData(hourList);
        minute_pv.setData(minuteList);
    }

    private void iniView(){
        alarm_cancel = findViewById(R.id.alarm_cancel);
        alarm_save = findViewById(R.id.alarm_save);
        month_pv = findViewById(R.id.month_pv);
        day_pv = findViewById(R.id.day_pv);
        hour_pv = findViewById(R.id.hour_pv);
        minute_pv = findViewById(R.id.minute_pv);
    }

    private void initComponent(){
        alarm_cancel.setOnClickListener(this);
        alarm_save.setOnClickListener(this);
    }

    private void initParameter(){
        Calendar calendar = Calendar.getInstance();
        year = formatTimeUnit(calendar.get(Calendar.YEAR));
        month = formatTimeUnit(calendar.get(Calendar.MONTH)+1);
        day = formatTimeUnit(calendar.get(Calendar.DAY_OF_MONTH));
        hour = formatTimeUnit(calendar.get(Calendar.HOUR_OF_DAY));
        minute = formatTimeUnit(calendar.get(Calendar.MINUTE));
    }

    private String formatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    private void addListener() {
        month_pv.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                month = text;
                initDay();
                day_pv.setSelected(0);
            }
        });

        day_pv.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                day = text;
            }
        });

        hour_pv.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                hour = text;
            }
        });

        minute_pv.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                minute = text;
            }
        });
    }

}
