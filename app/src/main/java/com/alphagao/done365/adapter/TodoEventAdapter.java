package com.alphagao.done365.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.ContextEvent;
import com.alphagao.done365.greendao.bean.TodoEvent;
import com.alphagao.done365.greendao.gen.ContextEventDao;
import com.alphagao.done365.greendao.gen.TodoEventDao;
import com.alphagao.done365.utils.SizeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alpha on 2017/3/17.
 */

public class TodoEventAdapter extends RecyclerView.Adapter<TodoEventAdapter.ViewHolder> {

    private List<TodoEvent> todoEvents;
    private final TodoEventDao todoEventDao;
    private Context mContext;
    private String searchStr;

    public TodoEventAdapter(List<TodoEvent> todoEvents, String searchStr) {
        this.todoEvents = todoEvents;
        this.searchStr = searchStr;
        todoEventDao = DaoUtil.daoSession.getTodoEventDao();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_todo_recycview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int truePosition = holder.getAdapterPosition();
        final TodoEvent event = todoEvents.get(truePosition);
        holder.todoContent.setText(todoEvents.get(truePosition).getContent());
        final List<ContextEvent> contexts = DaoUtil.daoSession.getContextEventDao()
                .queryBuilder()
                .where(ContextEventDao.Properties.Id.
                        eq(todoEvents.get(truePosition).getContextId()))
                .build()
                .list();
        holder.todoContext.setText(contexts.get(0).getName());

        if (searchStr != null) {
            setSearchStrHighLight(holder.todoContent, searchStr);
        }

        holder.todoNeedTime.setText(SizeUtil.getRightTime(
                todoEvents.get(truePosition).getNeedTimes()));

        if (searchStr != null && event.getFinished().equals(TodoEvent.YES)) {
            holder.todoDone.setVisibility(View.GONE);
        }

        holder.todoDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (searchStr != null && event.getFinished().equals(TodoEvent.YES)) {
                    return;
                }

                event.setFinished(isChecked ? TodoEvent.YES : TodoEvent.NO);
                if (isChecked) {
                    holder.todoContent.setTextColor(mContext.getResources()
                            .getColor(R.color.colorSecondTitleText));
                } else {
                    holder.todoContent.setTextColor(mContext.getResources()
                            .getColor(R.color.colorOrdinaryText));
                }
                if (isChecked) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    event.setFinishTime(format.format(new Date()));
                }
                todoEventDao.update(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoEvents.size();
    }

    public void setData(List<TodoEvent> events) {
        this.todoEvents = events;
        notifyDataSetChanged();
    }

    private void setSearchStrHighLight(TextView inboxContent, String searchStr) {
        SpannableStringBuilder builder = new SpannableStringBuilder(inboxContent.getText());
        Pattern p = Pattern.compile(searchStr);
        Matcher matcher = p.matcher(inboxContent.getText());
        while (matcher.find()) {
            builder.setSpan(new ForegroundColorSpan(
                            mContext.getResources().getColor(R.color.text_highlight_color)),
                    matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        inboxContent.setText(builder);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.todoDone.setChecked(false);
        holder.todoDone.setVisibility(View.VISIBLE);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout view;
        TextView todoContent;
        TextView todoNeedTime;
        TextView todoContext;
        CheckBox todoDone;

        public ViewHolder(View itemView) {
            super(itemView);
            view = (RelativeLayout) itemView.findViewById(R.id.todo_view);
            todoContent = (TextView) itemView.findViewById(R.id.todo_content);
            todoNeedTime = (TextView) itemView.findViewById(R.id.todo_need_date);
            todoContext = (TextView) itemView.findViewById(R.id.todo_context);
            todoDone = (CheckBox) itemView.findViewById(R.id.todo_done);
        }
    }
}
