package com.alphagao.done365.utils;

/**
 * Created by Alpha on 2017/4/27.
 */

public class DateUtil {
    private static final String TAG = "DateUtil";

    public static String longDate(String data) {
        String year = data.substring(0, 4);
        String month = data.substring(data.indexOf("-") + 1, data.lastIndexOf("-"));
        String day = data.substring(data.lastIndexOf("-") + 1);
        if (Integer.valueOf(month) < 10) {
            month = "0" + month;
        }
        if (Integer.valueOf(day) < 10) {
            day = "0" + day;
        }
        return year + "-" + month + "-" + day;
    }

    public static String shortDate(String date) {
        String year = date.substring(0, 4);
        int month = Integer.parseInt(date.substring(5, 7));
        int day = Integer.parseInt(date.substring(8, 10));
        String str = year + "-" + month + "-" + day;
        return str;
    }
}
