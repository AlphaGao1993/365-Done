package com.alphagao.done365.ui.presenter;

import com.alphagao.done365.ui.view.DataListener;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.ui.view.InboxViewInterface;
import com.alphagao.done365.ui.model.InboxModel;
import com.alphagao.done365.ui.model.InboxModelDBImpl;

import java.util.List;

/**
 * Created by Alpha on 2017/4/23.
 */

public class InboxPresenter {
    InboxViewInterface mInboxView;
    InboxModel mInboxModel;

    public InboxPresenter(InboxViewInterface mInboxView) {
        this.mInboxView = mInboxView;
        mInboxModel = new InboxModelDBImpl();
    }

    public void fetchInboxs() {

    }

    public Long insertInbox(InboxEvent inbox) {
        return mInboxModel.insertInbox(inbox);
    }

    public void updateInboxInTx(List<InboxEvent> inboxEvents) {
        mInboxModel.updateInboxInTx(inboxEvents);
    }

    public void deleteInbox(InboxEvent inbox) {
        mInboxModel.deleteInbox(inbox);
    }

    public void deletedInboxInTx(List<InboxEvent> inboxEvents) {
        mInboxModel.deleteInboxInTx(inboxEvents);
    }

    public void loadInboxFromDB() {
        mInboxModel.loadInboxByCreateTime(new DataListener<List<InboxEvent>>() {
            @Override
            public void onComplete(List<InboxEvent> result) {
                mInboxView.showInbox(result);
            }
        });
    }

    public void loadDeletedInboxFromDB() {
        mInboxModel.loadDeletedInboxByDeletedTime(new DataListener<List<InboxEvent>>() {
            @Override
            public void onComplete(List<InboxEvent> result) {
                mInboxView.showDeletedInbox(result);
            }
        });
    }
}
