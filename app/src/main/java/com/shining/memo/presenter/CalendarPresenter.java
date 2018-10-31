package com.shining.memo.presenter;

import android.content.Context;

import com.shining.memo.model.CalendarImpl;
import com.shining.memo.model.CalendarModel;
import com.shining.memo.view.CalendarActivity;

import org.joda.time.LocalDate;
import org.json.JSONArray;

import java.util.HashSet;
import java.util.List;

public class CalendarPresenter {
    private CalendarModel calendarModel;

    public CalendarPresenter(Context context){
        this.calendarModel = new CalendarImpl(context);
    }

    public HashSet<String> queryData(String year_month, String type){
        return calendarModel.queryData(year_month, type);
    }

    public JSONArray queryData(List<LocalDate> dateList, String type){
        return calendarModel.queryData(dateList, type);
    }

    public void deleteData(String id, String type){
        calendarModel.deleteData(id, type);
    }
}
