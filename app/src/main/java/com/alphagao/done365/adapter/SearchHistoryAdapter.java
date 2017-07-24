package com.alphagao.done365.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alphagao.done365.R;

import java.util.List;

/**
 * Created by Alpha on 2017/4/25.
 */

public class SearchHistoryAdapter extends BaseAdapter {

    private List<String> histories;

    public SearchHistoryAdapter(List<String> histories) {
        this.histories = histories;
    }

    @Override
    public int getCount() {
        return histories.size();
    }

    @Override
    public Object getItem(int position) {
        return histories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_history, null);
        TextView textView = (TextView) view.findViewById(R.id.searchHistoryContent);
        textView.setText(histories.get(position));
        return view;
    }
}
