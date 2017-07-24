package com.alphagao.done365.adapter;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.message.MessageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alpha on 2017/4/8.
 */

public class TrashEventAdapter extends RecyclerView.Adapter<TrashEventAdapter.ViewHolder> {

    private List<InboxEvent> deletedInboxs;
    private Context context;
    private List<InboxEvent> revokeInboxs;
    private String searchStr;

    public TrashEventAdapter(List<InboxEvent> deletedInboxs, String searchStr) {
        this.deletedInboxs = deletedInboxs;
        this.searchStr = searchStr;
        revokeInboxs = new ArrayList<>();
    }

    public List<InboxEvent> getRevokeInboxs() {
        return revokeInboxs;
    }

    public void setDeletedInboxs(List<InboxEvent> deletedInboxs) {
        this.deletedInboxs = deletedInboxs;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deleted_inbox_recycview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final InboxEvent event = deletedInboxs.get(position);
        holder.revoke.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    revokeInboxs.add(event);
                } else {
                    revokeInboxs.remove(event);
                }
            }
        });
        holder.title.setText(event.getContent());

        if (searchStr != null) {
            setSearchStrHighLight(holder.title, searchStr);
            holder.revokeView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    event.setDeleted(InboxEvent.NO);
                    deletedInboxs.remove(event);
                    notifyDataSetChanged();
                    Message msg = Message.obtain();
                    msg.what = InboxEvent.INBOX_REVOKE;
                    MessageManager.getInstance().publishMessage(msg);
                    return true;
                }
            });
        }

        holder.createTime.setText(context.getString(R.string.create_at,
                event.getCreateTime().substring(0, 16)));
        holder.deleteTime.setText(context.getString(R.string.delete_at,
                event.getDeleteTime().substring(0, 16)));
        holder.revokeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = holder.revoke.isChecked();
                holder.revoke.setChecked(!checked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deletedInboxs.size();
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
        RelativeLayout revokeView;
        CheckBox revoke;
        TextView title;
        TextView createTime;
        TextView deleteTime;

        public ViewHolder(View itemView) {
            super(itemView);
            revokeView = (RelativeLayout) itemView.findViewById(R.id.revoke_view);
            revoke = (CheckBox) itemView.findViewById(R.id.revoke);
            title = (TextView) itemView.findViewById(R.id.inbox_title);
            createTime = (TextView) itemView.findViewById(R.id.create_time);
            deleteTime = (TextView) itemView.findViewById(R.id.delete_time);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.revoke.setChecked(false);
    }
}
