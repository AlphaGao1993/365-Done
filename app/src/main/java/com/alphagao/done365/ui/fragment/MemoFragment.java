package com.alphagao.done365.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.MemoEvent;
import com.alphagao.done365.greendao.gen.MemoEventDao;
import com.alphagao.done365.holder.MemoTreeViewHolder;
import com.alphagao.done365.ui.activity.SingleFragmentActivity;
import com.google.gson.Gson;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Alpha on 2017/3/2.
 */

public class MemoFragment extends BaseFragment {


    @BindView(R.id.layout)
    LinearLayout layout;
    Unbinder unbinder;
    private MemoEventDao memoEventDao;
    private List<MemoEvent> memoEvents;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty, container, false);
        unbinder = ButterKnife.bind(this, view);

        memoEventDao = DaoUtil.daoSession.getMemoEventDao();
        initTreeViewData();
        return view;
    }

    private void initTreeViewData() {
        layout.removeAllViews();
        TreeNode root = TreeNode.root();
        getMemoData();
        fillTreeData(root, memoEvents);
        AndroidTreeView treeView = new AndroidTreeView(getActivity(), root);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultViewHolder(MemoTreeViewHolder.class);
        treeView.setDefaultContainerStyle(R.style.TreeNodeStyleDivider);
        treeView.setDefaultNodeClickListener(listener);
        layout.addView(treeView.getView());
    }

    private void fillTreeData(TreeNode root, List<MemoEvent> memoEvents) {
        if (memoEvents.size() <= 0) {
            return;
        }
        for (MemoEvent memo : memoEvents) {
            MemoTreeViewHolder.TreeItem item = new MemoTreeViewHolder.TreeItem(0, memo);
            TreeNode node = new TreeNode(item);
            root.addChild(node);
            if (memo.getIsDir() == MemoEvent.YES && hasChild(memo)) {
                List<MemoEvent> childItems = getchildItems(memo);
                fillTreeData(node, childItems);
            }
        }
    }

    private List<MemoEvent> getchildItems(MemoEvent memo) {
        return memoEventDao
                .queryBuilder()
                .where(MemoEventDao.Properties.DirID.eq(memo.getId()))
                .orderDesc(MemoEventDao.Properties.IsDir)
                .build()
                .list();
    }

    private boolean hasChild(MemoEvent memo) {
        return memoEventDao
                .queryBuilder()
                .where(MemoEventDao.Properties.DirID.eq(memo.getId()))
                .build()
                .list().size() > 0;
    }

    private void getMemoData() {
        memoEvents = memoEventDao
                .queryBuilder()
                .where(MemoEventDao.Properties.DirID.eq(0))
                .orderDesc(MemoEventDao.Properties.IsDir)
                .build().list();
    }

    private TreeNode.TreeNodeClickListener listener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            MemoTreeViewHolder.TreeItem item = (MemoTreeViewHolder.TreeItem) node.getValue();
            MemoEvent event = item.event;
            if (event.getIsDir().equals(MemoEvent.NO)) {
                Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                intent.putExtra(SingleFragmentActivity.FRAGMENT_PARAM, MemoDetailFragment.class);
                intent.putExtra("memo", new Gson().toJson(event));
                startActivity(intent);
            }
        }
    };

    public void onNewMessage(Message msg) {
        switch (msg.what) {
            case MemoEvent.MEMO_UPDATE:
                initTreeViewData();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
