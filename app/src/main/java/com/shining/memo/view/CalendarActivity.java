package com.shining.memo.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.shining.calendar.calendar.NCalendar;
import com.shining.calendar.listener.OnCalendarChangedListener;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.shining.memo.R;
import com.shining.memo.adapter.CalendarAdapter;
import com.shining.memo.presenter.CalendarPresenter;
import com.shining.memo.utils.Utils;

public class CalendarActivity extends AppCompatActivity implements OnCalendarChangedListener{

    private NCalendar ncalendar;
    private RecyclerView recyclerView;
    private CalendarAdapter calendarAdapter;
    private TextView calendar_month;
    private Button calendar_close;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        initView();

        calendar_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        calendarAdapter = new CalendarAdapter(this);
        ncalendar.setOnCalendarChangedListener(this);

        ncalendar.post(new Runnable() {
            @Override
            public void run() {

                List<String> list = new ArrayList<>();
                list.add("2018-09-21");
                list.add("2018-10-21");
                list.add("2018-10-1");
                list.add("2018-10-15");
                list.add("2018-10-18");
                list.add("2018-10-26");
                list.add("2018-11-21");

                ncalendar.setPoint(list);
            }
        });
    }


    @Override
    public void onCalendarChanged(LocalDate date, List<LocalDate> dateList) {
        calendar_month.setText(Utils.formatMonthUS(date.getMonthOfYear()));
        CalendarPresenter calendarPresenter = new CalendarPresenter(this);
        JSONArray taskDataArr = calendarPresenter.queryData(dateList);
        calendarAdapter.setInfo(taskDataArr, taskDataArr.length());
        recyclerView.setAdapter(calendarAdapter);
    }


    public Context getContext(){
        return this;
    }

    public void setPoint(View view) {
        List<String> list = new ArrayList<>();
        list.add("2017-09-21");
        list.add("2017-10-21");
        list.add("2017-10-1");
        list.add("2017-10-15");
        list.add("2017-10-18");
        list.add("2017-10-26");
        list.add("2017-11-21");

        ncalendar.setPoint(list);
    }

    private void initView(){
        ncalendar = findViewById(R.id.calendar_content);
        recyclerView = findViewById(R.id.recyclerView);
        calendar_month = findViewById(R.id.calendar_month);
        calendar_close = findViewById(R.id.calendar_close);
    }
}