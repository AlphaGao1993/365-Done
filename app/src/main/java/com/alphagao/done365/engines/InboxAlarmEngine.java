package com.alphagao.done365.engines;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.alphagao.done365.R;
import com.alphagao.done365.ui.activity.MainActivity;
import com.alphagao.done365.utils.Prefs;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Alpha on 2017/5/11.
 * 收件箱提醒相关业务逻辑
 */

public class InboxAlarmEngine {
    private static final String TAG = "InboxAlarmEngine";

    /**
     * 添加当日的收件箱提醒通知
     *
     * @param timer   timer
     * @param context context
     */
    public static void addInboxClearAlarm(Timer timer, final Context context) {
        Intent i = new Intent(context, MainActivity.class);
        final PendingIntent pi = PendingIntent.getActivity(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String descripetion = AgendaAlarmEngine.getTodayInboxCountStr();
                Notification notification = new NotificationCompat
                        .Builder(context)
                        .setContentTitle(context.getString(R.string.clear_today_inbox))
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentText(descripetion)
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI,
                                NotificationCompat.DEFAULT_SOUND)
                        .setLights(Color.GREEN, 500, 500)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setVibrate(new long[]{0, 300, 300, 300, 300})
                        .build();

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                managerCompat.notify((int) (InboxAlarmEngine.getTodayLongStr() % Integer.MAX_VALUE),
                        notification);
            }
        }, InboxAlarmEngine.getTodayInboxClearAlarmDate(context));
    }

    /**
     * 获取今日日期的文本描述
     *
     * @return 例如 20171212
     */
    public static long getTodayLongStr() {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String date = year + month + day;
        Log.d(TAG, "getTodayLongStr: done365:" + date);
        return Long.parseLong(date);
    }

    /**
     * 每日清理提醒，默认时间 21:30
     *
     * @param context context
     * @return 21:30 的 Date 对象
     */
    public static Date getTodayInboxClearAlarmDate(Context context) {
        Calendar calendar = Calendar.getInstance();
        Prefs prefs = Prefs.getInstance(context);
        Prefs userPrefs = Prefs.getUserInstance(context, prefs.getLong("current_login_user"));
        calendar.set(Calendar.HOUR_OF_DAY, userPrefs.getInt("inbox_clear_hour", 20));
        calendar.set(Calendar.MINUTE, userPrefs.getInt("inbox_clear_minute", 40));
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 当前时间是否已经超过预期的提醒时间
     *
     * @param context context
     * @return 是否过期
     */
    public static boolean isInboxClearedOverdue(Context context) {
        return new Date().getTime() > getTodayInboxClearAlarmDate(context).getTime();
    }
}
