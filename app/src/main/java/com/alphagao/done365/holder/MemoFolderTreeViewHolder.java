package com.alphagao.done365.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.MemoEvent;
import com.alphagao.done365.greendao.gen.MemoEventDao;
import com.unnamed.b.atv.model.TreeNode;

import java.util.List;

/**
 * Created by Alpha on 2017/4/1.
 */

public class MemoFolderTreeViewHolder
        extends TreeNode.BaseNodeViewHolder<MemoFolderTreeViewHolder.TreeItem> {

    private final MemoEventDao memoEventDao;
    private ImageView arrow;
    private MemoEvent event;
    private int childcount;

    public MemoFolderTreeViewHolder(Context context) {
        super(context);
        memoEventDao = DaoUtil.daoSession.getMemoEventDao();
    }

    @Override
    public View createNodeView(TreeNode node, TreeItem value) {
        event = value.event;
        View view = LayoutInflater.from(context).inflate(R.layout.item_treeview_memo, null, false);
        TextView content = (TextView) view.findViewById(R.id.memoContent);
        arrow = (ImageView) view.findViewById(R.id.memoArrow);
        TextView childCountView = (TextView) view.findViewById(R.id.memo_child_count);
        content.setText(event.getContent());
        childcount = 0;
        getMemoCount(event);
        childCountView.setText(context.getString(R.string.memo_count, childcount));
        if (hasChild(event)) {
            arrow.setImageResource(R.mipmap.ic_arrow_right_black_24dp);
        }
        return view;
    }

    /**
     * 该条目下是否还有文件夹
     * @param event
     * @return
     */
    private boolean hasChild(MemoEvent event) {
        return memoEventDao
                .queryBuilder()
                .where(memoEventDao.queryBuilder()
                        .and(MemoEventDao.Properties.DirID.eq(event.getId()),
                                MemoEventDao.Properties.IsDir.eq(MemoEvent.YES)))
                .build()
                .list()
                .size() > 0;
    }

    /**
     * 该条目下所有子条目，包含文件夹
     * @param event
     */
    public void getMemoCount(MemoEvent event) {
        getMemoChildCount(event);
        List<MemoEvent> list = memoEventDao
                .queryBuilder()
                .where(MemoEventDao.Properties.DirID.eq(event.getId()))
                .build()
                .list();
        if (list.size() > 0) {
            for (MemoEvent m : list) {
                if (m.getIsDir() == MemoEvent.YES) {
                    getMemoCount(m);
                }
            }
        }
    }

    /**
     * 该条目下备忘录的数量，不包含文件夹
     * @param event
     */
    private void getMemoChildCount(MemoEvent event) {
        List<MemoEvent> list = memoEventDao
                .queryBuilder()
                .where(memoEventDao.queryBuilder()
                        .and(MemoEventDao.Properties.DirID.eq(event.getId())
                        , MemoEventDao.Properties.IsDir.eq(MemoEvent.NO)))
                .build()
                .list();
        childcount += list.size();
    }

    @Override
    public void toggle(boolean active) {
        if (hasChild(event)) {
            arrow.setImageResource(active ?
                    R.mipmap.ic_arrow_down_black_24dp : R.mipmap.ic_arrow_right_black_24dp);
        }
    }

    public static class TreeItem {
        public int icon;
        public MemoEvent event;

        public TreeItem(int icon, MemoEvent event) {
            this.icon = icon;
            this.event = event;
        }
    }
}
