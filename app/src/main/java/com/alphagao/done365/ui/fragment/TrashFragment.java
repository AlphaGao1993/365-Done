package com.alphagao.done365.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.adapter.TrashEventAdapter;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.ui.presenter.InboxPresenter;
import com.alphagao.done365.ui.view.InboxViewInterface;
import com.alphagao.done365.widget.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Alpha on 2017/3/5.
 */

public class TrashFragment extends BaseFragment implements InboxViewInterface {

    @BindView(R.id.trash_view)
    SwipeRecyclerView trashView;
    Unbinder unbinder;
    @BindView(R.id.clear_trash)
    TextView clearTrash;
    @BindView(R.id.revoke_inbox)
    TextView revokeInbox;
    private List<InboxEvent> events = new ArrayList<>();
    private TrashEventAdapter adapter;
    private InboxPresenter mInboxPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trash, container, false);
        unbinder = ButterKnife.bind(this, view);
        mInboxPresenter = new InboxPresenter(this);
        initView();
        updateInboxData();
        return view;
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        trashView.setLayoutManager(manager);
    }

    private void updateInboxData() {
        mInboxPresenter.loadDeletedInboxFromDB();
    }

    @Override
    public void showInbox(List<InboxEvent> inboxEvents) {
        //垃圾箱不需要显示没有被删除的条目，该方法也不会被调用
    }

    @Override
    public void showDeletedInbox(List<InboxEvent> inboxEvents) {
        events = inboxEvents;
        if (adapter != null) {
            adapter.setDeletedInboxs(events);
        } else {
            adapter = new TrashEventAdapter(events, null);
            trashView.setAdapter(adapter);
        }
    }

    private void addDeletedInbox(Message msg) {
        InboxEvent event = (InboxEvent) msg.obj;
        events.add(0, event);
        if (adapter != null) {
            adapter.notifyItemInserted(0);
            adapter.notifyItemRangeChanged(0, events.size());
        } else {
            adapter = new TrashEventAdapter(events, null);
            trashView.setAdapter(adapter);
        }
    }

    @OnClick({R.id.clear_trash, R.id.revoke_inbox})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.clear_trash:
                showClearTrashDialog();
                break;
            case R.id.revoke_inbox:
                revokeSelectedItems();
                break;
            default:
                break;
        }
    }

    private void revokeSelectedItems() {
        List<InboxEvent> inboxs = adapter.getRevokeInboxs();
        for (InboxEvent e : inboxs) {
            e.setDeleted(InboxEvent.NO);
        }
        mInboxPresenter.updateInboxInTx(events);

        for (InboxEvent event : inboxs) {
            events.remove(event);
        }

        adapter.notifyDataSetChanged();

        Message msg = Message.obtain();
        msg.what = InboxEvent.INBOX_REVOKE;
        messageManager.publishMessage(msg);
        toast("已成功恢复~");
    }

    private void showClearTrashDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("清空垃圾箱")
                .setMessage("将从数据库删除所有已删除记录，该操作不能撤销，确定要删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearAllTrash();
                    }
                })
                .setNegativeButton("我再想想", null)
                .show();
    }

    private void clearAllTrash() {
        mInboxPresenter.deletedInboxInTx(events);
        events.clear();
        adapter.notifyDataSetChanged();
        toast("已清空垃圾箱~");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onNewMessage(Message msg) {
        switch (msg.what) {
            case InboxEvent.INBOX_DELETED:
                addDeletedInbox(msg);
                break;
            default:
                break;
        }
    }
}
