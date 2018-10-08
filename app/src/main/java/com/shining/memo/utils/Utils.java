package com.shining.memo.utils;

public class Utils {

    /**
     * 个位数转十位数
     */
    public static String formatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    /**
     * 将数据月份转化成英语简写月份
     */
    public static String formatMonthSimUS(int month){
        String resultMonth = null;
        switch (month){
            case 1:
                resultMonth = "Jan.";
                break;
            case 2:
                resultMonth = "Feb.";
                break;
            case 3:
                resultMonth = "Mar.";
                break;
            case 4:
                resultMonth = "Apr.";
                break;
            case 5:
                resultMonth = "May.";
                break;
            case 6:
                resultMonth = "Jun.";
                break;
            case 7:
                resultMonth = "Jul.";
                break;
            case 8:
                resultMonth = "Aug.";
                break;
            case 9:
                resultMonth = "Sept.";
                break;
            case 10:
                resultMonth = "Oct.";
                break;
            case 11:
                resultMonth = "Nov.";
                break;
            case 12:
                resultMonth = "Dec.";
                break;
            default:
                break;
        }
        return resultMonth;
    }

    /**
     * 将数据月份转化成英语月份
     */
    public static String formatMonthUS(int month){
        String resultMonth = null;
        switch (month){
            case 1:
                resultMonth = "January";
                break;
            case 2:
                resultMonth = "February";
                break;
            case 3:
                resultMonth = "March";
                break;
            case 4:
                resultMonth = "April";
                break;
            case 5:
                resultMonth = "May";
                break;
            case 6:
                resultMonth = "June";
                break;
            case 7:
                resultMonth = " July";
                break;
            case 8:
                resultMonth = "August";
                break;
            case 9:
                resultMonth = "September";
                break;
            case 10:
                resultMonth = "October";
                break;
            case 11:
                resultMonth = "November";
                break;
            case 12:
                resultMonth = "December";
                break;
            default:
                break;
        }
        return resultMonth;
    }

    /**
     * 将英语简写月份转化成数据月份
     */
    public static int formatMonthNumber(String month){
        int resultMonth = 0;
        switch (month){
            case "Jan.":
                resultMonth = 1;
                break;
            case "Feb.":
                resultMonth = 2;
                break;
            case "Mar.":
                resultMonth = 3;
                break;
            case "Apr.":
                resultMonth = 4;
                break;
            case "May.":
                resultMonth = 5;
                break;
            case "Jun.":
                resultMonth = 6;
                break;
            case "Jul.":
                resultMonth = 7;
                break;
            case "Aug.":
                resultMonth = 8;
                break;
            case "Sept.":
                resultMonth = 9;
                break;
            case "Oct.":
                resultMonth = 10;
                break;
            case "Nov.":
                resultMonth = 11;
                break;
            case "Dec.":
                resultMonth = 12;
                break;
            default:
                break;
        }
        return resultMonth;
    }
}
