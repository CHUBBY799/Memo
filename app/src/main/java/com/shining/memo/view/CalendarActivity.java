package com.shining.memo.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
    private RecyclerView recyclerView;
    private CalendarAdapter calendarAdapter;
    private TextView calendar_month;
    private Button calendar_close;
    private Button last_month;
    private Button next_month;

    private LocalDate date;
    private List<LocalDate> mSelectDateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initView();
        initListener();
    }


    @Override
    public void onCalendarChanged(LocalDate date, List<LocalDate> dateList) {
        this.date = date;
        this.mSelectDateList = dateList;
        this.monthCalendar = ncalendar.getMonthCalendar();

        JSONArray taskDataArr;
        calendar_month.setText(Utils.formatMonthUS(date.getMonthOfYear()));
        CalendarPresenter calendarPresenter = new CalendarPresenter(this);

        //设置圆点
        List<String> pointList = new ArrayList<>(calendarPresenter.queryData(date.getYear()+"-"+Utils.formatTimeUnit(date.getMonthOfYear())));
        ncalendar.setPoint(pointList);

        //根据选择的日期集合查询数据库
        Collections.reverse(dateList);
        taskDataArr = calendarPresenter.queryData(dateList);
        calendarAdapter.setInfo(taskDataArr, taskDataArr.length());
        recyclerView.setAdapter(calendarAdapter);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.calendar_close:
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
        }
    }

    public Context getContext(){
        return this;
    }

    private void initView(){
        ncalendar = findViewById(R.id.calendar_content);
        recyclerView = findViewById(R.id.recyclerView);
        calendar_month = findViewById(R.id.calendar_month);
        calendar_close = findViewById(R.id.calendar_close);
        last_month = findViewById(R.id.last_month);
        next_month = findViewById(R.id.next_month);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        calendarAdapter = new CalendarAdapter(this, this);
        ncalendar.setOnCalendarChangedListener(this);
    }

    private void initListener(){
        calendar_close.setOnClickListener(this);
        last_month.setOnClickListener(this);
        next_month.setOnClickListener(this);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}