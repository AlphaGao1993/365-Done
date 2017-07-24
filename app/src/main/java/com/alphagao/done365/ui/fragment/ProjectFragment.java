package com.alphagao.done365.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.bean.ProjectEvent;
import com.alphagao.done365.greendao.bean.TodoEvent;
import com.alphagao.done365.greendao.gen.ProjectEventDao;
import com.alphagao.done365.holder.ProjectSetTreeViewHolder;
import com.alphagao.done365.holder.ProjectTreeViewHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Alpha on 2017/3/9.
 */

public class ProjectFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.layout)
    LinearLayout layout;
    private ProjectEventDao projectEventDao;
    private List<ProjectEvent> projectEvents;
    private int mode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_empty, container, false);
        unbinder = ButterKnife.bind(this, view);

        Intent intent = getActivity().getIntent();
        mode = intent.getIntExtra("mode", ProjectTreeViewHolder.MODE_SHOW);
        projectEventDao = DaoUtil.daoSession.getProjectEventDao();

        updateTreeData();
        return view;
    }

    private void updateTreeData() {
        updateProjectData();
        layout.removeAllViews();
        TreeNode root = TreeNode.root();
        fillTreeData(root, projectEvents);

        AndroidTreeView treeView = new AndroidTreeView(getActivity(), root);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultViewHolder(ProjectTreeViewHolder.class);
        treeView.setDefaultContainerStyle(R.style.TreeNodeStyleDivider);
        treeView.setDefaultNodeLongClickListener(longClickListener);
        layout.addView(treeView.getView());
    }

    private void updateProjectData() {
        projectEvents = projectEventDao
                .queryBuilder()
                .where(ProjectEventDao.Properties.UpProjectId.eq(0))
                .build()
                .list();
    }

    private void fillTreeData(TreeNode root, List<ProjectEvent> projectEvents) {
        if (projectEvents.size() <= 0) {
            return;
        }
        for (ProjectEvent p : projectEvents) {
            ProjectTreeViewHolder.TreeItem item = new ProjectTreeViewHolder.TreeItem(mode, p);
            TreeNode node = new TreeNode(item);
            root.addChild(node);
            if (hasChild(p)) {
                List<ProjectEvent> events = getChildItems(p);
                fillTreeData(node, events);
            }
        }
    }

    private List<ProjectEvent> getChildItems(ProjectEvent p) {
        return projectEventDao
                .queryBuilder()
                .where(ProjectEventDao.Properties.UpProjectId.eq(p.getId()))
                .build()
                .list();
    }

    private boolean hasChild(ProjectEvent p) {
        return projectEventDao.queryBuilder()
                .where(ProjectEventDao.Properties.UpProjectId.eq(p.getId()))
                .build()
                .list()
                .size() > 0;
    }

    private TreeNode.TreeNodeLongClickListener longClickListener
            = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            if (value instanceof ProjectTreeViewHolder.TreeItem) {
                ProjectTreeViewHolder.TreeItem item = (ProjectTreeViewHolder.TreeItem) value;
                showProjectLongClickEvent(item.event);
            } else if (value instanceof ProjectSetTreeViewHolder.TreeItem) {
                ProjectSetTreeViewHolder.TreeItem item = (ProjectSetTreeViewHolder.TreeItem) value;
                showProjectLongClickEvent(item.event);
            }
            return false;
        }
    };

    private void showProjectLongClickEvent(final ProjectEvent event) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_modify_project_or_set, null);
        final EditText newName = (EditText) view.findViewById(R.id.newProjectName);
        TextView confirmBtn = (TextView) view.findViewById(R.id.confirm);

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(event.getIsDir().equals(ProjectEvent.YES) ? "重命名项目集" : "重命名项目")
                .setView(view)
                .setCancelable(true)
                .create();
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(newName.getText())) {
                    event.setName(newName.getText().toString());
                    projectEventDao.update(event);
                    updateTreeData();
                    dialog.dismiss();
                } else {
                    toast("命名不能为空");
                }
            }
        });
        dialog.show();
    }

    public void onNewMessage(Message msg) {
        switch (msg.what) {
            case ProjectEvent.PROJECT_NEW:
            case ProjectEvent.PROJECT_FOLDER_NEW:
            case AgendaEvent.AGENDA_UPDATE:
            case TodoEvent.TODO_UPDATE:
                updateProjectData();
                updateTreeData();
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
