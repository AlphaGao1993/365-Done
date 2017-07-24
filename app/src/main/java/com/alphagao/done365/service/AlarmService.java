package com.alphagao.done365.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.alphagao.done365.engines.AgendaAlarmEngine;
import com.alphagao.done365.engines.InboxAlarmEngine;
import com.alphagao.done365.greendao.bean.AgendaEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

/**
 * Created by Alpha on 2017/4/13.
 */

public class AlarmService extends IntentService {

    private static final String TAG = "AlarmService";
    /**
     * 每分钟发送一次 Alarm 广播，以开启此 Service 进行必要的提醒
     */
    private static long PUSH_INTERVAL = 1000 * 60;
    /**
     * 根据日程 ID 和提醒日期保存已经发送的定时提醒任务
     */
    private static Map<Long, Date> hadScheduledAgendas;
    private static Timer timer;

    public AlarmService() {
        super(TAG);

        if (timer == null) {
            timer = new Timer();
        }
        if (hadScheduledAgendas == null) {
            hadScheduledAgendas = new HashMap<>();
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        publishTodayClearAlarm();

        List<AgendaEvent> needAlarmAgendas = AgendaAlarmEngine.query5minutesAgenda();
        if (needAlarmAgendas.size() <= 0) {
            stopSelf();
            return;
        }

        publishNotificationForEveryAgenda(needAlarmAgendas);
    }

    private void publishNotificationForEveryAgenda(List<AgendaEvent> needAlarmAgendas) {
        for (final AgendaEvent agenda : needAlarmAgendas) {
            //如果该提醒已经被设置则不再继续添加任务
            if (!agendaHasScheduled(agenda)) {
                AgendaAlarmEngine.addAgendaNotification(timer, agenda, this);
            }
            hadScheduledAgendas.put(agenda.getId(), new Date(agenda.getAlarmTimeMills()));
        }
    }

    private boolean agendaHasScheduled(AgendaEvent agenda) {
        Date date = hadScheduledAgendas.get(agenda.getId());
        if (date == null) {
            return false;
        }
        return date.getTime() == agenda.getAlarmTimeMills();
    }

    private void publishTodayClearAlarm() {
        boolean needCleareTodayInbox = !InboxAlarmEngine.isInboxClearedOverdue(this)
                && AgendaAlarmEngine.getTodayInboxCountStr() != null
                && !hasTodayCleared();

        if (needCleareTodayInbox) {
            InboxAlarmEngine.addInboxClearAlarm(timer, this);
            hadScheduledAgendas.put(InboxAlarmEngine.getTodayLongStr(),
                    InboxAlarmEngine.getTodayInboxClearAlarmDate(this));
        }
    }

    private boolean hasTodayCleared() {
        return hadScheduledAgendas.containsKey(InboxAlarmEngine.getTodayLongStr());
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, AlarmService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = AlarmService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (isOn) {
            manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), PUSH_INTERVAL, pi);
        } else {
            manager.cancel(pi);
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = AlarmService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}
