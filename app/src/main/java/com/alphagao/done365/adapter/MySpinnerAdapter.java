package com.alphagao.done365.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.alphagao.done365.R;

import java.util.List;

/**
 * Created by Alpha on 2017/3/16.
 */

public class MySpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    Context context;
    List<String> values;

    public MySpinnerAdapter(Context context, List<String> values) {
        this.context = context;
        this.values = values;
    }

    public void setValues(List<String> values) {
        this.values = values;
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if (convertView != null) {
            view = (TextView) convertView;
        } else {
            view = (TextView) View.inflate(context, R.layout.item_usual_spinner, null);
        }
        view.setText(values.get(position));
        return view;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if (convertView != null) {
            view = (TextView) convertView;
        } else {
            view = (TextView) View.inflate(context, R.layout.item_usual_spinner, null);
        }
        view.setText(values.get(position));
        return view;
    }

}
