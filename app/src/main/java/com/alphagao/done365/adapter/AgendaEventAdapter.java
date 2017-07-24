package com.alphagao.done365.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
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
import com.alphagao.done365.greendao.bean.AgendaAlarmEvent;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.bean.AlarmType;
import com.alphagao.done365.greendao.bean.ContextEvent;
import com.alphagao.done365.greendao.bean.TodoEvent;
import com.alphagao.done365.greendao.gen.AgendaAlarmEventDao;
import com.alphagao.done365.greendao.gen.AgendaEventDao;
import com.alphagao.done365.greendao.gen.ContextEventDao;
import com.alphagao.done365.message.MessageManager;
import com.alphagao.done365.ui.activity.NewAgendaActivity;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alpha on 2017/3/10.
 */


public class AgendaEventAdapter extends RecyclerView.Adapter<AgendaEventAdapter.ViewHolder> {

    private static final String TAG = "AgendaEventAdapter";

    public static final int MODE_SHOW = 10130;

    private List<AgendaEvent> agendaEventList;
    private Context mContext;
    private AgendaAlarmEventDao agendaAlarmEventDao;
    private final ContextEventDao contextEventDao;
    private final AgendaEventDao agendaEventDao;
    private final SimpleDateFormat dateFormat;
    private int mode;
    private String searchStr;

    public AgendaEventAdapter(List<AgendaEvent> agendaEvents, int mode, String searchStr) {
        agendaEventList = agendaEvents;
        agendaAlarmEventDao = DaoUtil.daoSession.getAgendaAlarmEventDao();
        contextEventDao = DaoUtil.daoSession.getContextEventDao();
        agendaEventDao = DaoUtil.daoSession.getAgendaEventDao();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.mode = mode;
        this.searchStr = searchStr;
    }


    public void setData(List<AgendaEvent> agendaEvents) {
        this.agendaEventList = agendaEvents;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agenda_recycview, parent, false);
        mContext = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int mPosition = holder.getAdapterPosition();
        final AgendaEvent event = agendaEventList.get(mPosition);
        holder.content.setText(event.getContent());
        AgendaAlarmEvent agendaAlarmEvent = agendaAlarmEventDao.queryBuilder()
                .where(AgendaAlarmEventDao.Properties.AgendaId.eq(event.getId()))
                .build().unique();
        if (agendaAlarmEvent != null) {
            holder.nextAlarmDate.setText(agendaAlarmEvent.getNextAlarmDate());
        }

        ContextEvent context = contextEventDao
                .queryBuilder()
                .where(ContextEventDao.Properties.Id.eq(event.getContextId()))
                .build().unique();
        holder.context.setText(context != null ? context.getName() : "");

        if (searchStr != null) {
            setSearchStrHighLight(holder.content, searchStr);
        }

        if (mode == MODE_SHOW) {
            holder.agendaDone.setVisibility(View.GONE);
            return;
        }


        if (isAgendaOverdued(event)) {
            holder.nextAlarmDate.setTextColor(Color.RED);
        }

        if (searchStr != null && event.getFinished().equals(AgendaEvent.YES)) {
            holder.agendaDone.setVisibility(View.GONE);
        }

