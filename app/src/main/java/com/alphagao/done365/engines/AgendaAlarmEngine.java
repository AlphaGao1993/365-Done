package com.alphagao.done365.engines;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.alphagao.done365.R;
import com.alphagao.done365.app.App;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.AgendaAlarmEvent;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.bean.ContextEvent;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.greendao.bean.ProjectEvent;
import com.alphagao.done365.greendao.gen.AgendaAlarmEventDao;
import com.alphagao.done365.greendao.gen.AgendaEventDao;
import com.alphagao.done365.greendao.gen.ContextEventDao;
import com.alphagao.done365.greendao.gen.InboxEventDao;
import com.alphagao.done365.greendao.gen.ProjectEventDao;
import com.alphagao.done365.ui.activity.NewAgendaActivity;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Alpha on 2017/5/11.
 * 日程提醒相关业务逻辑
 */

public class AgendaAlarmEngine {
    private static long FIVE_MINUTES = 1000 * 60 * 5;

    private static InboxEventDao inboxEventDao;
    private static AgendaEventDao agendaEventDao;
    private static ContextEventDao contextEventDao;
    private static ProjectEventDao projectEventDao;
    private static AgendaAlarmEventDao agendaAlarmEventDao;

    static {
        inboxEventDao = DaoUtil.daoSession.getInboxEventDao();
        agendaEventDao = DaoUtil.daoSession.getAgendaEventDao();
        contextEventDao = DaoUtil.daoSession.getContextEventDao();
        projectEventDao = DaoUtil.daoSession.getProjectEventDao();
        agendaAlarmEventDao = DaoUtil.daoSession.getAgendaAlarmEventDao();
    }

    /**
     * 获取今日收件箱清理提醒的文本描述
     *
     * @return 文本描述
     */
    public static String getTodayInboxCountStr() {
        List<InboxEvent> list = inboxEventDao
                .queryBuilder()
                .where(InboxEventDao.Properties.Deleted.eq(InboxEvent.NO))
                .build()
                .list();
        if (list.size() > 0) {
            return App.getAppContext().getString(R.string.left_inbox, list.size());
        }
        return null;
    }

    /**
     * 查询 5 分钟内需要提醒的日程数据
     *
     * @return 日程s
     */
    public static List<AgendaEvent> query5minutesAgenda() {
        long now = Calendar.getInstance().getTimeInMillis();
        return agendaEventDao.queryBuilder()
                .where(agendaEventDao.queryBuilder().and(
                        AgendaEventDao.Properties.Finished.eq(AgendaEvent.NO),
                        AgendaEventDao.Properties.AlarmTimeMills.between(now, now + FIVE_MINUTES)))
                .build()
                .list();
    }

    /**
     * 获取日程关联的情境和项目的文本描述
     *
     * @param agenda agenda
     * @return 情境和项目的文本描述
     */
    public static String getAgendaContextAndProject(AgendaEvent agenda) {
        StringBuilder builder = new StringBuilder();
        ContextEvent context = contextEventDao
                .queryBuilder()
                .where(ContextEventDao.Properties.Id.eq(agenda.getContextId()))
                .build().list().get(0);
        builder.append("情境：").append(context.getName());
        if (agenda.getProjectId() != null) {
            List<ProjectEvent> list = projectEventDao
                    .queryBuilder()
                    .where(ProjectEventDao.Properties.Id.eq(agenda))
                    .build()
                    .list();
            if (list.size() > 0) {
                builder.append("   项目：").append(list.get(0).getName());
            }
        }
        return builder.toString();
    }

    /**
     * 查询某个日程提醒是否需要震动
     *
     * @param agenda 日程
     * @return 是否震动
     */
    public static boolean needAlarmVirberate(AgendaEvent agenda) {
        List<AgendaAlarmEvent> events = agendaAlarmEventDao
                .queryBuilder()
                .where(AgendaAlarmEventDao.Properties.AgendaId.eq(agenda.getId()))
                .build()
                .list();
        return events.size() > 0 && events.get(0).getVibrative() == AgendaAlarmEvent.YES;
    }

    /**
     * 添加日程提醒的定时任务
     *
     * @param timer   定时器
     * @param agenda  日程
     * @param context context
     */
    public static void addAgendaNotification(Timer timer, final AgendaEvent agenda,
                                             final Context context) {
        Intent i = NewAgendaActivity.newIntent(context);
        i.putExtra(NewAgendaActivity.OPEN_MODE, NewAgendaActivity.MODE_MODIFY);
        i.putExtra("agenda", new Gson().toJson(agenda));
        final PendingIntent pi = PendingIntent.getActivity(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                addNotification(agenda, context, pi);
            }
        }, new Date(agenda.getAlarmTimeMills()));
    }

    /**
     * 添加日程提醒
     *
     * @param agenda  日程
     * @param context context
     * @param pi      意图
     */
    private static void addNotification(AgendaEvent agenda, Context context, PendingIntent pi) {
        String descripetion = AgendaAlarmEngine
                .getAgendaContextAndProject(agenda);
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(context)
                .setTicker(context.getString(R.string.new_agenda_ticker))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(agenda.getContent())
                .setContentText(descripetion)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI,
                        NotificationCompat.DEFAULT_SOUND)
                .setLights(Color.GREEN, 500, 500)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        if (AgendaAlarmEngine.needAlarmVirberate(agenda)) {
            builder.setVibrate(new long[]{0, 300, 300, 300, 300});
        }
        Notification notification = builder.build();

        NotificationManagerCompat managerCompat = NotificationManagerCompat
                .from(context);
        managerCompat.notify((int) (agenda.getId() % Integer.MAX_VALUE),
                notification);
    }
}
