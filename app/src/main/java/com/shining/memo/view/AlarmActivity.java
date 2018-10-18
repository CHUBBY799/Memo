package com.shining.memo.view;


import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.model.Alarm;
import com.shining.memo.presenter.AlarmPresenter;
import com.shining.memo.utils.ToastUtils;
import com.shining.memo.utils.Utils;
import com.shining.memo.widget.DatePickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton alarm_cancel;
    private Button alarm_save,alarm_delete;
    private DatePickerView month_pv;
    private DatePickerView day_pv;
    private DatePickerView hour_pv;
    private DatePickerView minute_pv;
    private Switch ringtoneSwitch;
    private Switch popSwitch;
    private TextView ringtoneReminder;
    private TextView popReminder;
    private AlarmPresenter alarmPresenter;

    //需要保存和传递的参数
    private String year;
    private String month;
    private String day;
    private String hour;
    private String minute;
    private int ringtone;
    private int pop = 1;
    private int alarm;
    private int taskId;

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
        initParameters();
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
            case R.id.alarm_delete:
                alarmDelete();
                break;
            default:
                break;
        }

    }

    public void initParameters(){
        alarm = getIntent().getIntExtra("alarm",0);
        taskId = getIntent().getIntExtra("taskId",-1);
        Calendar calendar = Calendar.getInstance();
        if(alarm == 1){
            alarm_save.setText(getResources().getText(R.string.alarm_update));
            alarm_delete.setBackground(getDrawable(R.drawable.alarm_button_delete));
            alarm_delete.setTextColor(getColor(R.color.alarm_btn_delete_border));
            alarm_delete.setEnabled(true);
            String date,time;
            if(taskId == -1){
                date = getIntent().getStringExtra("date");
                time = getIntent().getStringExtra("time");
                ringtone = getIntent().getIntExtra("ringtone",0);
                pop = getIntent().getIntExtra("pop",1);
            }else {
                Alarm alarmObject = alarmPresenter.getAlarm(taskId);
                date = alarmObject.getDate();
                time = alarmObject.getTime();
                pop = alarmObject.getPop();
                ringtone = alarmObject.getRingtone();
            }
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date dates = new Date();
            try {
                dates = sdf.parse(date+" "+time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(dates);
        }
        year = formatTimeUnit(calendar.get(Calendar.YEAR));
        month = Utils.formatMonthSimUS(calendar.get(Calendar.MONTH)+1);
        day = formatTimeUnit(calendar.get(Calendar.DAY_OF_MONTH));
        hour = formatTimeUnit(calendar.get(Calendar.HOUR_OF_DAY));
        minute = formatTimeUnit(calendar.get(Calendar.MINUTE));
    }

    private void alarmCancel(){
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void alarmDelete(){
        if(alarmPresenter.deleteAlarm(taskId)){
            ToastUtils.showSuccessShort(this,"Delete the Alarm successfully!");
            Intent intent = new Intent();
            intent.putExtra("alarm",0);
            setResult(RESULT_OK,intent);
        }else {
            setResult(RESULT_CANCELED);
        }
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private void alarmSave(){
        String date = year+"-"+Utils.formatTimeUnit(Utils.formatMonthNumber(month))+"-"+day;
        String time = hour+":"+minute;
        Intent textIntent = new Intent();
        textIntent.putExtra("alarm",1);
        Log.d("TAG", "alarmSave" + date+"--"+ time +"--"+ ringtone+"--"+ pop+"--");
        if(taskId != -1){
            Alarm alarmObject = new Alarm();
            alarmObject.setDate(date);
            alarmObject.setTime(time);
            alarmObject.setRingtone(ringtone);
            alarmObject.setPop(pop);
            alarmObject.setTaskId(taskId);
            if(alarm == 1){
                alarmPresenter.modifyAlarm(alarmObject);
                alarmPresenter.setAlarmNotice(taskId);
            }else {
                alarmPresenter.addAlarm(alarmObject);
                alarmPresenter.setAlarmNotice(taskId);
            }
        }
        textIntent.putExtra("date",date);
        textIntent.putExtra("time",time);
        textIntent.putExtra("ringtone", ringtone);
        textIntent.putExtra("pop", pop);
        setResult(RESULT_OK, textIntent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void setSelectedTime() {
        Log.d("TAG", "setSelectedTime: "+month);
        month_pv.setSelected(month);
        day_pv.setSelected(day + "th");
        hour_pv.setSelected(hour);
        minute_pv.setSelected(minute);
        if (ringtone == 1)
            ringtoneSwitch.setChecked(true);
        else
            ringtoneSwitch.setChecked(false);
        if (pop == 1)
            popSwitch.setChecked(true);
        else
            popSwitch.setChecked(false);
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
        String[] monthArray = {"Jan.", "Feb.", "Mar.", "Apr.", "May.", "Jun.", "Jul.", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};
        Collections.addAll(monthList,monthArray);
    }

    private void initDay(){
        dayList.clear();
        int intYear = Integer.parseInt(year);
        for (int i = 0 ; i < 28 ; i++){
            //if (i == 0){
                //dayList.add(formatTimeUnit(i+1) + "st");
            //}else if (i == 1){
                //dayList.add(formatTimeUnit(i+1) + "nd");
            //}else {
                dayList.add(formatTimeUnit(i+1) + "th");
            //}
        }
        if (month.equals("Jan.") || month.equals("Mar.") || month.equals("May.") || month.equals("Jul.") || month.equals("Aug.") || month.equals("Oct.") || month.equals("Dec.")) {
            dayList.add(29 + "th");
            dayList.add(30 + "th");
            dayList.add(31 + "th");
        }
        else if (month.equals("Apr.") || month.equals("Jun.") || month.equals("Sept.") || month.equals("Nov.")) {
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
        alarm_delete = findViewById(R.id.alarm_delete);
        month_pv = findViewById(R.id.month_pv);
        day_pv = findViewById(R.id.day_pv);
        hour_pv = findViewById(R.id.hour_pv);
        minute_pv = findViewById(R.id.minute_pv);
        ringtoneSwitch = findViewById(R.id.ringtone_switch);
        popSwitch = findViewById(R.id.pop_switch);
        ringtoneReminder = findViewById(R.id.ringtone_reminder);
        popReminder = findViewById(R.id.pop_reminder);
        alarmPresenter = new AlarmPresenter(this);
    }


    private void initComponent(){
        alarm_cancel.setOnClickListener(this);
        alarm_save.setOnClickListener(this);
        alarm_delete.setOnClickListener(this);
        ringtoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ringtone = 1;
                    ringtoneReminder.setTextColor(ContextCompat.getColor(AlarmActivity.this, R.color.ringtone_reminder));
                }else{
                    ringtone = 0;
                    ringtoneReminder.setTextColor(ContextCompat.getColor(AlarmActivity.this, R.color.pop_reminder));
                }
                changeColor();
            }
        });
        popSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    pop = 1;
                    popReminder.setTextColor(ContextCompat.getColor(AlarmActivity.this, R.color.ringtone_reminder));
                }else{
                    pop = 0;
                    popReminder.setTextColor(ContextCompat.getColor(AlarmActivity.this, R.color.pop_reminder));
                }
                changeColor();
            }
        });
    }

    private void changeColor(){
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
                day = text.substring(0,2);
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

    @Override
    public void onResume(){
        super.onResume();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
