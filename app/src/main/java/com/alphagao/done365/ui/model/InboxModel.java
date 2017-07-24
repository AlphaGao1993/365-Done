package com.alphagao.done365.ui.model;

import com.alphagao.done365.ui.view.DataListener;
import com.alphagao.done365.greendao.bean.InboxEvent;

import java.util.List;

/**
 * Created by Alpha on 2017/4/23.
 */

public interface InboxModel {

    Long insertInbox(InboxEvent inbox);

    void updateInbox(InboxEvent inbox);

    void updateInboxInTx(List<InboxEvent> inboxEvents);

    void deleteInbox(InboxEvent inbox);

    void deleteInboxInTx(List<InboxEvent> inboxEvents);

    void loadInboxByCreateTime(DataListener<List<InboxEvent>> listener);

    void loadDeletedInboxByDeletedTime(DataListener<List<InboxEvent>> listener);
}
