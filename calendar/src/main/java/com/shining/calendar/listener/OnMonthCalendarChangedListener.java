package com.shining.calendar.listener;

import org.joda.time.LocalDate;

import java.util.List;

public interface OnMonthCalendarChangedListener {
    void onMonthCalendarChanged(LocalDate date, List<LocalDate> dateList);
}
