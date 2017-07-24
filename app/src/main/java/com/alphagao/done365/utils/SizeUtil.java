package com.alphagao.done365.utils;

import java.util.Calendar;

/**
 * Created by Alpha on 2017/3/17.
 */

public class SizeUtil {
    public static String getRightTime(long timeMills) {
        String unit = "秒";
        timeMills = timeMills / 1000;//秒
        if (timeMills > 60) {
            timeMills = timeMills / 60;//分钟
            unit = "分钟";
            if (timeMills >= 60) {
                timeMills = timeMills / 60;//小时
                unit = "小时";
                if (timeMills >= 24) {
                    timeMills /= 24;//天
                    unit = "天";
                }
            }
        }

        return timeMills + " " + unit;
    }

    public static Long getTimeMills(int dateType, int dateNum) {
        long needTimeMills;
        switch (dateType) {
            case Calendar.MINUTE:
                needTimeMills = dateNum * 1000 * 60;
                break;
            case Calendar.HOUR:
                needTimeMills = dateNum * 1000 * 60 * 60;
                break;
            case Calendar.DAY_OF_YEAR:
                needTimeMills = dateNum * 1000 * 60 * 60 * 24;
                break;
            case Calendar.WEEK_OF_YEAR:
                needTimeMills = dateNum * 1000 * 60 * 60 * 24 * 7;
                break;
            case Calendar.MONTH:
                needTimeMills = dateNum * 1000 * 60 * 60 * 24 * 30;
                break;
            case Calendar.YEAR:
                needTimeMills = dateNum * 1000 * 60 * 60 * 24 * 365;
                break;
            default:
                needTimeMills = -1;
                break;
        }
        return needTimeMills;
    }

    public static Long getTimeMills(String dateType, int dateNum) {
        long needTimeMills;
        switch (dateType) {
            case "分钟":
                needTimeMills = dateNum * 1000 * 60;
                break;
            case "小时":
                needTimeMills = dateNum * 1000 * 60 * 60;
                break;
            case "天":
                needTimeMills = dateNum * 1000 * 60 * 60 * 24;
                break;
            case "周":
                needTimeMills = dateNum * 1000 * 60 * 60 * 24 * 7;
                break;
            case "月":
                needTimeMills = dateNum * 1000 * 60 * 60 * 24 * 30;
                break;
            case "年":
                needTimeMills = dateNum * 1000 * 60 * 60 * 24 * 365;
                break;
            default:
                needTimeMills = -1;
                break;
        }
        return needTimeMills;
    }

    public static int getTimeType(String typeStr) {
        switch (typeStr) {
            case "分钟":
                return Calendar.MINUTE;
            case "小时":
                return Calendar.HOUR;
            case "天":
                return Calendar.DAY_OF_YEAR;
            case "周":
                return Calendar.WEEK_OF_YEAR;
            case "月":
                return Calendar.MONTH;
            case "年":
                return Calendar.YEAR;
            default:
                return 0;
        }
    }
}
