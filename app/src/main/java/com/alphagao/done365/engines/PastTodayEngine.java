package com.alphagao.done365.engines;

import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.gen.AgendaEventDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Alpha on 2017/5/11.
 */

public class PastTodayEngine {

    private static SimpleDateFormat shortFormat;
    private static SimpleDateFormat longformat;
    private static AgendaEventDao agendaEventDao;

    static {
        shortFormat = new SimpleDateFormat("MM-dd");
        longformat = new SimpleDateFormat("yyyy-MM-dd");
        agendaEventDao = DaoUtil.daoSession.getAgendaEventDao();
    }

    public static List<AgendaEvent> getPastTodayAgendas() {
        String shortDate = shortFormat.format(new Date());
        String longDate = longformat.format(new Date());

        return agendaEventDao.queryBuilder()
                .where(agendaEventDao.queryBuilder()
                        .and(AgendaEventDao.Properties.AlarmDate.like("%" + shortDate),
                                AgendaEventDao.Properties.AlarmDate.lt(longDate)))
                .build()
                .list();
    }
}
