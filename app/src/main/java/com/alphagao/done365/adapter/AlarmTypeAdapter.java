package com.alphagao.done365.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.bean.AlarmType;

import java.util.List;

/**
 * Created by Alpha on 2017/3/12.
 */

public class AlarmTypeAdapter extends BaseAdapter {

    private List<AlarmType> alarmTypeList;

    public AlarmTypeAdapter(List<AlarmType> alarmTypes) {
        alarmTypeList = alarmTypes;
    }

    @Override
    public int getCount() {
        return alarmTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView context = (TextView) View.inflate(parent.getContext(), R.layout.item_context_listview, null);
        context.setText(alarmTypeList.get(position).getName());
        return context;
    }
}
