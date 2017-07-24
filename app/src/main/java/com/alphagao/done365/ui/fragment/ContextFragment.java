package com.alphagao.done365.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.gen.AgendaEventDao;
import com.alphagao.done365.greendao.gen.TodoEventDao;
import com.alphagao.done365.ui.activity.NewContextActivity;
import com.alphagao.done365.ui.activity.SingleFragmentActivity;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.bean.ContextEvent;
import com.alphagao.done365.greendao.bean.TodoEvent;
import com.alphagao.done365.greendao.gen.ContextEventDao;
import com.alphagao.done365.holder.ContextTreeViewHolder;
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


public class ContextFragment extends BaseFragment {


    private static final int REQUEST_ADD_CONTEXT = 0x00321;
    @BindView(R.id.layout)
    LinearLayout layout;
    Unbinder unbinder;
    private ContextEventDao contextEventDao;
    private List<ContextEvent> contextEvents;
    private boolean isSelectMode;
    private AgendaEventDao agendaEventDao;
    private TodoEventDao todoEventDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty, container, false);
        unbinder = ButterKnife.bind(this, view);
        isSelectMode = getActivity() instanceof SingleFragmentActivity;

        if (isSelectMode) {
            initToolbar("选择情境");
            setHasOptionsMenu(true);
        }

        initDataDao();

        updateContextData();
        TreeNode root = TreeNode.root();
        fillTreeData(root, contextEvents);

        AndroidTreeView treeView = new AndroidTreeView(getActivity(), root);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultViewHolder(ContextTreeViewHolder.class);
        treeView.setDefaultContainerStyle(R.style.TreeNodeStyle);
        treeView.setDefaultNodeClickListener(nodeClickListener);
        treeView.setDefaultNodeLongClickListener(nodeLongClickListener);

        layout.addView(treeView.getView());
        return view;
    }

    private void initDataDao() {
        contextEventDao = DaoUtil.daoSession.getContextEventDao();
        agendaEventDao = DaoUtil.daoSession.getAgendaEventDao();
        todoEventDao = DaoUtil.daoSession.getTodoEventDao();
    }

    private void updateContextData() {
        contextEvents = contextEventDao
                .queryBuilder()
                .where(ContextEventDao.Properties.UpContextId.eq(0))
                .build()
                .list();
    }

    /**
     * 递归遍历数据添加至树型结构
     *
     * @param root 父节点
     * @param list 父节点下一层的所有子节点
     * @return 添加完子节点的父节点
     */
    private TreeNode fillTreeData(TreeNode root, List<ContextEvent> list) {
        for (ContextEvent event : list) {
            ContextTreeViewHolder.TreeItem item =
                    new ContextTreeViewHolder.TreeItem(isSelectMode ?
                            ContextTreeViewHolder.MODE_SELECT : ContextTreeViewHolder.MODE_SHOW
                            , event);
            TreeNode node = new TreeNode(item);
            root.addChild(node);
            if (hasChildItem(event)) {
                List<ContextEvent> childItems = getChildItems(event);
                fillTreeData(node, childItems);
            }
        }
        return root;
    }

    /**
     * 判断该条数据是否有子节点
     *
     * @param event
     * @return
     */
    private boolean hasChildItem(ContextEvent event) {
        List<ContextEvent> list = contextEventDao
                .queryBuilder()
                .where(ContextEventDao.Properties.UpContextId.eq(event.getId()))
                .build()
                .list();
        return list.size() > 0;
    }

    //返回该条数据下的子节点
    private List<ContextEvent> getChildItems(ContextEvent event) {
        return contextEventDao
                .queryBuilder()
                .where(ContextEventDao.Properties.UpContextId.eq(event.getId()))
                .build()
                .list();
    }

    @Override
    public void onNewMessage(Message msg) {
        switch (msg.what) {
            case ContextEvent.CONTEXT_UPDATE:
            case AgendaEvent.AGENDA_UPDATE:
            case TodoEvent.TODO_UPDATE:
                updateTreeData();
                break;
            case ContextEvent.CONTEXT_ADD:
                requestNewContext();
                break;
            default:
                break;
        }
    }

    private void updateTreeData() {
        updateContextData();
        if (layout != null) {
            layout.removeAllViews();
            TreeNode newroot = TreeNode.root();
            fillTreeData(newroot, contextEvents);
            AndroidTreeView newTree = new AndroidTreeView(getActivity(), newroot);
            newTree.setDefaultAnimation(true);
            newTree.setDefaultViewHolder(ContextTreeViewHolder.class);
            newTree.setDefaultContainerStyle(R.style.TreeNodeStyle);
            newTree.setDefaultNodeClickListener(nodeClickListener);
            newTree.setDefaultNodeLongClickListener(nodeLongClickListener);
            layout.addView(newTree.getView());
        }
    }

    private ContextEvent checkedEvent;
    private static ImageView lastCheckedView;
    private TreeNode.TreeNodeClickListener nodeClickListener =
            new TreeNode.TreeNodeClickListener() {
                @Override
                public void onClick(TreeNode node, Object value) {
                    ContextTreeViewHolder.TreeItem item = (ContextTreeViewHolder.TreeItem) value;
                    if (item.mode == ContextTreeViewHolder.MODE_SELECT) {
                        if (lastCheckedView != null) {
                            lastCheckedView.setImageDrawable(null);
                        }
                        ImageView checkedIcon = (ImageView) node.getViewHolder().getView()
                                .findViewById(R.id.checkedIcon);
                        checkedIcon.setImageResource(R.mipmap.ic_checked);
                        checkedEvent = item.event;
                        lastCheckedView = checkedIcon;
                    }
                }
            };

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener =
            new TreeNode.TreeNodeLongClickListener() {
                @Override
                public boolean onLongClick(TreeNode node, Object value) {
                    tryToDeleteContext((ContextTreeViewHolder.TreeItem) value);
                    return true;
                }
            };

    private void tryToDeleteContext(ContextTreeViewHolder.TreeItem value) {
        final ContextTreeViewHolder.TreeItem item = value;
        if (item.mode == ContextTreeViewHolder.MODE_SHOW) {
            if (!hasRelativeEvent(item.event)) {
                confirmDeleteContext(item);
            } else {
                toast(getString(R.string.delete_context_error));
            }
        }
    }

    private void confirmDeleteContext(final ContextTreeViewHolder.TreeItem item) {
        new AlertDialog.Builder(getContext())
                .setMessage(getString(R.string.sure_delete_context))
                .setPositiveButton(getString(R.string.positive_sure),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteContext(item.event);
                            }
                        })
                .setCancelable(true)
                .show();
    }

    private void deleteContext(ContextEvent event) {
        contextEventDao.delete(event);
        updateContextData();
        updateTreeData();
    }

    private boolean hasRelativeEvent(ContextEvent event) {
        if (hasRelativeAgenda(event) || hasRelativeTodo(event) || hasChild(event)) {
            return true;
        }
        return false;
    }

    private boolean hasChild(ContextEvent event) {
        return contextEventDao.queryBuilder()
                .where(ContextEventDao.Properties.UpContextId.eq(event.getId()))
                .build().list().size() > 0;
    }

    private boolean hasRelativeTodo(ContextEvent event) {
        return todoEventDao.queryBuilder()
                .where(TodoEventDao.Properties.ContextId.eq(event.getId()))
                .build().list().size() > 0;
    }

    private boolean hasRelativeAgenda(ContextEvent event) {
        return agendaEventDao.queryBuilder()
                .where(AgendaEventDao.Properties.ContextId.eq(event.getId()))
                .build().list().size() > 0;
    }

    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isSelectMode) {
            inflater.inflate(R.menu.choose_context, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choose_done:
                returnSelectedCOntext();
                break;
            case R.id.add_context:
                requestNewContext();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestNewContext() {
        Intent intent = new Intent(getActivity(), NewContextActivity.class);
        startActivityForResult(intent, REQUEST_ADD_CONTEXT);
    }

    private void returnSelectedCOntext() {
        if (checkedEvent == null) {
            toast("还没有选择情境哦~");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("context", new Gson().toJson(checkedEvent));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
