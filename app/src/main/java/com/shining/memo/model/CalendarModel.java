package com.shining.memo.model;

import org.joda.time.LocalDate;
import org.json.JSONArray;

import java.util.HashSet;
import java.util.List;

public interface CalendarModel {
    JSONArray queryData(List<LocalDate> dateList, String type);
    HashSet<String> queryData(String year_month, String type);
    void deleteData(String id, String type);
}
