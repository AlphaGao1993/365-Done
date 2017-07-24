package com.alphagao.done365.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alphagao.done365.R;
import com.alphagao.done365.ui.activity.MainActivity;
import com.alphagao.done365.widget.MyViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alpha on 2017/3/2.
 */


public class EventFragment extends BaseFragment {

    private static final String TAG = "EventFragment";
    @BindView(R.id.frag_event_tab)
    TabLayout fragEventTab;
    @BindView(R.id.frag_event_pager)
    MyViewPager fragEventPager;
    private List<String> eventTabList;
    private List<Fragment> eventFragmentList;
    private int showPosition = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        ButterKnife.bind(this, view);
        eventTabList = Arrays.asList((getResources().getStringArray(R.array.eventTypeList)));
        fragEventTab.addTab(fragEventTab.newTab().setText(eventTabList.get(0)));
        fragEventTab.addTab(fragEventTab.newTab().setText(eventTabList.get(1)));
        fragEventTab.addTab(fragEventTab.newTab().setText(eventTabList.get(2)));

        AgendaFragment agendaFragment = (AgendaFragment) FragmentFactory
                .create(getActivity(), AgendaFragment.class);
        ToDoFragment todoFragment = (ToDoFragment) FragmentFactory
                .create(getActivity(), ToDoFragment.class);
        ProjectFragment projectFragment = (ProjectFragment) FragmentFactory
                .create(getActivity(), ProjectFragment.class);
        eventFragmentList = new ArrayList<>();
        eventFragmentList.add(agendaFragment);
        eventFragmentList.add(todoFragment);
        eventFragmentList.add(projectFragment);
        fragEventPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return eventTabList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return eventFragmentList.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return eventTabList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
            }
        });
        fragEventTab.setupWithViewPager(fragEventPager);
        fragEventTab.setTabMode(TabLayout.MODE_FIXED);
        fragEventPager.setOffscreenPageLimit(2);
        fragEventPager.addOnPageChangeListener(new PageChangeListener());
        fragEventPager.setHorizontalScrollBarEnabled(false);
        fragEventPager.setDisenableScroll(true);
        return view;
    }

    @Override
    public void onNewMessage(Message msg) {

    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            showPosition = position;
            requestChangeMenu(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void requestChangeMenu(int position) {
        Message msg = Message.obtain();
        msg.what = MainActivity.CHANGE_MENU_GROUP;
        msg.obj = eventFragmentList.get(position).getClass().getName();
        messageManager.publishMessage(msg);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            requestChangeMenu(showPosition);
        }
    }
}
