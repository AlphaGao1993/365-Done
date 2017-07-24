package com.alphagao.done365.ui.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alphagao.done365.R;
import com.alphagao.done365.adapter.AgendaEventAdapter;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.bean.AlarmType;
import com.alphagao.done365.greendao.bean.ContextEvent;
import com.alphagao.done365.greendao.bean.Festival;
import com.alphagao.done365.greendao.gen.AgendaEventDao;
import com.alphagao.done365.greendao.gen.ContextEventDao;
import com.alphagao.done365.greendao.gen.FestivalDao;
import com.alphagao.done365.ui.activity.NewAgendaActivity;
import com.alphagao.done365.utils.DateUtil;
import com.alphagao.done365.widget.MyDateTheme;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.google.gson.Gson;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.aigestudio.datepicker.bizs.calendars.DPCManager;
import cn.aigestudio.datepicker.bizs.decors.DPDecor;
import cn.aigestudio.datepicker.bizs.themes.DPTManager;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

/**
 * Created by Alpha on 2017/3/9.
 */


public class AgendaFragment extends BaseFragment {
    private static final String TAG = "AgendaFragment";
    private static final int sortByCreateTime = 1;
    private static final int sortByAlarmDate = 2;
    private static final int sortByNeedTimeMills = 3;
    private static final int SHOW_MODE_LIST = 4;
    private static final int SHOW_MODE_DAY = 5;
    private static final int SHOW_MODE_WEEK = 6;
    private static final int SHOW_MODE_MONTH = 7;

    @BindView(R.id.agenda_view)
    RecyclerView agendaView;
    @BindView(R.id.calendar_day_view)
    AgendaCalendarView calendarDayView;
    @BindView(R.id.weekLayout)
    WeekView weekLayout;
    @BindView(R.id.monthLayout)
    DatePicker monthLayout;
    private List<AgendaEvent> agendaEventList;
    private AgendaEventDao agendaEventDao;
    private AgendaEventAdapter agendaEventAdapter;

    private int sortMode = sortByAlarmDate;
    private ContextEventDao contextEventDao;
    private Unbinder unbinder;
    private List<WeekViewEvent> weekViewEventList = new ArrayList<>();
    private int[] colors = new int[]{R.color.colorPrimary, R.color.theme_primary_dark,
            R.color.colorAccent, R.color.colorDivider, R.color.colorLabelText,
            R.color.colorOrdinaryText, R.color.date_picker_rest_day};
    private int colorIndex = 0;
    private static final int agenda_show_mode = SHOW_MODE_LIST;
    private FestivalDao festivalDao;
    private List<Festival> festivalList;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        initData();
        initDP();
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initDP() {
        DPTManager.getInstance().initCalendar(new MyDateTheme());
        DPCManager.getInstance().setDecorTL(mockTmpTL());
        DPCManager.getInstance().setDecorTR(mockTmpTR());
    }

    private List<String> mockTmpTR() {
        List<String> l = new ArrayList<>();
        festivalList = festivalDao.queryBuilder().build().list();
        for (Festival val : festivalList) {
            l.add(val.getDate());
        }
        return l;
    }

