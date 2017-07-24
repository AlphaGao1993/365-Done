package com.alphagao.done365.ui.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.adapter.AgendaEventAdapter;
import com.alphagao.done365.engines.PastTodayEngine;
import com.alphagao.done365.greendao.bean.AgendaEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alpha on 2017/4/14.
 */

public class PastTodayActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pastTodayAgendaView)
    RecyclerView pastTodayAgendaView;
    @BindView(R.id.noEventView)
    TextView noEventView;
    private List<AgendaEvent> todayAgendas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_today);
        ButterKnife.bind(this);
        setupActionBar();
        initView();
        initData();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.arrow_back_white_24dp);
        }
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        pastTodayAgendaView.setLayoutManager(manager);
    }

    private void initData() {
        todayAgendas = PastTodayEngine.getPastTodayAgendas();

        AgendaEventAdapter adapter = new AgendaEventAdapter(todayAgendas,
                AgendaEventAdapter.MODE_SHOW, null);
        pastTodayAgendaView.setAdapter(adapter);

        if (todayAgendas.size() > 0) {
            noEventView.setVisibility(View.GONE);
            pastTodayAgendaView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNewMessage(Message msg) {

    }
}
