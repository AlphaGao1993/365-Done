package com.alphagao.done365.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.ProjectEvent;
import com.alphagao.done365.greendao.gen.ProjectEventDao;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Alpha on 2017/4/4.
 */

public class ProjectSetTreeViewHolder extends
        TreeNode.BaseNodeViewHolder<ProjectSetTreeViewHolder.TreeItem> {


    private ProjectEventDao projectEventDao;
    private ProjectEvent event;
    private ImageView icon;

    public ProjectSetTreeViewHolder(Context context) {
        super(context);
        projectEventDao = DaoUtil.daoSession.getProjectEventDao();
    }

    @Override
    public View createNodeView(TreeNode node, TreeItem value) {
        event = value.event;
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_treeview_pro_set, null, false);
        icon = (ImageView) view.findViewById(R.id.proIcon);
        if (hasChildFolder(event))
            icon.setImageResource(R.mipmap.ic_arrow_right_black_24dp);
        TextView content = (TextView) view.findViewById(R.id.contentName);
        content.setText(event.getName());
        return view;
    }

    private boolean hasChildFolder(ProjectEvent event) {
        return projectEventDao
                .queryBuilder()
                .where(projectEventDao.queryBuilder()
                        .and(ProjectEventDao.Properties.UpProjectId.eq(event.getId()),
                                ProjectEventDao.Properties.IsDir.eq(ProjectEvent.YES)))
                .build()
                .list().size() > 0;
    }

    @Override
    public void toggle(boolean active) {
        if (hasChildFolder(event)) {
            icon.setImageResource(active
                    ? R.mipmap.ic_arrow_down_black_24dp : R.mipmap.ic_arrow_right_black_24dp);
        }
    }

    public static class TreeItem {
        public int icon;
        public ProjectEvent event;

        public TreeItem(int icon, ProjectEvent event) {
            this.icon = icon;
            this.event = event;
        }
    }
}
