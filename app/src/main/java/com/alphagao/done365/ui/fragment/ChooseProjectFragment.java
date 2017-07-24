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
import com.alphagao.done365.greendao.bean.ProjectEvent;
import com.alphagao.done365.greendao.gen.ProjectEventDao;
import com.alphagao.done365.holder.ProjectTreeViewHolder;
import com.google.gson.Gson;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Alpha on 2017/4/5.
 */

public class ChooseProjectFragment extends BaseFragment {
    private static final int REQUEST_NEW_PROJ = 0x00036;
    @BindView(R.id.layout)
    LinearLayout layout;
    Unbinder unbinder;
    private ProjectEventDao projectEventDao;
    private List<ProjectEvent> events;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty, container, false);
        unbinder = ButterKnife.bind(this, view);

        initToolbar("关联项目");
        setHasOptionsMenu(true);

        projectEventDao = DaoUtil.daoSession.getProjectEventDao();
        initTreeViewData();

        return view;
    }

    private void initTreeViewData() {
        updateProjectData();
        layout.removeAllViews();
        TreeNode root = TreeNode.root();
        fillProjectTree(root, events);

        AndroidTreeView treeView = new AndroidTreeView(getActivity(), root);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultViewHolder(ProjectTreeViewHolder.class);
        treeView.setDefaultContainerStyle(R.style.TreeNodeStyle);
        treeView.setDefaultNodeClickListener(nodeClickListener);
        layout.addView(treeView.getView());
    }

    private void fillProjectTree(TreeNode root, List<ProjectEvent> events) {
        for (ProjectEvent event : events) {
            ProjectTreeViewHolder.TreeItem item =
                    new ProjectTreeViewHolder.TreeItem(ProjectTreeViewHolder.MODE_SELECT, event);
            TreeNode node = new TreeNode(item);
            root.addChild(node);
            if (hasChild(event)) {
                List<ProjectEvent> childItems = getChildItems(event);
                fillProjectTree(node, childItems);
            }
        }
    }

    private List<ProjectEvent> getChildItems(ProjectEvent event) {
        return projectEventDao
                .queryBuilder()
                .where(ProjectEventDao.Properties.UpProjectId.eq(event.getId()))
                .build()
                .list();
    }

    private boolean hasChild(ProjectEvent event) {
        return projectEventDao
                .queryBuilder()
                .where(ProjectEventDao.Properties.UpProjectId.eq(event.getId()))
                .build()
                .list().size() > 0;
    }

    private void updateProjectData() {
        events = projectEventDao
                .queryBuilder()
                .where(ProjectEventDao.Properties.UpProjectId.eq(0))
                .build()
                .list();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_choose_project, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                returnSelectedProject();
                break;
            case R.id.addProj:
                addNewProj();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewProj() {
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.FRAGMENT_PARAM, NewProjectFragment.class);
        getActivity().startActivityForResult(intent, REQUEST_NEW_PROJ);
    }

    private void returnSelectedProject() {
        if (checkEvent == null) {
            toast("还没有选择项目哦~");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("project", new Gson().toJson(checkEvent));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private static ImageView lastCheckedView;

    private ProjectEvent checkEvent;
    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            ProjectTreeViewHolder.TreeItem item = (ProjectTreeViewHolder.TreeItem) value;
            if (item.event.getIsDir() == ProjectEvent.NO) {
                if (lastCheckedView != null) {
                    lastCheckedView.setImageDrawable(null);
                }
                ImageView imageView = (ImageView) node.getViewHolder().getView()
                        .findViewById(R.id.projectChecked);
                checkEvent = item.event;
                imageView.setImageResource(R.mipmap.ic_checked);
                lastCheckedView = imageView;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_NEW_PROJ) {
                initTreeViewData();
            }
        }
    }

    @Override
    public void onNewMessage(Message msg) {
        switch (msg.what) {
            case ProjectEvent.PROJECT_NEW:
                initTreeViewData();
                break;
            default:
                break;
        }
    }
}
