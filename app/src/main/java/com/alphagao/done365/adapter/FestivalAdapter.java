package com.alphagao.done365.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.Festival;
import com.alphagao.done365.greendao.gen.FestivalDao;
import com.alphagao.done365.utils.MyToast;
import com.alphagao.done365.widget.SwipeItemLayout;

import java.util.Calendar;
import java.util.List;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

/**
 * Created by Alpha on 2017/4/12.
 */

public class FestivalAdapter extends RecyclerView.Adapter<FestivalAdapter.ViewHolder> {

    private List<Festival> festivalList;
    private Festival festival;
    private Context mContext;
    private final FestivalDao festivalDao;

    public FestivalAdapter(List<Festival> festivalList) {
        this.festivalList = festivalList;
        festivalDao = DaoUtil.daoSession.getFestivalDao();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_festival_recycview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int mPosition = holder.getAdapterPosition();
        festival = festivalList.get(mPosition);
        holder.name.setText(festival.getName());
        holder.date.setText(festival.getDate());
        holder.festivalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogModifyFestival(mPosition);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFestival(mPosition);
            }
        });
    }

    private void deleteFestival(int mPosition) {
        festivalDao.delete(festivalList.get(mPosition));
        festivalList.remove(mPosition);
        notifyItemRemoved(mPosition);
        notifyItemRangeChanged(mPosition, festivalList.size() - mPosition);
    }

    private void showDialogModifyFestival(final int mPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.dialog_customize_festival, null);
        final EditText fes_name = (EditText) view.findViewById(R.id.festival_name);
        final TextView fes_date = (TextView) view.findViewById(R.id.festival_date);
        fes_name.setText(festival.getName());
        fes_date.setText(festival.getDate());
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
                        updateFestival(fes_name, fes_date, mPosition);
                        dialog.dismiss();
                    }
                }).show();
    }

    private void updateFestival(EditText fes_name, TextView fes_date, int mPosition) {
        String name = fes_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            MyToast.toast("你还没有输入节日名称~");
            return;
        }
        festival.setName(name);
        festival.setDate(fes_date.getText().toString());
        festivalDao.update(festival);
        notifyItemChanged(mPosition);
    }

    private void showCalendarPicker(final TextView fes_date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        DatePicker datePicker = new DatePicker(mContext);
        datePicker.setMode(DPMode.SINGLE);
        datePicker.setDate(Calendar.getInstance().get(Calendar.YEAR)
                , Calendar.getInstance().get(Calendar.MONTH) + 1);
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
    public int getItemCount() {
        return festivalList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        SwipeItemLayout festivalView;
        TextView name;
        TextView date;
        TextView delete;

        public ViewHolder(View itemView) {
            super(itemView);
            festivalView = (SwipeItemLayout) itemView;
            name = (TextView) itemView.findViewById(R.id.festival_name);
            date = (TextView) itemView.findViewById(R.id.festival_date);
            delete = (TextView) itemView.findViewById(R.id.festival_to_trash);
        }
    }
}
