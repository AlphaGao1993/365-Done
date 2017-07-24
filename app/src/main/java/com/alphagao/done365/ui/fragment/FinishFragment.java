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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Alpha on 2017/3/5.
 */


public class FinishFragment extends BaseFragment {

    @BindView(R.id.frag_finish_tab)
    TabLayout fragFinishTab;
    @BindView(R.id.frag_finish_pager)
    ViewPager fragFinishPager;
    Unbinder unbinder;
    private List<String> titleList;
    private List<Fragment> fragmentList;
    private int showPosition = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finish, container, false);
        unbinder = ButterKnife.bind(this, view);
        titleList = Arrays.asList(getResources().getStringArray(R.array.finshTypeList));
        fragFinishTab.addTab(fragFinishTab.newTab().setText(titleList.get(0)));
        fragFinishTab.addTab(fragFinishTab.newTab().setText(titleList.get(1)));
        AgendaDoneFragment agendaDoneFragment = (AgendaDoneFragment) FragmentFactory
                .create(getActivity(), AgendaDoneFragment.class);
        TodoDoneFragment todoDoneFragment = (TodoDoneFragment) FragmentFactory
                .create(getActivity(), TodoDoneFragment.class);
        fragmentList = new ArrayList<>();
        fragmentList.add(agendaDoneFragment);
        fragmentList.add(todoDoneFragment);
        fragFinishPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
            }
        });
        fragFinishTab.setupWithViewPager(fragFinishPager);
        fragFinishTab.setTabMode(TabLayout.MODE_FIXED);
        fragFinishPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        });
        return view;
    }

    private void requestChangeMenu(int position) {
        Message msg = Message.obtain();
        msg.what = MainActivity.CHANGE_MENU_GROUP;
        msg.obj = fragmentList.get(position).getClass().getName();
        messageManager.publishMessage(msg);
    }

    @Override
    public void onNewMessage(Message msg) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            requestChangeMenu(showPosition);
        }
    }
}
