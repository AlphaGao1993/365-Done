package com.alphagao.done365.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.ProjectEvent;
import com.alphagao.done365.greendao.gen.ProjectEventDao;
import com.alphagao.done365.holder.ProjectTreeViewHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Alpha on 2017/4/4.
 */

public class NewProjectFragment extends BaseFragment {
    @BindView(R.id.project_name)
    EditText projectName;
    @BindView(R.id.has_Parent_Folder)
    Switch hasParentFolder;
    @BindView(R.id.projectTreeLayout)
    LinearLayout projectTreeLayout;
    @BindView(R.id.addNewProjectFolder)
    Button addNewProject;
    Unbinder unbinder;
    private ProjectEventDao projectEventDao;
    private List<ProjectEvent> projectEvents;
    private ProjectEvent checkedEvent;
    private static ImageView lastCheckedView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_project, container, false);
        unbinder = ButterKnife.bind(this, view);
        initToolbar("新建项目");
        setHasOptionsMenu(true);

        projectEventDao = DaoUtil.daoSession.getProjectEventDao();
        initTreeViewData();

        hasParentFolder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                projectTreeLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        return view;
    }

    private void initTreeViewData() {
        projectTreeLayout.removeAllViews();

        updateRootProjectFolder();

        TreeNode root = TreeNode.root();
        fillTreeNode(root, projectEvents);

        AndroidTreeView treeView = new AndroidTreeView(getActivity(), root);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultViewHolder(ProjectTreeViewHolder.class);
        treeView.setDefaultContainerStyle(R.style.TreeNodeStyle);
        treeView.setDefaultNodeClickListener(nodeClickListener);
        projectTreeLayout.addView(treeView.getView());
    }

    private void fillTreeNode(TreeNode root, List<ProjectEvent> projectEvents) {
        for (ProjectEvent p : projectEvents) {
            ProjectTreeViewHolder.TreeItem item =
                    new ProjectTreeViewHolder.TreeItem(ProjectTreeViewHolder.MODE_SELECT, p);
            TreeNode node = new TreeNode(item);
            root.addChild(node);
            List<ProjectEvent> events = projectEventDao.queryBuilder()
                    .where(projectEventDao.queryBuilder()
                            .and((ProjectEventDao.Properties.UpProjectId.eq(p.getId())),
                                    ProjectEventDao.Properties.IsDir.eq(ProjectEvent.YES)))
                    .build()
                    .list();
            if (events.size() > 0) {
                fillTreeNode(node, events);
            }
        }
    }

    private void updateRootProjectFolder() {
        projectEvents = projectEventDao
                .queryBuilder()
                .where(projectEventDao.queryBuilder()
                        .and(ProjectEventDao.Properties.UpProjectId.eq(0),
                                ProjectEventDao.Properties.IsDir.eq(ProjectEvent.YES)))
                .build()
                .list();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.addNewProjectFolder)
    public void onViewClicked() {
        saveProject();
    }

    private void saveProject() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String name = projectName.getText().toString();
        try {
            if (!TextUtils.isEmpty(name)) {
                ProjectEvent event = new ProjectEvent();
                event.setName(name);
                event.setUpProjectId(hasParentFolder.isChecked() ? checkedEvent.getId() : 0);
                event.setIsDir(ProjectEvent.NO);
                event.setCompletion(0f);
                event.setRelatedMsg("");
                event.setCreateDate(formater.format(new Date()));

                projectEventDao.insert(event);
                toast("项目新建成功~");
                //处于同一个 Activity 壳之内的 Fragment 无法通过 ActivityResult 获取结果
                Message msg = Message.obtain();
                msg.what = ProjectEvent.PROJECT_NEW;
                messageManager.publishMessage(msg);

                //双管齐下，保证正常调用有效
                getActivity().setResult(getActivity().RESULT_OK);
                getActivity().finish();
            } else {
                toast("项目名称不能为空哦~");
                return;
            }
        } catch (Exception e) {
            toast("项目新建失败，请稍后再试~");
        }
    }

    private TreeNode.TreeNodeClickListener nodeClickListener
            = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            if (lastCheckedView != null) {
                lastCheckedView.setImageDrawable(null);
            }
            ProjectTreeViewHolder.TreeItem item = (ProjectTreeViewHolder.TreeItem) value;
            ImageView imageView = (ImageView) node.getViewHolder().getView()
                    .findViewById(R.id.checkedFolder);
            imageView.setImageResource(R.mipmap.ic_checked);
            checkedEvent = item.event;
            lastCheckedView = imageView;
        }
    };

    @Override
    public void onNewMessage(Message msg) {
        switch (msg.what) {
            case ProjectEvent.PROJECT_FOLDER_NEW:
                initTreeViewData();
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            onDestroy();
            getActivity().finish();
        }
    }
}
