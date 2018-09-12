package com.shining.calendar.listener;

import org.joda.time.LocalDate;

public interface OnClickMonthViewListener {

    void onClickCurrentMonth(LocalDate date);

    void onClickLastMonth(LocalDate date);

    void onClickNextMonth(LocalDate date);

}
