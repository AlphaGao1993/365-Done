package com.alphagao.done365.ui.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.service.AlarmService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alpha on 2017/4/14.
 */

public class AppConfigActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.startAgendaAlarmView)
    TextView startAgendaAlarmView;
    @BindView(R.id.startAgendaAlarmLabel)
    TextView startAgendaAlarmLabel;
    @BindView(R.id.startAgendaAlarmSwitch)
    Switch startAgendaAlarmSwitch;
    @BindView(R.id.startAgendaAlarmLayout)
    RelativeLayout startAgendaAlarmLayout;
    @BindView(R.id.showPastEventInMonthView)
    TextView showPastEventInMonthView;
    @BindView(R.id.showPastEventInMonthLabel)
    TextView showPastEventInMonthLabel;
    @BindView(R.id.showPastEventInMonthSwitch)
    Switch showPastEventInMonthSwitch;
    @BindView(R.id.showPastEventInMonthLayout)
    RelativeLayout showPastEventInMonthLayout;
    @BindView(R.id.startPastTodayView)
    TextView startPastTodayView;
    @BindView(R.id.startPastTodayLabel)
    TextView startPastTodayLabel;
    @BindView(R.id.startPastTodaySwitch)
    Switch startPastTodaySwitch;
    @BindView(R.id.startPastTodayLayout)
    RelativeLayout startPastTodayLayout;
    @BindView(R.id.speechHasSymbolView)
    TextView speechHasSymbolView;
    @BindView(R.id.speechHasSymbolLabel)
    TextView speechHasSymbolLabel;
    @BindView(R.id.speechHasSymbolSwitch)
    Switch speechHasSymbolSwitch;
    @BindView(R.id.speechHasSymbolLayout)
    RelativeLayout speechHasSymbolLayout;
    @BindView(R.id.inboxClearAlarmView)
    TextView inboxClearAlarmView;
    @BindView(R.id.inboxClearAlarmLabel)
    TextView inboxClearAlarmLabel;
    @BindView(R.id.inboxClearAlarmSwitch)
    Switch inboxClearAlarmSwitch;
    @BindView(R.id.inboxClearAlarmLayout)
    RelativeLayout inboxClearAlarmLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
        setupActionBar();
        initViewAndState();
    }

    private void initViewAndState() {
        startAgendaAlarmSwitch.setOnCheckedChangeListener(listener);
        startPastTodaySwitch.setOnCheckedChangeListener(listener);
        showPastEventInMonthSwitch.setOnCheckedChangeListener(listener);
        speechHasSymbolSwitch.setOnCheckedChangeListener(listener);
        inboxClearAlarmSwitch.setOnCheckedChangeListener(listener);

        startAgendaAlarmSwitch.setChecked(userPrefs.getBoolean("startAgendaAlarm", true));
        switchStartAgendaAlarm(startAgendaAlarmSwitch.isChecked());

        startPastTodaySwitch.setChecked(userPrefs.getBoolean("startPastToday", true));
        switchStartPastToday(startPastTodaySwitch.isChecked());

        showPastEventInMonthSwitch.setChecked(userPrefs.getBoolean("showPastEventInMonth", true));
        switchShowPastEventInMonth(showPastEventInMonthSwitch.isChecked());

        speechHasSymbolSwitch.setChecked(userPrefs.getBoolean("speechHasSymbol"));
        switchSpeechHasSymbol(speechHasSymbolSwitch.isChecked());

        inboxClearAlarmSwitch.setChecked(userPrefs.getBoolean("startInboxClearAlarm", true));
        switchStartInboxClearAlarm(inboxClearAlarmSwitch.isChecked());
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.arrow_back_white_24dp);
        }
    }

    @OnClick({R.id.startAgendaAlarmLayout, R.id.showPastEventInMonthLayout,
            R.id.startPastTodayLayout, R.id.speechHasSymbolLayout,
            R.id.inboxClearAlarmLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.startAgendaAlarmLayout:
                startAgendaAlarmSwitch.setChecked(!startAgendaAlarmSwitch.isChecked());
                break;
            case R.id.showPastEventInMonthLayout:
                showPastEventInMonthSwitch.setChecked(!showPastEventInMonthSwitch.isChecked());
                break;
            case R.id.startPastTodayLayout:
                startPastTodaySwitch.setChecked(!startPastTodaySwitch.isChecked());
                break;
            case R.id.speechHasSymbolLayout:
                speechHasSymbolSwitch.setChecked(!speechHasSymbolSwitch.isChecked());
                break;
            case R.id.inboxClearAlarmLayout:
                inboxClearAlarmSwitch.setChecked(!inboxClearAlarmSwitch.isChecked());
                break;
            default:
                break;
        }
    }

    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton
            .OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.startAgendaAlarmSwitch:
                    switchStartAgendaAlarm(isChecked);
                    break;
                case R.id.startPastTodaySwitch:
                    switchStartPastToday(isChecked);
                    break;
                case R.id.showPastEventInMonthSwitch:
                    switchShowPastEventInMonth(isChecked);
                    break;
                case R.id.speechHasSymbolSwitch:
                    switchSpeechHasSymbol(isChecked);
                    break;
                case R.id.inboxClearAlarmSwitch:
                    switchStartInboxClearAlarm(isChecked);
                    break;
                default:
                    break;
            }
        }
    };

    private void switchShowPastEventInMonth(boolean isChecked) {
        showPastEventInMonthLabel.setText(isChecked ?
                getString(R.string.showPastEventInMonth_true)
                : getString(R.string.showPastEventInMonth_false));
        userPrefs.putBoolean("showPastEventInMonth", isChecked);
    }

    private void switchStartPastToday(boolean isChecked) {
        startPastTodayLabel.setText(isChecked ?
                getString(R.string.startPastToday_true)
                : getString(R.string.startPastToday_false));
        userPrefs.putBoolean("startPastToday", isChecked);
    }

    private void switchStartAgendaAlarm(boolean isChecked) {
        startAgendaAlarmLabel.setText(isChecked
                ? getString(R.string.startAgendaAlarm_true)
                : getString(R.string.startAgendaAlarm_false));
        userPrefs.putBoolean("startAgendaAlarm", isChecked);
        AlarmService.setServiceAlarm(this, isChecked);
    }

    private void switchSpeechHasSymbol(boolean isChecked) {
        speechHasSymbolLabel.setText(isChecked ?
                getString(R.string.speechHasSymbol_true)
                : getString(R.string.speechHasSymbol_false));
        userPrefs.putBoolean("speechHasSymbol", isChecked);
    }

    private void switchStartInboxClearAlarm(boolean isChecked) {
        inboxClearAlarmLabel.setText(isChecked ?
                getString(R.string.inboxClearAlarm_true)
                : getString(R.string.inboxClearAlarm_false));
        userPrefs.putBoolean("startInboxClearAlarm", isChecked);
    }

    @Override
    public void onNewMessage(Message msg) {

    }
}