    private List<String> mockTmpTL() {
        List<String> l = new ArrayList<>();
        for (AgendaEvent e : agendaEventList) {
            l.add(DateUtil.shortDate(e.getAlarmDate()));
        }
        return l;
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        agendaView.setLayoutManager(manager);
        agendaView.setAdapter(agendaEventAdapter);

        monthLayout.setDate(Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH) + 1);
        //monthLayout
        monthLayout.setMode(DPMode.SINGLE);
        monthLayout.setTodayDisplay(true);
        monthLayout.setDeferredDisplay(true);
        monthLayout.setHolidayDisplay(false);
        monthLayout.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                setMonthViewEvent(date);
            }
        });

        monthLayout.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorTL(Canvas canvas, Rect rect, Paint paint, String data) {
                List<AgendaEvent> events;
                if (data.length() < 10) {
                    data = DateUtil.longDate(data);
                }
                events = agendaEventDao.queryBuilder()
                        .where(agendaEventDao.queryBuilder().and(
                                AgendaEventDao.Properties.AlarmDate.like("%" + data + "%"),
                                AgendaEventDao.Properties.Finished.eq(AgendaEvent.NO)))
                        .build()
                        .list();
                if (events.size() > 0) {
                    paint.setColor(Color.RED);//2017-01-01
                    canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, paint);
                    paint.setColor(Color.WHITE);
                    canvas.drawText(events.size() + "", rect.centerX(), rect.centerY() + 7, paint);
                }
            }

            @Override
            public void drawDecorTR(Canvas canvas, Rect rect, Paint paint, String data) {
                int i = getFestivalName(data);
                if (i >= 0) {
                    paint.setColor(Color.BLUE);
                    canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, paint);
                    paint.setColor(Color.WHITE);
                    canvas.drawText("节", rect.centerX(), rect.centerY() + 7, paint);
                }
            }
        });

        weekLayout.setMonthChangeListener(listener);
        weekLayout.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        weekLayout.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                showAgendaDetail(event);
            }
        });
    }

    private void showAgendaDetail(WeekViewEvent event) {
        AgendaEvent agendaEvent = agendaEventDao
                .queryBuilder()
                .where(AgendaEventDao.Properties.Id.eq(event.getId()))
                .build()
                .unique();
        Intent intent = new Intent(getActivity(), NewAgendaActivity.class);
        intent.putExtra(NewAgendaActivity.OPEN_MODE, NewAgendaActivity.MODE_MODIFY);
        intent.putExtra("agenda", new Gson().toJson(agendaEvent));
        getActivity().startActivity(intent);
    }


    private void setMonthViewEvent(String date) {
        try {
            date = DateUtil.longDate(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(date));

            monthLayout.setVisibility(View.GONE);
            weekLayout.setVisibility(View.VISIBLE);

            weekLayout.goToDate(calendar);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private int getFestivalName(String data) {
        for (int i = 0; i < festivalList.size(); i++) {
            if (festivalList.get(i).getDate().equals(data)) {
                return i;
            }
        }
        return -1;
    }

    private void initData() {
        agendaEventDao = DaoUtil.daoSession.getAgendaEventDao();
        contextEventDao = DaoUtil.daoSession.getContextEventDao();
        festivalDao = DaoUtil.daoSession.getFestivalDao();
        updateAgendaData(sortMode);
        agendaEventAdapter = new AgendaEventAdapter(agendaEventList, 0, null);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }


    private void updateAgendaData(int orderBy) {
        if (agendaEventDao != null) {
            QueryBuilder<AgendaEvent> builder = agendaEventDao.queryBuilder();

            if (userPrefs.getBoolean("showPastEventInMonth")) {
                builder.where(AgendaEventDao.Properties.Finished.eq(AgendaEvent.NO));
            } else {
                builder.where(agendaEventDao.queryBuilder().and(
                        AgendaEventDao.Properties.Finished.eq(AgendaEvent.NO),
                        AgendaEventDao.Properties.AlarmTimeMills
                                .ge(Calendar.getInstance().getTimeInMillis())));
            }

            switch (orderBy) {
                case 1://创建日期排序
                    builder.orderAsc(AgendaEventDao.Properties.CreateDate);
                    break;
                case 2://提醒日期排序
                    builder.orderAsc(AgendaEventDao.Properties.AlarmDate,
                            AgendaEventDao.Properties.AlarmTime);
                    break;
                case 3://按持续时间排序
                    builder.orderAsc(AgendaEventDao.Properties.NeddTimeMills);
                    break;
                default:
                    break;
            }
            agendaEventList = builder.build().list();
            Log.d(TAG, "updateAgendaData: agendaEventList.Size():" + agendaEventList.size());
            if (agendaEventAdapter != null) {
                agendaEventAdapter.setData(agendaEventList);
            }
        }
        if (weekLayout != null) {
            weekLayout.notifyDatasetChanged();
            listener.onMonthChange(Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH) + 1);
            weekLayout.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        }
    }

    @Override
    public void onNewMessage(Message msg) {
        switch (msg.what) {
            case AgendaEvent.AGENDA_UPDATE:
                updateAgendaData(sortMode);
                break;
            case AgendaEvent.SORT:
                showSortDialog();
                break;
            case AgendaEvent.AGENDA_CALENDAR_TODAY:
                switchCalendar(SHOW_MODE_DAY);
                break;
            case AgendaEvent.AGENDA_CALENDAR_WEEK:
                switchCalendar(SHOW_MODE_WEEK);
                break;
            case AgendaEvent.AGENDA_CALENDAR_MONTH:
                switchCalendar(SHOW_MODE_MONTH);
                break;
            case AgendaEvent.AGENDA_CALENDAR_LIST:
                switchCalendar(SHOW_MODE_LIST);
                break;
            default:
                break;
        }
    }

    private void switchCalendar(int mode) {
        switch (mode) {
            case SHOW_MODE_LIST:
                agendaView.setVisibility(View.VISIBLE);
                weekLayout.setVisibility(View.GONE);
                monthLayout.setVisibility(View.GONE);
                break;
            case SHOW_MODE_DAY:
                agendaView.setVisibility(View.GONE);
                weekLayout.setVisibility(View.VISIBLE);
                weekLayout.setNumberOfVisibleDays(1);
                monthLayout.setVisibility(View.GONE);
                break;
            case SHOW_MODE_WEEK:
                agendaView.setVisibility(View.GONE);
                weekLayout.setVisibility(View.VISIBLE);
                weekLayout.setNumberOfVisibleDays(3);
                monthLayout.setVisibility(View.GONE);
                break;
            case SHOW_MODE_MONTH:
                agendaView.setVisibility(View.GONE);
                weekLayout.setVisibility(View.GONE);
                monthLayout.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        //在每一次切换视图的时候跳转到当前时间
        weekLayout.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
    }

    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_agenda_sort, null);

        builder.setTitle("日程排序")
                .setView(view)
                .setCancelable(true)
                .setNegativeButton("取消", null);

        RadioGroup sortGroup = (RadioGroup) view.findViewById(R.id.agenda_sort_group);
        int agenda_sort_id = userPrefs.getInt("agenda_sort_by");
        sortGroup.check(agenda_sort_id != -1 ? agenda_sort_id : R.id.byAlarmTime);

        final AlertDialog dialog = builder.create();
        sortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.byCreateTime:
                        updateAgendaData(sortByCreateTime);
                        break;
                    case R.id.byAlarmTime:
                        updateAgendaData(sortByAlarmDate);
                        break;
                    case R.id.byNeedTime:
                        updateAgendaData(sortByNeedTimeMills);
                        break;
                    default:
                        break;
                }
                userPrefs.putInt("agenda_sort_by", checkedId);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (monthLayout != null) {
            monthLayout.destroyDrawingCache();
        }
    }

    private MonthLoader.MonthChangeListener listener = new MonthLoader.MonthChangeListener() {
        @Override
        public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
            String month = newMonth - 1 <= 10 ? "0" + (newMonth - 1) : newMonth + "";
            String datePattern = "%" + newYear + "-" + month + "%";
            Log.d(TAG, "onMonthChange: " + datePattern);
            List<AgendaEvent> eventList = agendaEventDao
                    .queryBuilder()
                    .where(agendaEventDao.queryBuilder().and(
                            AgendaEventDao.Properties.AlarmDate.like(datePattern),
                            AgendaEventDao.Properties.Finished.eq(AgendaEvent.NO)))
                    .build()
                    .list();

            weekViewEventList = new ArrayList<>();
            for (AgendaEvent event : eventList) {
                fillWeekViewEvents(event);
            }
            Log.d(TAG, "onMonthChange: weekViewEventList.size:" + weekViewEventList.size());
            return weekViewEventList;
        }
    };

    private void fillWeekViewEvents(AgendaEvent event) {
        List<ContextEvent> contexts = contextEventDao
                .queryBuilder()
                .where(ContextEventDao.Properties.Id.eq(event.getContextId()))
                .build()
                .list();

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.YEAR, Integer.parseInt(event.getAlarmDate().substring(0, 4)));
        startTime.set(Calendar.MONTH, Integer.parseInt(event.getAlarmDate().substring(5, 7)) - 1);
        startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(event.getAlarmDate().substring(8, 10)));
        startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(event.getAlarmTime().substring(0, 2)));
        startTime.set(Calendar.MINUTE, Integer.parseInt(event.getAlarmTime().substring(3, 5)));

        Calendar endTime = (Calendar) startTime.clone();
        switch (event.getNeedTimeType()) {
            case AlarmType.TYPE_HOUR:
                endTime.add(Calendar.HOUR, event.getNeedTimeNum());
                break;
            case AlarmType.TYPE_MINUTE:
                endTime.add(Calendar.MINUTE, event.getNeedTimeNum());
                break;
            default:
                break;
        }
        WeekViewEvent cal = new WeekViewEvent(event.getId(), event.getContent(),
                startTime, endTime);
        cal.setLocation(" @ " + contexts.get(0).getName());
        cal.setColor(getResources().getColor(colors[colorIndex++ % colors.length]));

        weekViewEventList.add(cal);
    }
}
