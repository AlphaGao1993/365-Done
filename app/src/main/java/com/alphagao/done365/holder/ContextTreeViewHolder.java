package com.alphagao.done365.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.bean.ContextEvent;
import com.alphagao.done365.greendao.bean.TodoEvent;
import com.alphagao.done365.greendao.gen.AgendaEventDao;
import com.alphagao.done365.greendao.gen.ContextEventDao;
import com.alphagao.done365.greendao.gen.TodoEventDao;
import com.unnamed.b.atv.model.TreeNode;

import java.util.List;

/**
 * Created by Alpha on 2017/3/19.
 */

public class ContextTreeViewHolder extends
        TreeNode.BaseNodeViewHolder<ContextTreeViewHolder.TreeItem> {

    public static final int MODE_SHOW = 32021;
    public static final int MODE_SELECT = 32022;

    private ImageView arrow;
    private ContextEvent event;
    private final ContextEventDao contextEventDao;
    private int agendaCount;
    private AgendaEventDao agendaEventDao;
    private final TodoEventDao todoEventDao;
    private int todoCount;

    public ContextTreeViewHolder(Context context) {
        super(context);
        contextEventDao = DaoUtil.daoSession.getContextEventDao();
        agendaEventDao = DaoUtil.daoSession.getAgendaEventDao();
        todoEventDao = DaoUtil.daoSession.getTodoEventDao();
    }

    @Override
    public View createNodeView(final TreeNode node, final TreeItem value) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_treeview_context, null);
        event = value.event;
        final TextView content = (TextView) view.findViewById(R.id.contextContent);
        arrow = (ImageView) view.findViewById(R.id.contextArrow);
        TextView childCount = (TextView) view.findViewById(R.id.context_child_count);
        agendaCount = 0;
        todoCount = 0;
        getEventCount(value.event);
        childCount.setText(agendaCount + "/" + todoCount);
        if (hasChildItem(event)) {
            arrow.setImageResource(R.mipmap.ic_arrow_right_black_24dp);
        }
        content.setText(value.event.getName());
/*        if (value.mode == MODE_SELECT) {
            ImageView checkedIcon = (ImageView) view.findViewById(R.id.checkedFolder);
            checkedIcon.setImageResource(R.mipmap.ic_checked);
        }*/
        return view;
    }

    /**
     * 计算当前情境以及所有子情境关联的事务数量
     *
     * @param event
     */
    private void getEventCount(ContextEvent event) {
        //根情境下的事务数量
        getEventAgendaAndTodoCount(event);
        List<ContextEvent> events = contextEventDao
                .queryBuilder()
                .where(ContextEventDao.Properties.UpContextId.eq(event.getId()))
                .build()
                .list();
        if (events.size() > 0) {
            for (ContextEvent c : events) {
                getEventCount(c);
            }
        }
    }

    /**
     * 计算当前情境下关联的事务数量，不计算子情境的事务数量
     *
     * @param c
     */
    private void getEventAgendaAndTodoCount(ContextEvent c) {
        List<AgendaEvent> agendas = agendaEventDao.queryBuilder()
                .where(AgendaEventDao.Properties.ContextId.eq(c.getId()))
                .build()
                .list();
        agendaCount += agendas.size();
        List<TodoEvent> todos = todoEventDao
                .queryBuilder()
                .where(TodoEventDao.Properties.ContextId.eq(c.getId()))
                .build()
                .list();
        todoCount += todos.size();
    }

    @Override
    public void toggle(boolean active) {
        if (hasChildItem(event)) {
            arrow.setImageResource(active ?
                    R.mipmap.ic_arrow_down_black_24dp : R.mipmap.ic_arrow_right_black_24dp);
        }
    }

    /**
     * 判断该条数据是否有子节点
     *
     * @param event
     * @return
     */
    private boolean hasChildItem(ContextEvent event) {
        return contextEventDao
                .queryBuilder()
                .where(ContextEventDao.Properties.UpContextId.eq(event.getId()))
                .build()
                .list().size() > 0;
    }

    public static class TreeItem {
        public int mode;
        public ContextEvent event;

        public TreeItem(int mode, ContextEvent event) {
            this.mode = mode;
            this.event = event;
        }
    }
}
