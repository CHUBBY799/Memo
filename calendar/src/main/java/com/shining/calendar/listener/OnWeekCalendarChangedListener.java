package com.shining.calendar.listener;

import org.joda.time.LocalDate;

public interface OnWeekCalendarChangedListener {
    void onWeekCalendarChanged(LocalDate date);
}
