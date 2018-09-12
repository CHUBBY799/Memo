package com.shining.calendar.listener;

import org.joda.time.LocalDate;

public interface OnMonthCalendarChangedListener {
    void onMonthCalendarChanged(LocalDate date);
}
