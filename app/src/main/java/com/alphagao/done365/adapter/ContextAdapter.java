package com.alphagao.done365.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.bean.ContextEvent;

import java.util.List;

/**
 * Created by Alpha on 2017/3/12.
 */

public class ContextAdapter extends BaseAdapter {
    private List<ContextEvent> contextEventList;

    public ContextAdapter(List<ContextEvent> contextEvents) {
        contextEventList = contextEvents;
    }

    public void setData(List<ContextEvent> contextEvents){
        contextEventList = contextEvents;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contextEventList.size();
    }

    @Override
    public Object getItem(int position) {
        return contextEventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView context = (TextView) View.inflate(parent.getContext(), R.layout.item_context_listview, null);
        context.setText(contextEventList.get(position).getName());
        return context;
    }
}
