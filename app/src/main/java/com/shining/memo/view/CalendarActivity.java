package com.shining.memo.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shining.calendar.calendar.MonthCalendar;
import com.shining.calendar.calendar.NCalendar;
import com.shining.calendar.listener.OnCalendarChangedListener;

import org.joda.time.LocalDate;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.shining.memo.R;
import com.shining.memo.adapter.CalendarAdapter;
import com.shining.memo.presenter.CalendarPresenter;
import com.shining.memo.utils.Utils;

public class CalendarActivity extends AppCompatActivity implements OnCalendarChangedListener,View.OnClickListener{

    private NCalendar ncalendar;
    private MonthCalendar monthCalendar;
    private RecyclerView calendar_event;
    private CalendarAdapter calendarAdapter;
    private AlertDialog dialog;

    private TextView calendar_type;
    private TextView month_year;
    private ImageButton calendar_close;
    private ImageButton last_month;
    private ImageButton next_month;
    private Button guide_confirm;

    private String calendarType;
    private LocalDate date;
    private List<LocalDate> mSelectDateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Intent intent = getIntent();
        calendarType = intent.getStringExtra("calendarType");
        initView();
        initListener();
        SharedPreferences preferences= getSharedPreferences("countCalendar", 0);
        int count = preferences.getInt("countCalendar", 0);// 取出数据
        if(count == 0){
            onCreateDialog();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("countCalendar", 1);
            editor.apply();
        }
    }


    @Override
    public void onCalendarChanged(LocalDate date, List<LocalDate> dateList) {
        this.date = date;
        this.mSelectDateList = dateList;
        this.monthCalendar = ncalendar.getMonthCalendar();

        JSONArray taskDataArr;
        String year = String.valueOf(date.getYear());
        String month = String.valueOf(date.getMonthOfYear());
        if (getResources().getConfiguration().locale.getCountry().equals("CN")){
            month_year.setText(getString(R.string.month_year, year, month));
        }else {
            month_year.setText(getString(R.string.month_year, Utils.formatMonthSimUS(date.getMonthOfYear()), year));
        }
        CalendarPresenter calendarPresenter = new CalendarPresenter(this);

        //设置圆点
        List<String> pointList = new ArrayList<>(calendarPresenter.queryData(date.getYear()+"-"+Utils.formatTimeUnit(date.getMonthOfYear()), calendarType));
        ncalendar.setPoint(pointList);

        //根据选择的日期集合查询数据库
        Collections.reverse(dateList);
        taskDataArr = calendarPresenter.queryData(dateList, calendarType);
        calendarAdapter.setInfo(taskDataArr, taskDataArr.length(), calendarType);
        calendar_event.setAdapter(calendarAdapter);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.calendar_close:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.calendar_type:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.last_month:
                date = date.plusMonths(-1).dayOfMonth().withMaximumValue();
                if (monthCalendar != null){
                    monthCalendar.onClickLastMonth(date, true);
                    ncalendar.onMonthCalendarChanged(date, mSelectDateList);
                }
                break;
            case R.id.next_month:
                date = date.plusMonths(1).dayOfMonth().withMaximumValue();
                if (monthCalendar != null){
                    monthCalendar.onClickNextMonth(date, true);
                    ncalendar.onMonthCalendarChanged(date, mSelectDateList);
                }
                break;
            case R.id.calendar_guide_ok:
                dialog.dismiss();
                break;
            default:
                break;
        }
    }

    public Context getContext(){
        return this;
    }

    private void initView(){
        ncalendar = findViewById(R.id.calendar_content);
        calendar_event = findViewById(R.id.calendar_event);

        calendar_type = findViewById(R.id.calendar_type);
        switch (calendarType){
            case "task":
                calendar_type.setText(getString(R.string.task));
                break;
            case "list":
                calendar_type.setText(getString(R.string.list));
                break;
            case "note":
                calendar_type.setText(getString(R.string.note));
                break;
            default:
        }

        month_year = findViewById(R.id.month_year);
        calendar_close = findViewById(R.id.calendar_close);
        last_month = findViewById(R.id.last_month);
        next_month = findViewById(R.id.next_month);
        calendar_event.setLayoutManager(new LinearLayoutManager(this));
        calendar_event.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        calendarAdapter = new CalendarAdapter(this, this);
        ncalendar.setOnCalendarChangedListener(this);
    }

    private void initListener(){
        calendar_close.setOnClickListener(this);
        last_month.setOnClickListener(this);
        next_month.setOnClickListener(this);
        calendar_type.setOnClickListener(this);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private void onCreateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog);
        builder.setView(R.layout.calendar_guide);
        dialog = builder.show();
        guide_confirm = dialog.findViewById(R.id.calendar_guide_ok);
        guide_confirm.setOnClickListener(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
    }
}