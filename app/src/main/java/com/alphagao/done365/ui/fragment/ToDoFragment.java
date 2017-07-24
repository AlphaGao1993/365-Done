package com.alphagao.done365.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.alphagao.done365.R;
import com.alphagao.done365.adapter.TodoEventAdapter;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.TodoEvent;
import com.alphagao.done365.greendao.gen.TodoEventDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alpha on 2017/3/9.
 */

public class ToDoFragment extends BaseFragment {

    private static int sortByDeadDate = 46702;
    private static int sortByNeedTimeMills = 46703;

    @BindView(R.id.todo_ListView)
    RecyclerView todoListView;
    private TodoEventDao todoEventDao;
    private TodoEventAdapter todoEventAdapter;
    private List<TodoEvent> todoEvents;
    private int sortMode = sortByDeadDate;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();


        return view;
    }

    private void initData() {
        todoEvents = new ArrayList<>();
        todoEventAdapter = new TodoEventAdapter(todoEvents, null);
        todoListView.setAdapter(todoEventAdapter);

        updateTodoData(0);
    }

    private void initView() {
        todoEventDao = DaoUtil.daoSession.getTodoEventDao();

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        todoListView.setLayoutManager(manager);
    }

    @Override
    public void onNewMessage(Message msg) {
        switch (msg.what) {
            case TodoEvent.TODO_UPDATE:
                updateTodoData(0);
                break;
            case TodoEvent.TODO_SORT:
                showSortDialog();
                break;
            default:
                break;
        }
    }

    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_todo_sort, null);

        builder.setTitle("待办排序")
                .setView(view)
                .setCancelable(true)
                .setNegativeButton("取消", null);

        RadioGroup sortGroup = (RadioGroup) view.findViewById(R.id.agenda_sort_group);
        int todo_sort_id = userPrefs.getInt("todo_sort_by");
        sortGroup.check(todo_sort_id != -1 ? todo_sort_id : R.id.byDeadTime);

        final AlertDialog dialog = builder.create();
        sortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.byDeadTime:
                        updateTodoData(sortByDeadDate);
                        break;
                    case R.id.byNeedTime:
                        updateTodoData(sortByNeedTimeMills);
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateTodoData(int sortBy) {
        if (sortBy <= 0) {
            sortBy = userPrefs.getInt("todo_sort_by");
            if (sortBy <= 0) {
                sortBy = sortByDeadDate;
            }
        }
        sortMode = sortBy;
        if (todoEventDao != null) {
            QueryBuilder<TodoEvent> builder = todoEventDao
                    .queryBuilder()
                    .where(TodoEventDao.Properties.Finished.eq(TodoEvent.NO));
            switch (sortBy) {
                case 1://提醒日期排序
                    builder.orderAsc(TodoEventDao.Properties.DueDate);
                    break;
                case 2://按持续时间排序
                    builder.orderAsc(TodoEventDao.Properties.NeedTimes);
                    break;
                default:
                    break;
            }

            todoEvents.clear();
            todoEvents.addAll(builder.build().list());
            todoEventAdapter.notifyDataSetChanged();
        }
        userPrefs.putInt("todo_sort_by", sortMode);
    }
}
