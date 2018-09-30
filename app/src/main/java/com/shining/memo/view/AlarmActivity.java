package com.shining.memo.view;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.utils.Utils;
import com.shining.memo.widget.DatePickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener{

    private Button alarm_cancel;
    private Button alarm_save;
    private DatePickerView month_pv;
    private DatePickerView day_pv;
    private DatePickerView hour_pv;
    private DatePickerView minute_pv;
    private Switch ringtoneSwitch;
    private Switch popSwitch;
    private TextView ringtoneReminder;
    private TextView popReminder;

    //需要保存和传递的参数
    private String year;
    private String month;
    private String day;
    private String hour;
    private String minute;
    private boolean ringtone;
    private boolean pop;

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
                alarmCancel();
                break;
            case R.id.alarm_save:
                alarmSave();
                break;
            default:
                break;
        }

    }

    private void alarmCancel(){
        Intent textIntent = new Intent();
        setResult(RESULT_CANCELED, textIntent);
        finish();
    }

    private void alarmSave(){
        Intent textIntent = new Intent();
        textIntent.putExtra("year", year);
        textIntent.putExtra("month", month);
        textIntent.putExtra("day", day);
        textIntent.putExtra("hour", hour);
        textIntent.putExtra("minute", minute);
        textIntent.putExtra("ringtone", ringtone);
        textIntent.putExtra("pop", pop);
        setResult(RESULT_OK, textIntent);
        finish();
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
        String[] monthArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        Collections.addAll(monthList,monthArray);
    }

    private void initDay(){
        dayList.clear();
        int intYear = Integer.parseInt(year);
        for (int i = 0 ; i < 28 ; i++){
            dayList.add(formatTimeUnit(i+1) + "th");
        }
        if (month.equals("Jan") || month.equals("Mar") || month.equals("May") || month.equals("Jul") || month.equals("Aug") || month.equals("Oct") || month.equals("Dec")) {
            dayList.add(29 + "th");
            dayList.add(30 + "th");
            dayList.add(31 + "th");
        }
        else if (month.equals("Apr") || month.equals("Jun") || month.equals("Sep") || month.equals("Nov")) {
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
        ringtoneSwitch = findViewById(R.id.ringtone_switch);
        popSwitch = findViewById(R.id.pop_switch);
        ringtoneReminder = findViewById(R.id.ringtone_reminder);
        popReminder = findViewById(R.id.pop_reminder);
    }


    private void initComponent(){
        alarm_cancel.setOnClickListener(this);
        alarm_save.setOnClickListener(this);
        ringtoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ringtone = true;
                    ringtoneReminder.setTextColor(ContextCompat.getColor(AlarmActivity.this, R.color.ringtone_reminder));
                }else{
                    ringtone = false;
                    ringtoneReminder.setTextColor(ContextCompat.getColor(AlarmActivity.this, R.color.pop_reminder));
                }
                changeColor();
            }
        });
        popSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    pop = true;
                    popReminder.setTextColor(ContextCompat.getColor(AlarmActivity.this, R.color.ringtone_reminder));
                }else{
                    pop = false;
                    popReminder.setTextColor(ContextCompat.getColor(AlarmActivity.this, R.color.pop_reminder));
                }
                changeColor();
            }
        });
    }

    private void changeColor(){
    }

    private void initParameter(){
        Calendar calendar = Calendar.getInstance();
        year = formatTimeUnit(calendar.get(Calendar.YEAR));
        month = Utils.formatMonthSimUS(calendar.get(Calendar.MONTH)+1);
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
