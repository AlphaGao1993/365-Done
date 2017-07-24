package com.alphagao.done365.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alphagao.done365.R;
import com.alphagao.done365.ui.activity.SingleFragmentActivity;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.greendao.bean.MemoEvent;
import com.alphagao.done365.greendao.gen.InboxEventDao;
import com.alphagao.done365.greendao.gen.MemoEventDao;
import com.alphagao.done365.holder.MemoFolderTreeViewHolder;
import com.google.gson.Gson;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Alpha on 2017/4/2.
 */

public class ChooseMemoFolderFragment extends BaseFragment {
    private static final String TAG = "ChooseMemoFolderFragmen";
    private static final int NEW_MEMO_FOLDER = 209;

    Unbinder unbinder;
    @BindView(R.id.layout)
    LinearLayout layout;
    private MemoEventDao memoEventDao;
    private List<MemoEvent> memoEvents;
    private InboxEvent inbox;
    private static ImageView lastCheckedView;
    private MemoEvent checkedEvent;
    private InboxEventDao inboxEventDao;
    private MemoEvent rootEvent;
    private MemoFolderTreeViewHolder.TreeItem rootItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty, null, false);
        unbinder = ButterKnife.bind(this, view);
        initToolbar(R.string.memo_folder_label);
        setHasOptionsMenu(true);

        String inboxString = getActivity().getIntent().getStringExtra("inbox");
        inbox = new Gson().fromJson(inboxString, InboxEvent.class);
        memoEventDao = DaoUtil.daoSession.getMemoEventDao();
        inboxEventDao = DaoUtil.daoSession.getInboxEventDao();

        initTreeViewData();
        return view;
    }

    private void initTreeViewData() {
        UpdateMemoData();
        TreeNode root = TreeNode.root();
        rootEvent = new MemoEvent(0L, "根文件夹", 0L, MemoEvent.YES, "", MemoEvent.NO, null);
        rootItem = new MemoFolderTreeViewHolder.TreeItem(0, rootEvent);
        TreeNode node = new TreeNode(rootItem);
        fillTreeData(node, memoEvents);
        root.addChild(node);

        AndroidTreeView treeView = new AndroidTreeView(getActivity(), root);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultViewHolder(MemoFolderTreeViewHolder.class);
        treeView.setDefaultContainerStyle(R.style.TreeNodeStyle);
        treeView.setDefaultNodeClickListener(nodeClickListener);
        treeView.expandLevel(1);
        layout.addView(treeView.getView());
    }

    private void UpdateMemoData() {
        memoEvents = memoEventDao
                .queryBuilder()
                .where(memoEventDao.queryBuilder().and(MemoEventDao.Properties.DirID.eq(0),
                        MemoEventDao.Properties.IsDir.eq(MemoEvent.YES)))
                .build()
                .list();
    }


    private void fillTreeData(TreeNode root, List<MemoEvent> memoEvents) {
        for (MemoEvent event : memoEvents) {
            MemoFolderTreeViewHolder.TreeItem item
                    = new MemoFolderTreeViewHolder.TreeItem(0, event);
            TreeNode node = new TreeNode(item);
            root.addChild(node);
            if (hasChild(event)) {
                List<MemoEvent> childItems = getChildItems(event);
                fillTreeData(node, childItems);
            }
        }
    }

    private List<MemoEvent> getChildItems(MemoEvent event) {
        return memoEventDao
                .queryBuilder()
                .where(memoEventDao.queryBuilder()
                        .and(MemoEventDao.Properties.DirID.eq(event.getId()),
                                MemoEventDao.Properties.IsDir.eq(MemoEvent.YES)))
                .build()
                .list();
    }

    private boolean hasChild(MemoEvent event) {
        List<MemoEvent> list = memoEventDao
                .queryBuilder()
                .where(memoEventDao.queryBuilder()
                        .and(MemoEventDao.Properties.DirID.eq(event.getId()),
                                MemoEventDao.Properties.IsDir.eq(MemoEvent.YES)))
                .build()
                .list();
        return list.size() > 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private TreeNode.TreeNodeClickListener nodeClickListener
            = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            if (lastCheckedView != null) {
                lastCheckedView.setImageDrawable(null);
            }
            MemoFolderTreeViewHolder.TreeItem item = (MemoFolderTreeViewHolder.TreeItem) value;
            ImageView imageView = (ImageView) node.getViewHolder()
                    .getView().findViewById(R.id.folderChecked);
            imageView.setImageResource(R.mipmap.ic_checked);
            checkedEvent = item.event;
            lastCheckedView = imageView;
        }
    };

    private void updateTreeView() {
        UpdateMemoData();
        if (layout != null) {
            layout.removeAllViews();
            TreeNode newroot = TreeNode.root();
            TreeNode node = new TreeNode(rootItem);
            newroot.addChild(node);
            fillTreeData(node, memoEvents);
            AndroidTreeView newTree = new AndroidTreeView(getActivity(), newroot);
            newTree.setDefaultAnimation(true);
            newTree.setDefaultViewHolder(MemoFolderTreeViewHolder.class);
            newTree.setDefaultContainerStyle(R.style.TreeNodeStyle);
            newTree.setDefaultNodeClickListener(nodeClickListener);
            newTree.expandLevel(1);
            layout.addView(newTree.getView());
        }
    }

    @Override
    public void onNewMessage(Message msg) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_choose_memo_folder, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNeWFolder:
                Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                intent.putExtra(SingleFragmentActivity.FRAGMENT_PARAM,
                        NewMemoryFolderFragment.class);
                startActivityForResult(intent, NEW_MEMO_FOLDER);
                break;
            case R.id.addNewMemo:
                saveMemo();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMemo() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (checkedEvent != null) {
                memoEventDao.insert(
                        new MemoEvent(null,
                                inbox.getContent(),
                                checkedEvent.getId(),
                                MemoEvent.NO,
                                formater.format(new Date()),
                                MemoEvent.NO,
                                null));
                publishInboxRemoved(inbox);
                toast("成功添加到备忘录~");
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else {
                toast("没有选择文件夹哦~");
            }
        } catch (Exception e) {
            toast("添加失败~");
        }
    }

    private void publishInboxRemoved(InboxEvent inbox) {
        Message msg = Message.obtain();
        msg.what = InboxEvent.INBOX_REMOVED;
        msg.obj = inbox;
        messageManager.publishMessage(msg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Message msg = Message.obtain();
            msg.what = MemoEvent.MEMO_UPDATE;
            messageManager.publishMessage(msg);
            updateTreeView();
        }
    }
}
