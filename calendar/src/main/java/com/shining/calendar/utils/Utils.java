package com.shining.calendar.utils;

import android.content.Context;
import android.util.TypedValue;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * dp转px
     *
     */
    public static float dp2px(Context context, int dpVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     */
    public static float sp2px(Context context, float spVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * 是否同月
     */
    public static boolean isEqualsMonth(LocalDate date1, LocalDate date2) {
        return date1.getYear() == date2.getYear() && date1.getMonthOfYear() == date2.getMonthOfYear();
    }

    /**
     * 第一个是不是第二个的上一个月,只在此处有效
     *
     */
    public static boolean isLastMonth(LocalDate date1, LocalDate date2) {
        LocalDate date = date2.plusMonths(-1);
        return date1.getMonthOfYear() == date.getMonthOfYear();
    }

    /**
     * 第一个是不是第二个的下一个月，只在此处有效
     *
     */
    public static boolean isNextMonth(LocalDate date1, LocalDate date2) {
        LocalDate date = date2.plusMonths(1);
        return date1.getMonthOfYear() == date.getMonthOfYear();
    }

    /**
     * 获得两个日期距离几个月
     *
     */
    public static int getIntervalMonths(LocalDate date1, LocalDate date2) {
        date1 = date1.withDayOfMonth(1);
        date2 = date2.withDayOfMonth(1);

        return Months.monthsBetween(date1, date2).getMonths();
    }

    /**
     * 获得两个日期距离几周
     *
     */
    public static int getIntervalWeek(LocalDate date1, LocalDate date2, int type) {

        if (type == 0) {
            date1 = getSunFirstDayOfWeek(date1);
            date2 = getSunFirstDayOfWeek(date2);
        } else {
            date1 = getMonFirstDayOfWeek(date1);
            date2 = getMonFirstDayOfWeek(date2);
        }

        return Weeks.weeksBetween(date1, date2).getWeeks();

    }

    /**
     * 是否是今天
     *
     */
    public static boolean isToday(LocalDate date) {
        return new LocalDate().equals(date);

    }

    /**
     * @param date 今天
     * @param type     0，周日，1周一
     *
     */
    public static NCalendar getMonthCalendar(LocalDate date, int type) {

        LocalDate lastMonthDate = date.plusMonths(-1);//上个月
        LocalDate nextMonthDate = date.plusMonths(1);//下个月

        int days = date.dayOfMonth().getMaximumValue();//当月天数
        int lastMonthDays = lastMonthDate.dayOfMonth().getMaximumValue();//上个月的天数

        int firstDayOfWeek = new LocalDate(date.getYear(), date.getMonthOfYear(), 1).getDayOfWeek();//当月第一天周几

        int endDayOfWeek = new LocalDate(date.getYear(), date.getMonthOfYear(), days).getDayOfWeek();//当月最后一天周几

        NCalendar nCalendar = new NCalendar();
        List<LocalDate> dates = new ArrayList<>();

        //周日开始的
        if (type == 0) {
            //上个月
            if (firstDayOfWeek != 7) {
                for (int i = 0; i < firstDayOfWeek; i++) {
                    LocalDate date1 = new LocalDate(lastMonthDate.getYear(), lastMonthDate.getMonthOfYear(), lastMonthDays - (firstDayOfWeek - i - 1));
                    dates.add(date1);
                }
            }
            //当月
            for (int i = 0; i < days; i++) {
                LocalDate date1 = new LocalDate(date.getYear(), date.getMonthOfYear(), i + 1);
                dates.add(date1);
            }
            //下个月
            if (endDayOfWeek == 7) {
                endDayOfWeek = 0;
            }
            for (int i = 0; i < 6 - endDayOfWeek; i++) {
                LocalDate date1 = new LocalDate(nextMonthDate.getYear(), nextMonthDate.getMonthOfYear(), i + 1);
                dates.add(date1);
            }
        } else {
            //周一开始的
            for (int i = 0; i < firstDayOfWeek - 1; i++) {
                LocalDate date1 = new LocalDate(lastMonthDate.getYear(), lastMonthDate.getMonthOfYear(), lastMonthDays - (firstDayOfWeek - i - 2));
                dates.add(date1);
            }
            for (int i = 0; i < days; i++) {
                LocalDate date1 = new LocalDate(date.getYear(), date.getMonthOfYear(), i + 1);
                dates.add(date1);
            }
            for (int i = 0; i < 7 - endDayOfWeek; i++) {
                LocalDate date1 = new LocalDate(nextMonthDate.getYear(), nextMonthDate.getMonthOfYear(), i + 1);
                dates.add(date1);
            }
        }

        nCalendar.dateList = dates;
        return nCalendar;

    }

    /**
     * 周视图的数据
     *
     */
    public static NCalendar getWeekCalendar(LocalDate date, int type) {
        List<LocalDate> dateList = new ArrayList<>();
        if (type == 0) {
            date = getSunFirstDayOfWeek(date);
        } else {
            date = getMonFirstDayOfWeek(date);
        }

        NCalendar calendar = new NCalendar();
        for (int i = 0; i < 7; i++) {
            LocalDate date1 = date.plusDays(i);
            dateList.add(date1);
        }
        calendar.dateList = dateList;
        return calendar;
    }

    /**
     * 转化一周从周日开始
     */
    public static LocalDate getSunFirstDayOfWeek(LocalDate date) {
        if (date.dayOfWeek().get() == 7) {
            return date;
        } else {
            return date.minusWeeks(1).withDayOfWeek(7);
        }
    }

    /**
     * 转化一周从周一开始
     */
    public static LocalDate getMonFirstDayOfWeek(LocalDate date) {
        return date.dayOfWeek().withMinimumValue();
    }


    /**
     * 格式化的日期
     */
    public static class NCalendar {
        public List<LocalDate> dateList;
    }
}
