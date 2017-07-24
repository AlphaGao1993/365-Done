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
 * Created by Alpha on 2017/3/20.
 */

public class MemoTreeViewHolder extends
        TreeNode.BaseNodeViewHolder<MemoTreeViewHolder.TreeItem> {

    private final MemoEventDao memoEventDao;
    private MemoEvent event;
    private ImageView arrow;
    private int memoCount;

    public MemoTreeViewHolder(Context context) {
        super(context);
        memoEventDao = DaoUtil.daoSession.getMemoEventDao();
    }

    @Override
    public View createNodeView(TreeNode node, TreeItem value) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_treeview_memo, null, false);
        event = value.event;
        arrow = (ImageView) view.findViewById(R.id.memoArrow);
        ImageView icon = (ImageView) view.findViewById(R.id.memoIcon);
        TextView content = (TextView) view.findViewById(R.id.memoContent);
        TextView count = (TextView) view.findViewById(R.id.memo_child_count);

        if (hasChildItem(event)) {
            arrow.setImageResource(R.mipmap.ic_arrow_right_black_24dp);
        }
        if (event.getIsDir() == MemoEvent.NO) {
            icon.setImageResource(R.mipmap.ic_note_black_24dp);
        }
        content.setText(event.getContent());
        memoCount = 0;
        getChildCount(event);
        if (event.getIsDir() == MemoEvent.YES) {
            count.setText(context.getString(R.string.memo_count,memoCount));
        }

        return view;
    }

    /**
     * 计算该目录下属所有备忘录的数量，不包含目录
     *
     * @param event
     */
    private void getChildCount(MemoEvent event) {
        getCurrentChild(event);
        List<MemoEvent> list = memoEventDao
                .queryBuilder()
                .where(MemoEventDao.Properties.DirID.eq(event.getId()))
                .build().list();
        if (list.size() > 0) {
            for (MemoEvent m : list) {
                if (m.getIsDir() == MemoEvent.YES) {
                    getChildCount(m);
                }
            }
        }
    }

    /**
     * 计算当前目录下的备忘录数量，不包含目录
     *
     * @param event
     */
    private void getCurrentChild(MemoEvent event) {
        List<MemoEvent> list = memoEventDao
                .queryBuilder()
                .where(memoEventDao
                        .queryBuilder()
                        .and(MemoEventDao.Properties.IsDir.eq(MemoEvent.NO)
                                , MemoEventDao.Properties.DirID.eq(event.getId())))
                .build()
                .list();
        memoCount += list.size();
    }

    /**
     * 判断该条目下是否含有子条目，包含文件夹
     *
     * @param event
     * @return
     */
    private boolean hasChildItem(MemoEvent event) {
        List<MemoEvent> list = memoEventDao
                .queryBuilder()
                .where(MemoEventDao.Properties.DirID.eq(event.getId()))
                .build()
                .list();
        return list.size() > 0;
    }


    @Override
    public void toggle(boolean active) {
        if (event.getIsDir() == MemoEvent.NO || !hasChildItem(event)) {
            return;
        }
        arrow.setImageResource(active ?
                R.mipmap.ic_arrow_down_black_24dp : R.mipmap.ic_arrow_right_black_24dp);
        super.toggle(active);
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
