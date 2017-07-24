package com.alphagao.done365.ui.model;

import com.alphagao.done365.ui.view.DataListener;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.greendao.gen.InboxEventDao;

import java.util.List;

/**
 * Created by Alpha on 2017/4/23.
 */

public class InboxModelDBImpl implements InboxModel {

    private final InboxEventDao inboxEventDao;

    public InboxModelDBImpl() {
        inboxEventDao = new DaoUtil().daoSession.getInboxEventDao();
    }

    @Override
    public Long insertInbox(InboxEvent inbox) {
        inboxEventDao.insert(inbox);
        return inboxEventDao.queryBuilder()
                .where(InboxEventDao.Properties.CreateTime.eq(inbox.getCreateTime()))
                .build().unique().getId();
    }

    @Override
    public void updateInbox(InboxEvent inbox) {
        inboxEventDao.update(inbox);
    }

    @Override
    public void updateInboxInTx(List<InboxEvent> inboxEvents) {
        inboxEventDao.updateInTx(inboxEvents);
    }

    @Override
    public void deleteInbox(InboxEvent inbox) {
        inboxEventDao.delete(inbox);
    }

    @Override
    public void deleteInboxInTx(List<InboxEvent> inboxEvents) {
        inboxEventDao.deleteInTx(inboxEvents);
    }

    @Override
    public void loadInboxByCreateTime(DataListener<List<InboxEvent>> listener) {
        List<InboxEvent> inboxEvents = inboxEventDao.queryBuilder()
                .where(InboxEventDao.Properties.Deleted.eq(InboxEvent.NO))
                .orderDesc(InboxEventDao.Properties.CreateTime)
                .build()
                .list();
        if (inboxEvents.size() > 0) {
            listener.onComplete(inboxEvents);
        } else {
            listener.onComplete(null);
        }
    }

    @Override
    public void loadDeletedInboxByDeletedTime(DataListener<List<InboxEvent>> listener) {
        List<InboxEvent> deletedInbox = inboxEventDao.queryBuilder()
                .where(InboxEventDao.Properties.Deleted.eq(InboxEvent.YES))
                .orderDesc(InboxEventDao.Properties.DeleteTime)
                .build()
                .list();
        if (deletedInbox.size() > 0) {
            listener.onComplete(deletedInbox);
        }
    }
}