        holder.agendaDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dealWhenAgendaChecked(isChecked, holder, event);
            }
        });
        holder.agendaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchStr != null && event.getFinished().equals(AgendaEvent.YES)) {
                    return;
                }
                Intent intent = NewAgendaActivity.newIntent(mContext);
                intent.putExtra(NewAgendaActivity.OPEN_MODE, NewAgendaActivity.MODE_MODIFY);
                intent.putExtra("agenda", new Gson().toJson(event));
                mContext.startActivity(intent);
            }
        });
    }

    private void dealWhenAgendaChecked(boolean isChecked, ViewHolder holder, AgendaEvent event) {
        if (isChecked) {
            holder.content.setTextColor(mContext.getResources()
                    .getColor(R.color.colorSecondTitleText));
            setAgendaAlarmNext(event);
        } else {
            holder.content.setTextColor(mContext.getResources()
                    .getColor(R.color.colorOrdinaryText));
            setAgendaAlarmLast(event);
        }
        sendUpdatMessage();
    }

    private boolean isAgendaOverdued(AgendaEvent event) {
        return event.getAlarmTimeMills() < Calendar.getInstance().getTimeInMillis();
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
    public int getItemCount() {
        return agendaEventList.size();
    }

    private void sendUpdatMessage() {
        Message msg = Message.obtain();
        msg.what = TodoEvent.TODO_UPDATE;
        MessageManager.getInstance().publishMessage(msg);
    }

    /**
     * 恢复日程位上一次完成的状态（撤销本次完成）
     *
     * @param agenda
     */
    private void setAgendaAlarmLast(AgendaEvent agenda) {
        AgendaAlarmEvent alarm = getAlarmOfAgendaEvent(agenda);

        if (alarm == null) {
            return;
        }

        //一次性日程，没有重复提醒
        if (alarm.getIntervalTime() == 0 || alarm.getIntervalType() == 0) {
            agenda.setFinished(AgendaEvent.NO);
            agenda.setFinishTime(null);
            saveAgendaAndAlarm(agenda, null);
            return;
        }
        resetAgendaEventAndAlarmDate(agenda, alarm, false);
    }

    /**
     * 设置日程的下一次提醒（本次已完成）
     *
     * @param agenda
     */
    private void setAgendaAlarmNext(AgendaEvent agenda) {
        AgendaAlarmEvent alarm = getAlarmOfAgendaEvent(agenda);

        if (alarm == null) {
            return;
        }

        //一次性日程，没有重复提醒
        if (alarm.getIntervalTime() == 0 || alarm.getIntervalType() == 0) {
            agenda.setFinished(AgendaEvent.YES);
            agenda.setFinishTime(dateFormat.format(new Date()));
            saveAgendaAndAlarm(agenda, null);
            return;
        }
        resetAgendaEventAndAlarmDate(agenda, alarm, true);
    }

    /**
     * 重置日程的完成状态
     *
     * @param agenda    日程
     * @param alarm     日程对应的提醒
     * @param isSetNext 是否是设置下一次提醒，不是就是上一次
     */
    private void resetAgendaEventAndAlarmDate(AgendaEvent agenda, AgendaAlarmEvent alarm,
                                              boolean isSetNext) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(agenda.getAlarmTimeMills());

        int num = isSetNext ? alarm.getIntervalTime() : -alarm.getIntervalTime();

        switch (alarm.getIntervalType()) {
            case AlarmType.TYPE_MINUTE:
                calendar.add(Calendar.MINUTE, num);
                break;
            case AlarmType.TYPE_HOUR:
                calendar.add(Calendar.HOUR, num);
                break;
            case AlarmType.TYPE_DAY:
                calendar.add(Calendar.DAY_OF_YEAR, num);
                break;
            case AlarmType.TPYE_WEEK:
                calendar.add(Calendar.WEEK_OF_YEAR, num);
                break;
            case AlarmType.TPYE_MONTH:
                calendar.add(Calendar.MONTH, num);
                break;
            case AlarmType.TPYE_YEAR:
                calendar.add(Calendar.YEAR, num);
                break;
            default:
                break;
        }
        String nextAlarmDate = dateFormat.format(calendar.getTime());
        agenda.setAlarmDate(nextAlarmDate.substring(0, nextAlarmDate.indexOf(" ")));
        agenda.setAlarmTime(nextAlarmDate.substring(nextAlarmDate.indexOf(" ") + 1));
        agenda.setAlarmTimeMills(calendar.getTimeInMillis());
        alarm.setNextAlarmDate(nextAlarmDate);
        saveAgendaAndAlarm(agenda, alarm);
    }

    private void saveAgendaAndAlarm(AgendaEvent agenda, AgendaAlarmEvent alarm) {
        if (agenda != null) {
            agendaEventDao.update(agenda);
        }
        if (alarm != null) {
            //agenda 完成，对应的 alarm 暂时不删除，以免后续无法恢复
            agendaAlarmEventDao.update(alarm);
        }
    }

    /**
     * 获取日程对应的提醒
     *
     * @param event 日程
     * @return 提醒
     */
    private AgendaAlarmEvent getAlarmOfAgendaEvent(AgendaEvent event) {
        List<AgendaAlarmEvent> alarms = agendaAlarmEventDao
                .queryBuilder()
                .where(AgendaAlarmEventDao.Properties.AgendaId.eq(event.getId()))
                .build()
                .list();
        if (alarms.size() > 0) {
            return alarms.get(0);
        }
        return null;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        //重置回收 view 的状态，防止显示混论
        holder.agendaDone.setChecked(false);
        holder.nextAlarmDate.setTextColor(
                mContext.getResources().getColor(R.color.colorOrdinaryText));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout agendaView;
        TextView content;
        TextView nextAlarmDate;
        TextView context;
        CheckBox agendaDone;

        public ViewHolder(View itemView) {
            super(itemView);
            agendaView = (RelativeLayout) itemView.findViewById(R.id.agenda_view);
            content = (TextView) agendaView.findViewById(R.id.agenda_content);
            nextAlarmDate = (TextView) agendaView.findViewById(R.id.agenda_next_alarm_date);
            context = (TextView) agendaView.findViewById(R.id.agenda_context);
            agendaDone = (CheckBox) agendaView.findViewById(R.id.agenda_done);
        }
    }
}
