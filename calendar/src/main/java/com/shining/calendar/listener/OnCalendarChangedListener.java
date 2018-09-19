package com.shining.calendar.listener;

import org.joda.time.LocalDate;

import java.util.List;

public interface OnCalendarChangedListener {
    void onCalendarChanged(LocalDate date, List<LocalDate> dateList);
}
