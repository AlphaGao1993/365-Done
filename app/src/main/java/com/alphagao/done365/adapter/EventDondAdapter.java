package com.alphagao.done365.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.bean.ProjectEvent;
import com.alphagao.done365.greendao.bean.TodoEvent;
import com.alphagao.done365.greendao.gen.ProjectEventDao;

import java.util.List;

/**
 * Created by Alpha on 2017/3/10.
 */


public class EventDondAdapter<T> extends RecyclerView.Adapter<EventDondAdapter.ViewHolder> {

    private List<T> eventList;
    private final ProjectEventDao projectEventDao;

    public EventDondAdapter(List<T> agendaEvents) {
        eventList = agendaEvents;
        projectEventDao = DaoUtil.daoSession.getProjectEventDao();
    }


    public void setData(List<T> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_done_recycview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int truePosition = holder.getAdapterPosition();
        if (eventList.get(truePosition) instanceof AgendaEvent) {
            final AgendaEvent event = (AgendaEvent) eventList.get(truePosition);
            holder.content.setText(event.getContent());
            holder.doneTime.setText(event.getFinishTime().substring(0, 16));
            List<ProjectEvent> projects = projectEventDao.queryBuilder()
                    .where(ProjectEventDao.Properties.Id.eq(event.getProjectId()))
                    .build()
                    .list();
            if (projects.size() > 0) {
                holder.project.setText(projects.get(0).getName());
            }
        } else if (eventList.get(truePosition) instanceof TodoEvent) {
            final TodoEvent event = (TodoEvent) eventList.get(truePosition);
            holder.content.setText(event.getContent());
            holder.doneTime.setText(event.getFinishTime().substring(0, 16));
            if (event.getProjectId() != null) {
                List<ProjectEvent> projects = projectEventDao.queryBuilder()
                        .where(ProjectEventDao.Properties.Id.eq(event.getProjectId()))
                        .build()
                        .list();
                if (projects.size() > 0) {
                    holder.project.setText(projects.get(0).getName());
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout eventView;
        TextView content;
        TextView doneTime;
        TextView project;

        public ViewHolder(View itemView) {
            super(itemView);
            eventView = (RelativeLayout) itemView.findViewById(R.id.done_view);
            content = (TextView) eventView.findViewById(R.id.agenda_content);
            doneTime = (TextView) eventView.findViewById(R.id.agenda_finish_date);
            project = (TextView) eventView.findViewById(R.id.agenda_project);
        }
    }

}
