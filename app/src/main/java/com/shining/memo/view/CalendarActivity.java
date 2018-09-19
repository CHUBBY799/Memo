package com.shining.memo.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
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

public class CalendarActivity extends AppCompatActivity implements OnCalendarChangedListener{

    private NCalendar ncalendar;
    private RecyclerView recyclerView;
    private CalendarAdapter calendarAdapter;
    private TextView tv_month;
    private TextView tv_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        initView();

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
        tv_month.setText(formatMonthUS(date.getMonthOfYear()));
        tv_date.setText(String.format(getResources().getString(R.string.title_date),formatMonthUS(date.getMonthOfYear()),date.getDayOfMonth(),date.getYear()));
        queryData(dateList);
    }

    private void queryData(List<LocalDate> dateList){
        //查询数据库

        JSONArray infoArr = new JSONArray();
        for(int i = 0 ; i < dateList.size() ; i++){
            String title = "周六约同学吃饭" + " " + dateList.get(i);
            JSONObject info = new JSONObject();
            try {
                info.put("title", title);
            }catch (JSONException e){
                e.printStackTrace();
            }
            infoArr.put(info);
        }
        calendarAdapter.setInfo(infoArr, infoArr.length());
        recyclerView.setAdapter(calendarAdapter);
    }

    public void setDate(View view) {
        ncalendar.setDate("2017-12-31");
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

    private String formatMonthUS(int month){
        String resultMonth = null;
        switch (month){
            case 1:
                resultMonth = "Jan";
                break;
            case 2:
                resultMonth = "Feb";
                break;
            case 3:
                resultMonth = "Mar";
                break;
            case 4:
                resultMonth = "Apr";
                break;
            case 5:
                resultMonth = "May";
                break;
            case 6:
                resultMonth = "Jun";
                break;
            case 7:
                resultMonth = "Jul";
                break;
            case 8:
                resultMonth = "Aug";
                break;
            case 9:
                resultMonth = "Sep";
                break;
            case 10:
                resultMonth = "Oct";
                break;
            case 11:
                resultMonth = "Nov";
                break;
            case 12:
                resultMonth = "Dec";
                break;
            default:
                break;
        }
        return resultMonth;
    }

    private void initView(){
        ncalendar = findViewById(R.id.calendar_content);
        recyclerView = findViewById(R.id.recyclerView);
        tv_month = findViewById(R.id.tv_month);
        tv_date = findViewById(R.id.tv_date);
    }
}