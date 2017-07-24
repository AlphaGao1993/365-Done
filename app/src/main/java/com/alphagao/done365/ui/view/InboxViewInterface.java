package com.alphagao.done365.ui.view;

import com.alphagao.done365.greendao.bean.InboxEvent;

import java.util.List;

/**
 * Created by Alpha on 2017/4/23.
 */

public interface InboxViewInterface {

    void showInbox(List<InboxEvent> inboxEvents);

    void showDeletedInbox(List<InboxEvent> inboxEvents);
}
