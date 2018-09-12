package com.shining.calendar.listener;

import org.joda.time.LocalDate;

public interface OnCalendarChangedListener {
    void onCalendarChanged(LocalDate date);
}
