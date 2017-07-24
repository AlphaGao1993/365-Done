package com.alphagao.done365.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.adapter.MySpinnerAdapter;
import com.alphagao.done365.ui.fragment.ChooseProjectFragment;
import com.alphagao.done365.ui.fragment.ContextFragment;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.ContextEvent;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.greendao.bean.ProjectEvent;
import com.alphagao.done365.greendao.bean.TodoEvent;
import com.alphagao.done365.utils.SizeUtil;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewTodoActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CHOOSE_CONTEXT = 0x00012;
    private static final int REQUEST_CHOOSE_PROJECT = 0x00013;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.todoContentText)
    EditText todoContentText;
    @BindView(R.id.deadlineDateTime)
    TextView deadlineDateTime;
    @BindView(R.id.deadlineLayout)
    LinearLayout deadlineLayout;
    @BindView(R.id.deadTimePicker)
    SingleDateAndTimePicker deadTimePicker;
    @BindView(R.id.datePickerLayout)
    LinearLayout datePickerLayout;
    @BindView(R.id.todoNeedTime)
    TextView todoNeedTime;
    @BindView(R.id.todoNeedLayout)
    LinearLayout todoNeedLayout;
    @BindView(R.id.todoSelectedContext)
    TextView todoSelectedContext;
    @BindView(R.id.addContextLayout)
    LinearLayout addContextLayout;
    @BindView(R.id.selectedProject)
    TextView selectProjectView;
    @BindView(R.id.selectProjectLayout)
    LinearLayout selectProjectLayout;
    @BindView(R.id.todoDeadlineAlarm)
    Switch todoDeadlineAlarm;
    @BindView(R.id.addTodoToList)
    Button addTodoToList;
    @BindView(R.id.activity_new_todo)
    CoordinatorLayout activityNewTodo;
    private String selectDeadTimeStr;
    private SimpleDateFormat dateFormat;
    private List<String> numbers;
    private List<String> types;
    private ContextEvent selectedContext;
    private long needTimeMills = 1000 * 60 * 60;//默认持续一小时
    private InboxEvent inboxEvent;
    private ProjectEvent selectedProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_todo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.arrow_back_white_24dp);
        }
        initContentAndDate();
        initNeedData();
        initViewListener();
    }

    private void initContentAndDate() {
        Intent intent = getIntent();
        String inboxJson = intent.getStringExtra("inbox");
        inboxEvent = new Gson().fromJson(inboxJson, InboxEvent.class);
        todoContentText.setText(inboxEvent.getContent());
        todoContentText.setSelection(inboxEvent.getContent().length());

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm EE");
        long date = new Date().getTime() + 1000 * 60 * 60 * 24;//默认截止时间一天后
        Date initData = new Date();
        initData.setTime(date);
        selectDeadTimeStr = dateFormat.format(initData);
        deadlineDateTime.setText(selectDeadTimeStr);
    }

    private void initNeedData() {
        numbers = new ArrayList<>();
        for (int i = 0; i < 120; i++) {
            numbers.add(i + 1 + "");
        }
        types = Arrays.asList(new String[]{
                "分钟", "小时", "天", "周", "月"
        });
    }

    private void initViewListener() {
        //时间选择器监听
        deadTimePicker.setListener(new SingleDateAndTimePicker.Listener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                selectDeadTimeStr = dateFormat.format(date);
                deadlineDateTime.setText(selectDeadTimeStr);
            }
        });

        deadlineLayout.setOnClickListener(this);
        todoNeedLayout.setOnClickListener(this);
        addContextLayout.setOnClickListener(this);
        selectProjectLayout.setOnClickListener(this);
    }

    @OnClick({R.id.deadlineLayout, R.id.todoNeedLayout, R.id.addContextLayout,
            R.id.selectProjectLayout, R.id.addTodoToList})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.deadlineLayout:
                switchDeadLineTimeView();
                break;
            case R.id.todoNeedLayout:
                showDateDialog();
                break;
            case R.id.selectProjectLayout:
                chooseProject();
                break;
            case R.id.addTodoToList:
                saveTodoEvent();
                break;
            case R.id.addContextLayout:
                requestChooseContext();
                break;
            default:
                break;
        }
    }

    private void chooseProject() {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.FRAGMENT_PARAM, ChooseProjectFragment.class);
        startActivityForResult(intent, REQUEST_CHOOSE_PROJECT);
    }

    private void saveTodoEvent() {
        if (selectedContext == null) {
            toast(getString(R.string.choose_relative_context));
            return;
        }
        TodoEvent todoEvent = new TodoEvent();
        todoEvent.setContent(todoContentText.getText().toString());
        todoEvent.setNeedTimes(needTimeMills);
        todoEvent.setContextId(selectedContext.getId());
        todoEvent.setDueDate(selectDeadTimeStr);
        todoEvent.setProjectId(selectedProject == null ? null : selectedProject.getId());
        todoEvent.setDeadAlarm(todoDeadlineAlarm.isChecked() ? TodoEvent.YES : TodoEvent.NO);
        todoEvent.setOverdued(TodoEvent.NO);
        todoEvent.setFinished(TodoEvent.NO);
        todoEvent.setDeleted(TodoEvent.NO);
        long id = DaoUtil.daoSession.getTodoEventDao().insert(todoEvent);
        if (id > 0) {
            toast(getString(R.string.new_todo_ok));
            publishInboxRemoved(inboxEvent);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void publishInboxRemoved(InboxEvent inboxEvent) {
        Message msg = Message.obtain();
        msg.what = InboxEvent.INBOX_REMOVED;
        msg.obj = inboxEvent;
        messageManager.publishMessage(msg);
    }

    private void switchDeadLineTimeView() {
        if (datePickerLayout.getVisibility() == View.VISIBLE) {
            datePickerLayout.setVisibility(View.GONE);
        } else {
            datePickerLayout.setVisibility(View.VISIBLE);
        }
    }

    private void requestChooseContext() {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.FRAGMENT_PARAM, ContextFragment.class);
        startActivityForResult(intent, REQUEST_CHOOSE_CONTEXT);
    }

    private void showDateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_need_time, null);
        final Spinner numPicker = (Spinner) view.findViewById(R.id.number_picker);
        final Spinner typePicker = (Spinner) view.findViewById(R.id.type_picker);
        numPicker.setAdapter(new MySpinnerAdapter(this, numbers));
        typePicker.setAdapter(new MySpinnerAdapter(this, types));
        builder.setTitle(getString(R.string.set_need_time_title))
                .setView(view)
                .setPositiveButton(R.string.set_need_time_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        needTimeMills = 0;
                        calcNeedTimeMills(typePicker, numPicker);
                        todoNeedTime.setText(numPicker.getSelectedItem().toString()
                                + " " + typePicker.getSelectedItem().toString());
                    }
                })
                .show();
    }

    private void calcNeedTimeMills(Spinner typePicker, Spinner numPicker) {
        needTimeMills = SizeUtil.getTimeMills(typePicker.getSelectedItem().toString(),
                numPicker.getSelectedItemPosition() + 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CHOOSE_CONTEXT:
                    String contextString = data.getStringExtra("context");
                    selectedContext = new Gson().fromJson(contextString, ContextEvent.class);
                    todoSelectedContext.setText(selectedContext.getName());
                    break;
                case REQUEST_CHOOSE_PROJECT:
                    String proString = data.getStringExtra("project");
                    selectedProject = new Gson().fromJson(proString, ProjectEvent.class);
                    selectProjectView.setText(selectedProject.getName());
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNewMessage(Message msg) {

    }
}
