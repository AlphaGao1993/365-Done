package com.alphagao.done365.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alphagao.done365.R;
import com.alphagao.done365.adapter.EventDondAdapter;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.gen.AgendaEventDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Alpha on 2017/4/7.
 */

public class AgendaDoneFragment extends BaseFragment {
    @BindView(R.id.doneAgendaLayout)
    RecyclerView doneAgendaLayout;
    Unbinder unbinder;
    private AgendaEventDao agendaEventDao;
    private List<AgendaEvent> agendaEvents;
    private EventDondAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_done, null, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        initView();
        return view;
    }

    private void initData() {
        agendaEventDao = DaoUtil.daoSession.getAgendaEventDao();

        agendaEvents = agendaEventDao
                .queryBuilder()
                .where(AgendaEventDao.Properties.Finished.eq(AgendaEvent.YES))
                .orderDesc(AgendaEventDao.Properties.FinishTime)
                .build()
                .list();
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        doneAgendaLayout.setLayoutManager(manager);

        adapter = new EventDondAdapter(agendaEvents);
        doneAgendaLayout.setAdapter(adapter);
    }

    @Override
    public void onNewMessage(Message msg) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
