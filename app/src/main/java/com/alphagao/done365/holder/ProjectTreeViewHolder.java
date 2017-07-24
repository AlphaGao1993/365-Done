package com.alphagao.done365.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.bean.ProjectEvent;
import com.alphagao.done365.greendao.bean.TodoEvent;
import com.alphagao.done365.greendao.gen.AgendaEventDao;
import com.alphagao.done365.greendao.gen.ProjectEventDao;
import com.alphagao.done365.greendao.gen.TodoEventDao;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.unnamed.b.atv.model.TreeNode;

import java.util.List;

/**
 * Created by Alpha on 2017/4/4.
 */

public class ProjectTreeViewHolder extends
        TreeNode.BaseNodeViewHolder<ProjectTreeViewHolder.TreeItem> {
    public static final int MODE_SHOW = 101;
    public static final int MODE_SELECT = 102;

    private ProjectEventDao projectEventDao;
    private ProjectEvent event;
    private ImageView icon;
    private AgendaEventDao agendaEventDao;
    private TodoEventDao todoEventDao;
    private int mode = MODE_SHOW;

    public ProjectTreeViewHolder(Context context) {
        super(context);
        agendaEventDao = DaoUtil.daoSession.getAgendaEventDao();
        todoEventDao = DaoUtil.daoSession.getTodoEventDao();
        projectEventDao = DaoUtil.daoSession.getProjectEventDao();
    }

    @Override
    public View createNodeView(TreeNode node, TreeItem value) {
        mode = value.mode;
        event = value.event;
        View view;
        if (event.getIsDir() != ProjectEvent.YES) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_treeview_pro, null, false);
            if (mode == MODE_SHOW) {
                DonutProgress progress = (DonutProgress) view.findViewById(R.id.progress);
                progress.setVisibility(View.VISIBLE);
                progress.setProgress(getProjectProgress(event));
            }
            icon = (ImageView) view.findViewById(R.id.proIcon);
            icon.setImageResource(R.mipmap.ic_project);

        } else {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_treeview_pro_set, null, false);
            icon = (ImageView) view.findViewById(R.id.proIcon);
            icon.setImageResource(R.mipmap.ic_arrow_right_black_24dp);
        }

        TextView content = (TextView) view.findViewById(R.id.contentName);
        content.setText(event.getName());
        return view;
    }

    private float getProjectProgress(ProjectEvent event) {
        int allEventCount = getAllEventCount(event);
        int allFinishedEventCount = getAllFinishEventCount(event);
        if (allEventCount == 0) {
            return 0;
        } else {
            float result = allFinishedEventCount * 1000 / allEventCount;
            return result / 10;
        }
    }

    private int getAllFinishEventCount(ProjectEvent event) {
        return getCurrentEventCount(event, true) + getChildEventCount(event, true);
    }

    private int getAllEventCount(ProjectEvent event) {
        return getCurrentEventCount(event, false) + getChildEventCount(event, false);
    }

    private int getChildEventCount(ProjectEvent event, boolean finished) {
        int count = 0;
        List<ProjectEvent> events = projectEventDao
                .queryBuilder()
                .where(ProjectEventDao.Properties.UpProjectId.eq(event.getId()))
                .build().list();
        if (events.size() > 0) {
            for (ProjectEvent p : events) {
                count += getCurrentEventCount(p, finished);
                count += getChildEventCount(p, finished);
            }
        }
        return count;
    }

    private int getCurrentEventCount(ProjectEvent event, boolean finished) {
        return getAgendaCount(event, finished) + getTodoCount(event, finished);
    }

    private int getTodoCount(ProjectEvent event, boolean finished) {
        List<TodoEvent> todoEvents;
        if (finished) {
            todoEvents = todoEventDao
                    .queryBuilder()
                    .where(todoEventDao.queryBuilder()
                            .and(TodoEventDao.Properties.ProjectId.eq(event.getId()),
                                    TodoEventDao.Properties.Finished.eq(TodoEvent.YES)))
                    .build()
                    .list();
        } else {
            todoEvents = todoEventDao.queryBuilder()
                    .where(TodoEventDao.Properties.ProjectId.eq(event.getId()))
                    .build()
                    .list();
        }
        return todoEvents.size();
    }

    private int getAgendaCount(ProjectEvent event, boolean finished) {
        List<AgendaEvent> agendaEvents;
        if (finished) {
            agendaEvents = agendaEventDao
                    .queryBuilder()
                    .where(agendaEventDao.queryBuilder()
                            .and(AgendaEventDao.Properties.ProjectId.eq(event.getId()),
                                    AgendaEventDao.Properties.Finished.eq(TodoEvent.YES)))
                    .build()
                    .list();
        } else {
            agendaEvents = agendaEventDao
                    .queryBuilder()
                    .where(AgendaEventDao.Properties.ProjectId.eq(event.getId()))
                    .build()
                    .list();
        }
        return agendaEvents.size();
    }

    @Override
    public void toggle(boolean active) {
        if (event.getIsDir() == ProjectEvent.YES) {
            icon.setImageResource(active
                    ? R.mipmap.ic_arrow_down_black_24dp : R.mipmap.ic_arrow_right_black_24dp);
        }
    }

    public static class TreeItem {
        public int mode;
        public ProjectEvent event;

        public TreeItem(int mode, ProjectEvent event) {
            this.mode = mode;
            this.event = event;
        }
    }
}
