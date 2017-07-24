package com.alphagao.done365.ui.model;

import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.greendao.bean.MemoEvent;
import com.alphagao.done365.greendao.bean.TodoEvent;
import com.alphagao.done365.greendao.gen.AgendaEventDao;
import com.alphagao.done365.greendao.gen.InboxEventDao;
import com.alphagao.done365.greendao.gen.MemoEventDao;
import com.alphagao.done365.greendao.gen.TodoEventDao;
import com.alphagao.done365.ui.view.DataListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alpha on 2017/4/26.
 */

public class SearchModelImpl implements SearchModel {


    private final InboxEventDao inboxEventDao;
    private final AgendaEventDao agendaEventDao;
    private final TodoEventDao todoEventDao;
    private final MemoEventDao memoEventDao;

    public SearchModelImpl() {
        inboxEventDao = DaoUtil.daoSession.getInboxEventDao();
        agendaEventDao = DaoUtil.daoSession.getAgendaEventDao();
        todoEventDao = DaoUtil.daoSession.getTodoEventDao();
        memoEventDao = DaoUtil.daoSession.getMemoEventDao();
    }

    @Override
    public void execQueryForSearch(String str, DataListener listener) {
        Map<String, List> result = new HashMap<>();

        List<InboxEvent> inboxEvents = queryInbox(str);
        List<AgendaEvent> agendaEvents = queryAgendas(str);
        List<TodoEvent> todoEvents = queryTodos(str);
        List<MemoEvent> memoEvents = queryMemos(str);
        List<InboxEvent> trashEvents = queryTrash(str);

        result.put("inbox", inboxEvents);
        result.put("agenda", agendaEvents);
        result.put("todo", todoEvents);
        result.put("memo", memoEvents);
        result.put("trash", trashEvents);

        listener.onComplete(result);
    }

    private List<MemoEvent> queryMemos(String str) {
        return memoEventDao.queryBuilder()
                .where(memoEventDao.queryBuilder().and(
                        MemoEventDao.Properties.IsDir.eq(MemoEvent.NO),
                        MemoEventDao.Properties.Content.like("%" + str + "%")))
                .orderAsc(MemoEventDao.Properties.CreateDate)
                .build().list();
    }

    private List<InboxEvent> queryTrash(String str) {
        return inboxEventDao.queryBuilder()
                .where(inboxEventDao.queryBuilder().and(
                        InboxEventDao.Properties.Deleted.eq(InboxEvent.YES),
                        InboxEventDao.Properties.Content.like("%" + str + "%")))
                .orderDesc(InboxEventDao.Properties.DeleteTime)
                .build().list();
    }

    private List<AgendaEvent> queryAgendas(String str) {
        return agendaEventDao.queryBuilder()
                .where(AgendaEventDao.Properties.Content.like("%" + str + "%"))
                .orderDesc(AgendaEventDao.Properties.AlarmDate, AgendaEventDao.Properties.AlarmTime)
                .build().list();
    }

    private List<InboxEvent> queryInbox(String str) {
        return inboxEventDao.queryBuilder()
                .where(inboxEventDao.queryBuilder().and(
                        InboxEventDao.Properties.Deleted.eq(InboxEvent.NO),
                        InboxEventDao.Properties.Content.like("%" + str + "%")))
                .orderDesc(InboxEventDao.Properties.CreateTime)
                .build().list();
    }

    private List<TodoEvent> queryTodos(String str) {
        return todoEventDao.queryBuilder()
                .where(TodoEventDao.Properties.Content.like("%" + str + "%"))
                .orderDesc(TodoEventDao.Properties.DueDate)
                .build().list();
    }
}
