package com.alphagao.done365.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.adapter.FestivalAdapter;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.Festival;
import com.alphagao.done365.greendao.gen.FestivalDao;
import com.alphagao.done365.utils.MyToast;
import com.alphagao.done365.widget.SwipeRecyclerView;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

/**
 * Created by Alpha on 2017/4/12.
 */

public class FestivalFragment extends BaseFragment {

    @BindView(R.id.festivalView)
    SwipeRecyclerView festivalView;
    Unbinder unbinder;
    private FestivalDao festivalDao;
    private List<Festival> festivalList;
    private FestivalAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_festival, container, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        initToolbar("自定义节假日");
        initFestivalData();
        initView();
        return view;
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        festivalView.setLayoutManager(manager);
    }

    private void initFestivalData() {
        festivalDao = DaoUtil.daoSession.getFestivalDao();
        updateData();
        adapter = new FestivalAdapter(festivalList);
        festivalView.setAdapter(adapter);
    }

    private void updateData() {
        festivalList = festivalDao.queryBuilder().build().list();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.customize_festival, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_festival:
                showCreateFestivalDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCreateFestivalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = View.inflate(getContext(), R.layout.dialog_customize_festival, null);
        final EditText fes_name = (EditText) view.findViewById(R.id.festival_name);
        final TextView fes_date = (TextView) view.findViewById(R.id.festival_date);
        fes_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarPicker(fes_date);
            }
        });
        builder.setView(view).setTitle("自定义节日")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = fes_name.getText().toString();
                        String date = fes_date.getText().toString();
                        dialog.dismiss();
                        updateFestival(name, date);
                    }
                }).show();
    }

    private void updateFestival(String name, String date) {
        if (TextUtils.isEmpty(name)) {
            MyToast.toast("你还没有输入节日名称~");
            return;
        }
        Festival festival = new Festival(null, name, date);
        festivalDao.insert(festival);
        festivalList.add(0, festival);
        adapter.notifyItemInserted(0);
        adapter.notifyItemRangeChanged(0, festivalList.size());
    }

    private void showCalendarPicker(final TextView fes_date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        DatePicker datePicker = new DatePicker(getContext());
        datePicker.setMode(DPMode.SINGLE);
        datePicker.setDate(Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH) + 1);
        final AlertDialog dialog = builder.setView(datePicker)
                .setCancelable(false)
                .create();
        datePicker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                MyToast.toast(date);
                fes_date.setText(date);
                dialog.dismiss();
            }
        });
        dialog.show();
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
