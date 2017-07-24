package com.alphagao.done365.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.bean.MemoEvent;
import com.alphagao.done365.ui.activity.SingleFragmentActivity;
import com.alphagao.done365.ui.fragment.MemoDetailFragment;
import com.google.gson.Gson;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alpha on 2017/4/27.
 */

public class MemoEventAdapter extends RecyclerView.Adapter<MemoEventAdapter.ViewHolder> {

    private List<MemoEvent> memoEvents;
    private String searchStr;
    private Context context;

    public MemoEventAdapter(Context context, List<MemoEvent> memoEvents, String searchStr) {
        this.context = context;
        this.memoEvents = memoEvents;
        this.searchStr = searchStr;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_memo_recyclerview, null, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MemoEvent event = memoEvents.get(holder.getAdapterPosition());
        holder.content.setText(event.getContent());
        if (searchStr != null) {
            setSearchStrHighLight(holder.content, searchStr);
        }
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleFragmentActivity.class);
                intent.putExtra(SingleFragmentActivity.FRAGMENT_PARAM, MemoDetailFragment.class);
                intent.putExtra("memo", new Gson().toJson(event));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return memoEvents.size();
    }

    private void setSearchStrHighLight(TextView inboxContent, String searchStr) {
        SpannableStringBuilder builder = new SpannableStringBuilder(inboxContent.getText());
        Pattern p = Pattern.compile(searchStr);
        Matcher matcher = p.matcher(inboxContent.getText());
        while (matcher.find()) {
            builder.setSpan(new ForegroundColorSpan(
                            context.getResources().getColor(R.color.text_highlight_color)),
                    matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        inboxContent.setText(builder);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout item;
        TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (RelativeLayout) itemView;
            content = (TextView) itemView.findViewById(R.id.memoContent);
        }
    }

}
