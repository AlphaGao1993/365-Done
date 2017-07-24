package com.alphagao.done365.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.adapter.AlarmTypeAdapter;
import com.alphagao.done365.adapter.MySpinnerAdapter;
import com.alphagao.done365.ui.fragment.ChooseProjectFragment;
import com.alphagao.done365.ui.fragment.ContextFragment;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.AgendaAlarmEvent;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.bean.AlarmType;
import com.alphagao.done365.greendao.bean.ContextEvent;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.greendao.bean.ProjectEvent;
import com.alphagao.done365.greendao.gen.AgendaAlarmEventDao;
import com.alphagao.done365.greendao.gen.AlarmTypeDao;
import com.alphagao.done365.greendao.gen.ContextEventDao;
import com.alphagao.done365.utils.SizeUtil;
import com.alphagao.done365.widget.StickyListView;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewAgendaActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "NewAgendaActivity";
    public static final String OPEN_MODE = "open_mode";
    public static final int MODE_ADD = 10301;
    public static final int MODE_MODIFY = 10302;
    //创建新情境的请求ID
    private static final int REQUEST_NEW_CONTEXT = 0x00011;
    private static final int REQUEST_CHOOSE_PROJECT = 0x00014;
    private static final int REQUEST_CHOOSE_CONTEXT = 0x00015;


    @BindView(R.id.agenda_content_text)
    EditText agendaContentText;
    @BindView(R.id.alarm_dateTime)
    TextView alarmSimpleDateTime;
    @BindView(R.id.setDateView)
    LinearLayout setDateView;
    @BindView(R.id.dateTime_picker)
    SingleDateAndTimePicker dateTimePicker;
    @BindView(R.id.hideDatePickerView)
    LinearLayout hideDatePickerView;
    @BindView(R.id.agenda_alarm_type_text)
    TextView agendaAlarmTypeText;
    @BindView(R.id.alarm_repeat_type)
    LinearLayout alarmRepeatType;
    @BindView(R.id.agenda_alarm_customize)
    TextView agendaAlarmCustomize;
    @BindView(R.id.hideAlarmType)
    LinearLayout hideAlarmType;
    @BindView(R.id.activity_new_agenda)
    CoordinatorLayout activityNewAgenda;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.agenda_selected_context)
    TextView agendaSelectedContext;
    @BindView(R.id.agenda_add_context)
    LinearLayout agendaAddContext;
    @BindView(R.id.alarm_type_list)
    StickyListView alarmTypeList;
    @BindView(R.id.add_agenda_to_list)
    Button addAgendaToList;
    @BindView(R.id.agenda_need_time)
    TextView agendaNeedTime;
    @BindView(R.id.agenda_need_view)
    LinearLayout agendaNeedView;
    @BindView(R.id.select_project)
    TextView selectProject;
    @BindView(R.id.select_project_view)
    LinearLayout selectProjectView;
    @BindView(R.id.agenda_vibrate)
    Switch agendaVibrate;
    @BindView(R.id.add_agenda_to_finish)
    Button addAgendaToFinish;
    private SimpleDateFormat shortFormat;
    private TextView alarmInDialog;
    private int selectIntervalNum;
    private String selectType;
    private Integer selectTypeValue;
    private List<ContextEvent> contextEvents;
    private ContextEventDao contextEventDao;
    private CheckBox saveToList;
    private AlarmTypeDao alarmTypeDao;
    private List<AlarmType> alarmTypes;
    private AlarmTypeAdapter alarmTypeAdapter;
    private InboxEvent inboxEvent;
    private String shortDate;
    private List<String> types;
    private List<String> nums;
    private int neededTimeNum = 1;
    private int neededTimeType = Calendar.HOUR;
    private ContextEvent selectedContext = null;
    private ProjectEvent selectedProject = null;
    private MySpinnerAdapter numAdapter;
    private Spinner numSpinner;
    private Date alarmDate = new Date();
    private int open_mode;
    private AgendaEvent modifyAgenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_agenda);
        ButterKnife.bind(this);
        handleIntent();
        setupActionBar();
        initNumAndType();
        initDisplayContentAndDate();
        initContextData();
        initAlarmTypeData();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        open_mode = intent.getIntExtra("open_mode", MODE_ADD);
        Log.d(TAG, "handleIntent: " + open_mode);
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.arrow_back_white_24dp);

            if (open_mode == MODE_MODIFY) {
                actionBar.setTitle(getString(R.string.agenda_detail));
                addAgendaToList.setText(getString(R.string.exec_modify));
                addAgendaToFinish.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 初始化要显示的数据和日期
     */
    private void initDisplayContentAndDate() {
        Intent intent = getIntent();
        if (open_mode == MODE_ADD) {
            String inboxJson = intent.getStringExtra("inbox");
            inboxEvent = new Gson().fromJson(inboxJson, InboxEvent.class);
            agendaContentText.setText(inboxEvent.getContent());
            agendaContentText.setSelection(inboxEvent.getContent().length());
            displayDate(new Date());
        } else if (open_mode == MODE_MODIFY) {
            String agendaJson = intent.getStringExtra("agenda");
            modifyAgenda = new Gson().fromJson(agendaJson, AgendaEvent.class);
            agendaContentText.setText(modifyAgenda.getContent());
            agendaContentText.setSelection(modifyAgenda.getContent().length());
            displayDate(new Date(modifyAgenda.getAlarmTimeMills()));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -10);
        dateTimePicker.setMinDate(calendar.getTime());
        dateTimePicker.setCyclic(false);
        dateTimePicker.setListener(new SingleDateAndTimePicker.Listener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                alarmDate = date;
                displayDate(date);
            }
        });

        agendaAddContext.setOnClickListener(this);
        agendaNeedView.setOnClickListener(this);
        setDateView.setOnClickListener(this);
        alarmRepeatType.setOnClickListener(this);
    }

    private void initContextData() {
        contextEventDao = DaoUtil.daoSession.getContextEventDao();
        contextEvents = contextEventDao
                .queryBuilder()
                .where(ContextEventDao.Properties.UpContextId.eq(0))
                .build()
                .list();
        //默认显示第一条情境
        if (contextEvents.size() > 0) {
            selectedContext = contextEvents.get(0);
            if (open_mode == MODE_MODIFY) {
                List<ContextEvent> cons = contextEventDao.queryBuilder()
                        .where(ContextEventDao.Properties.Id.eq(modifyAgenda.getContextId()))
                        .build()
                        .list();
                agendaSelectedContext.setText(cons.get(0).getName());
                selectedContext = cons.get(0);
            } else {
                agendaSelectedContext.setText(selectedContext.getName());
            }
        }
    }

    private void initAlarmTypeData() {
        alarmTypeDao = DaoUtil.daoSession.getAlarmTypeDao();
        alarmTypes = alarmTypeDao
                .queryBuilder()
                .build()
                .list();
        alarmTypeAdapter = new AlarmTypeAdapter(alarmTypes);
        alarmTypeList.setAdapter(alarmTypeAdapter);
        alarmTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                agendaAlarmTypeText.setText(alarmTypes.get(position).getName());
            }
        });
        alarmTypeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view,
                                           final int position, long id) {
                showLongClickDialog(view, position);
                return true;
            }
        });
    }

    /**
     * 长按删除提醒 item
     *
     * @param view     itemView
     * @param position 位置
     */
    private void showLongClickDialog(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(getString(R.string.delete_from_alarm_title))
                .setPositiveButton(getString(R.string.delete_from_alarm_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showDeleteDialog(position);
                            }
                        })
                .setNegativeButton(getString(R.string.delete_from_alarm_no), null)
                .show();
    }

    private void showDeleteDialog(int position) {
        final AlarmType deletedAlarm = alarmTypes.get(position);
        alarmTypeDao.delete(deletedAlarm);
        alarmTypes.remove(deletedAlarm);
        alarmTypeAdapter.notifyDataSetChanged();
        Snackbar.make(getCurrentFocus(),
                getString(R.string.delete_from_alarm_tip), 5000)
                .setAction(getString(R.string.delete_from_alarm_back),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                revokeDeletedAlarm(deletedAlarm);
                            }
                        }).show();
    }

    private void revokeDeletedAlarm(AlarmType deletedAlarm) {
        alarmTypes.add(deletedAlarm);
        alarmTypeDao.insert(deletedAlarm);
        alarmTypeAdapter.notifyDataSetChanged();
        toast(getString(R.string.delete_from_alarm_back_tip));
    }

    private void displayDate(Date date) {
        if (shortFormat == null) {
            // 2017/03/11 19:00 周日
            shortFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm EE");
        }
        shortDate = shortFormat.format(date);
        alarmSimpleDateTime.setText(shortDate);
    }

    @OnClick({R.id.setDateView, R.id.alarm_repeat_type, R.id.agenda_alarm_customize,
            R.id.agenda_add_context, R.id.add_agenda_to_list, R.id.agenda_need_view,
            R.id.select_project_view, R.id.add_agenda_to_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setDateView:
                switchDatePicker();
                break;
            case R.id.alarm_repeat_type:
                switchRepeatTypeView();
                break;
            case R.id.agenda_alarm_customize:
                showCustomizeAlarmDialog();
                break;
            case R.id.agenda_add_context:
                requestChooseContext();
                break;
            case R.id.add_agenda_to_list:
                saveAgenda();
                break;
            case R.id.agenda_need_view:
                showNeedTimeView();
                break;
            case R.id.select_project_view:
                requestChooseProject();
                break;
            case R.id.add_agenda_to_finish:
                finishAgenda();
                break;
            default:
                break;
        }

    }

    private void finishAgenda() {
        modifyAgenda.setFinished(AgendaEvent.YES);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        modifyAgenda.setFinishTime(format.format(new Date()));
        DaoUtil.daoSession.getAgendaEventDao().update(modifyAgenda);

        Message msg = Message.obtain();
        msg.what = AgendaEvent.AGENDA_UPDATE;
        messageManager.publishMessage(msg);
        finish();
    }

    private void requestChooseProject() {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.FRAGMENT_PARAM, ChooseProjectFragment.class);
        startActivityForResult(intent, REQUEST_CHOOSE_PROJECT);
    }

    private void showNeedTimeView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_need_time, null);
        final Spinner numPicker = (Spinner) view.findViewById(R.id.number_picker);
        final Spinner typePicker = (Spinner) view.findViewById(R.id.type_picker);
        typePicker.setAdapter(new MySpinnerAdapter(this, types.subList(0, 2)));
        numPicker.setAdapter(new MySpinnerAdapter(this, nums));
        builder.setTitle(getString(R.string.set_need_time_title))
                .setView(view)
                .setPositiveButton(getString(R.string.set_need_time_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setNeedTimeFromDialog(numPicker, typePicker);
                            }
                        })
                .show();
    }

    private void setNeedTimeFromDialog(Spinner numPicker, Spinner typePicker) {
        int num = Integer.parseInt(numPicker.getSelectedItem().toString());
        int typePosition = typePicker.getSelectedItemPosition();
        neededTimeNum = num;
        neededTimeType = SizeUtil.getTimeType(types.get(typePosition));
        agendaNeedTime.setText(neededTimeNum + " " + types.get(typePosition));
    }

    private void requestChooseContext() {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.FRAGMENT_PARAM, ContextFragment.class);
        startActivityForResult(intent, REQUEST_CHOOSE_CONTEXT);
    }

    private void saveAgenda() {
        if (selectedContext == null) {
            toast(getString(R.string.choose_relative_context));
            return;
        }

        //添加至日程表
        AgendaEvent newAgenda;
        if (open_mode == MODE_MODIFY) {
            newAgenda = modifyAgenda;
        } else {
            newAgenda = new AgendaEvent();
            newAgenda.setCreateDate(inboxEvent.getCreateTime());
        }
        newAgenda.setContent(agendaContentText.getText().toString());
        String date = shortDate.substring(0, 10);
        String time = shortDate.substring(11, 16);
        newAgenda.setNeedTimeNum(neededTimeNum);
        newAgenda.setNeedTimeType(neededTimeType);
        newAgenda.setNeddTimeMills(SizeUtil.getTimeMills(neededTimeType, neededTimeNum));

        newAgenda.setContextId(selectedContext.getId());
        newAgenda.setProjectId(selectedProject == null ? 0l : selectedProject.getId());
        newAgenda.setAlarmDate(date);
        newAgenda.setAlarmTime(time);
        newAgenda.setAlarmTimeMills(alarmDate.getTime());
        newAgenda.setDelayTime(0);
        newAgenda.setDeleted(0);
        newAgenda.setFinished(0);
        newAgenda.setOverdued(0);
        Long agendaId;
        if (open_mode == MODE_MODIFY) {
            DaoUtil.daoSession.getAgendaEventDao().update(newAgenda);
            agendaId = newAgenda.getId();
        } else {
            agendaId = DaoUtil.daoSession.getAgendaEventDao().insert(newAgenda);
        }

        //添加至提醒表
        AgendaAlarmEvent alarmEvent;
        if (open_mode == MODE_MODIFY) {
            alarmEvent = DaoUtil.daoSession.getAgendaAlarmEventDao()
                    .queryBuilder().where(AgendaAlarmEventDao.Properties.AgendaId
                            .eq(newAgenda.getId()))
                    .build()
                    .list().get(0);
        } else {
            alarmEvent = new AgendaAlarmEvent();
        }
        alarmEvent.setAgendaId(agendaId);
        alarmEvent.setYear(Integer.valueOf(date.substring(0, 4)));
        alarmEvent.setMonth(Integer.valueOf(date.substring(5, 7)));
        String weekday = shortDate.substring(17, 19);
        switch (weekday) {
            case "周一":
                alarmEvent.setWeekday(AgendaAlarmEvent.WEEK_MON);
                break;
            case "周二":
                alarmEvent.setWeekday(AgendaAlarmEvent.WEEK_TUE);
                break;
            case "周三":
                alarmEvent.setWeekday(AgendaAlarmEvent.WEEK_WED);
                break;
            case "周四":
                alarmEvent.setWeekday(AgendaAlarmEvent.WEEK_THU);
                break;
            case "周五":
                alarmEvent.setWeekday(AgendaAlarmEvent.WEEK_FRI);
                break;
            case "周六":
                alarmEvent.setWeekday(AgendaAlarmEvent.WEEK_SAT);
                break;
            case "周日":
                alarmEvent.setWeekday(AgendaAlarmEvent.WEEK_SUN);
                break;
            default:
                break;
        }
        alarmEvent.setDay(Integer.valueOf(date.substring(8, 10)));
        alarmEvent.setHour(Integer.valueOf(time.substring(0, 2)));
        alarmEvent.setMinute(Integer.valueOf(time.substring(3, 5)));
        alarmEvent.setVibrative(agendaVibrate.isChecked() ? 1 : 0);
        alarmEvent.setIntervalTime(selectIntervalNum);
        alarmEvent.setIntervalType(selectTypeValue);
        alarmEvent.setNextAlarmDate(shortDate);
        if (open_mode == MODE_MODIFY) {
            DaoUtil.daoSession.getAgendaAlarmEventDao().update(alarmEvent);
            Message msg = Message.obtain();
            msg.what = AgendaEvent.AGENDA_UPDATE;
            messageManager.publishMessage(msg);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            DaoUtil.daoSession.getAgendaAlarmEventDao().insert(alarmEvent);

            /*//将收件箱来源事件删除
            DaoUtil.daoSession.getInboxEventDao().delete(inboxEvent);*/

            Message msg = Message.obtain();
            msg.what = InboxEvent.INBOX_REMOVED;
            msg.obj = inboxEvent;
            messageManager.publishMessage(msg);

            Intent intent = new Intent();
            intent.putExtra("new_agenda_return", "OK");
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void showCustomizeAlarmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_customize_alarm, null);
        numSpinner = (Spinner) view.findViewById(R.id.number_picker);
        final Spinner intervalSpinner = (Spinner) view.findViewById(R.id.interval_picker);
        alarmInDialog = (TextView) view.findViewById(R.id.select_alarm);
        saveToList = (CheckBox) view.findViewById(R.id.save_to_alarm_list);

        numAdapter = new MySpinnerAdapter(this, nums);
        numSpinner.setAdapter(numAdapter);
        MySpinnerAdapter intervalAdapter = new MySpinnerAdapter(this, types);
        intervalSpinner.setAdapter(intervalAdapter);

        numSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectIntervalNum = Integer.parseInt(
                        nums.get(numSpinner.getSelectedItemPosition()));
                setCustomizedAlarm(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        intervalSpinner.setOnItemSelectedListener(spinnerItemListener);

        builder.setTitle(getString(R.string.customize_alarm_title))
                .setView(view)
                .setPositiveButton(getString(R.string.select_alarm_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setCustomizedAlarm(true);
                        if (saveToList.isChecked()) {
                            saveAlarmToList();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.select_alarm_cancle), null)
                .show();
    }

    private AdapterView.OnItemSelectedListener spinnerItemListener
            = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (types.get(position)) {
                case "分钟":
                    selectType = "分钟";
                    selectTypeValue = AlarmType.TYPE_MINUTE;
                    break;
                case "小时":
                    selectType = "小时";
                    selectTypeValue = AlarmType.TYPE_HOUR;
                    break;
                case "天":
                    selectType = "天";
                    selectTypeValue = AlarmType.TYPE_DAY;
                    break;
                case "周":
                    selectType = "个星期";
                    selectTypeValue = AlarmType.TPYE_WEEK;
                    break;
                case "月":
                    selectType = "个月";
                    selectTypeValue = AlarmType.TPYE_MONTH;
                    break;
                case "年":
                    selectType = "年";
                    selectTypeValue = AlarmType.TPYE_YEAR;
                    break;
                default:
                    break;
            }
            selectIntervalNum = Integer.parseInt(nums.get(numSpinner.getSelectedItemPosition()));
            setCustomizedAlarm(false);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void initNumAndType() {
        nums = new ArrayList<>();
        for (int i = 0; i < 120; i++) {
            nums.add(i + 1 + "");
        }
        types = Arrays.asList("分钟", "小时", "天", "周", "月", "年");
        selectIntervalNum = 0;//默认间隔数量 0
        selectTypeValue = 0;//默认间隔单位 0 ;表示不重复
    }

    private void setCustomizedAlarm(boolean updateLocal) {
        alarmInDialog.setText(getString(R.string.selected_customize_alarm, selectIntervalNum, selectType));
        if (updateLocal) {
            agendaAlarmTypeText.setText(getString(R.string.per_num_time,selectIntervalNum,selectType));
        }
    }

    private void saveAlarmToList() {
        String alarmName = getString(R.string.per_num_time, selectIntervalNum, selectType);
        AlarmType newType = new AlarmType(null, alarmName, selectIntervalNum, selectTypeValue);
        alarmTypeDao.insert(newType);
        alarmTypes.add(newType);
        alarmTypeAdapter.notifyDataSetChanged();
    }

    private void switchRepeatTypeView() {
        if (hideAlarmType.getVisibility() == View.GONE) {
            hideAlarmType.setVisibility(View.VISIBLE);
        } else {
            hideAlarmType.setVisibility(View.GONE);
        }
    }

    private void switchDatePicker() {
        hideDatePickerView.setVisibility(hideDatePickerView
                .getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NEW_CONTEXT:
                    updateContextData();
                    break;
                case REQUEST_CHOOSE_PROJECT:
                    String proString = data.getStringExtra("project");
                    selectedProject = new Gson().fromJson(proString, ProjectEvent.class);
                    selectProject.setText(selectedProject.getName());
                    break;
                case REQUEST_CHOOSE_CONTEXT:
                    String contextString = data.getStringExtra("context");
                    selectedContext = new Gson().fromJson(contextString, ContextEvent.class);
                    agendaSelectedContext.setText(selectedContext.getName());
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateContextData() {
        contextEvents = contextEventDao
                .queryBuilder()
                .where(ContextEventDao.Properties.UpContextId.eq(0))
                .build()
                .list();
        if (contextEvents.size() > 0) {
            selectedContext = contextEvents.get(contextEvents.size() - 1);
        }
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, NewAgendaActivity.class);
    }

    @Override
    public void onNewMessage(Message msg) {
    }
}
