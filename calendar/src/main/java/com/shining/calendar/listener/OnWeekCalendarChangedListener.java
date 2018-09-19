package com.shining.calendar.listener;

import org.joda.time.LocalDate;

import java.util.List;

public interface OnWeekCalendarChangedListener {
    void onWeekCalendarChanged(LocalDate date, List<LocalDate> dateList);
}
