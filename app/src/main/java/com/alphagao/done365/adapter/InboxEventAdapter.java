package com.alphagao.done365.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.message.MessageManager;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.ui.activity.MainActivity;
import com.alphagao.done365.ui.activity.NewAgendaActivity;
import com.alphagao.done365.ui.activity.NewTodoActivity;
import com.alphagao.done365.ui.activity.SingleFragmentActivity;
import com.alphagao.done365.ui.fragment.ChooseMemoFolderFragment;
import com.alphagao.done365.ui.model.InboxModel;
import com.alphagao.done365.ui.model.InboxModelDBImpl;
import com.alphagao.done365.utils.DisplayUtils;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alpha on 2017/3/2.
 */


public class InboxEventAdapter extends RecyclerView.Adapter<InboxEventAdapter.ViewHolder> {
    private static final String TAG = "InboxEventAdapter";
    private Context mContext;
    private List<InboxEvent> eventList;
    private InboxEvent inboxEvent;
    private FragmentActivity activity;
    private final InboxModel mInboxModel;
    private String searchStr;

    public InboxEventAdapter(List<InboxEvent> eventList, FragmentActivity activity, String searchStr) {
        this.eventList = eventList;
        this.activity = activity;
        this.searchStr = searchStr;
        mInboxModel = new InboxModelDBImpl();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inbox_recycview, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //确保得到正确的顺序
        final int truePosition = holder.getAdapterPosition();
        inboxEvent = eventList.get(truePosition);
        holder.inboxContent.setText(inboxEvent.getContent());
        holder.inboxLastTime.setText(inboxEvent.getCreateTime().toString());
        holder.inboxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInboxDialog(v, truePosition);
            }
        });
        holder.inbox2agenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, NewAgendaActivity.class);
                intent.putExtra("inbox", new Gson().toJson(eventList.get(truePosition)));
                activity.startActivityForResult(intent, MainActivity.REQUEST_NEW_AGENDA);
            }
        });
        holder.inbox2todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, NewTodoActivity.class);
                intent.putExtra("inbox", new Gson().toJson(eventList.get(truePosition)));
                activity.startActivityForResult(intent, MainActivity.REQUEST_NEW_TODO);
            }
        });
        holder.inbox2memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SingleFragmentActivity.class);
                intent.putExtra("inbox", new Gson().toJson(eventList.get(truePosition)));
                intent.putExtra(SingleFragmentActivity.FRAGMENT_PARAM
                        , ChooseMemoFolderFragment.class);
                activity.startActivityForResult(intent, MainActivity.REQUEST_NEW_MEMO);
            }
        });
        holder.inbox2trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeInbox(truePosition);
            }
        });

        if (searchStr != null) {
            setSearchStrHighLight(holder.inboxContent, searchStr);
        }
    }

    private void setSearchStrHighLight(TextView inboxContent, String searchStr) {
        SpannableStringBuilder builder = new SpannableStringBuilder(inboxContent.getText());
        Pattern p = Pattern.compile(searchStr);
        Matcher matcher = p.matcher(inboxContent.getText());
        while (matcher.find()) {
            builder.setSpan(new ForegroundColorSpan(
                            mContext.getResources().getColor(R.color.text_highlight_color)),
                    matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        inboxContent.setText(builder);
    }

    private void updateInboxDialog(View v, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        final EditText editText = new EditText(v.getContext());
        editText.setBackgroundColor(Color.WHITE);
        editText.setText(eventList.get(position).getContent());
        editText.setSelection(editText.getText().length());
        LinearLayout layout = new LinearLayout(v.getContext());
        int dp20 = (int) DisplayUtils.dp2Px(mContext, 20);
        layout.setPadding(dp20, dp20, dp20, dp20);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editText);
        builder.setTitle(mContext.getString(R.string.inbox_edit_text_title))
                .setView(layout)
                .setPositiveButton(mContext.getString(R.string.inbox_add_text_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(editText.getText().toString())) {
                            updateInboxEvent(editText.getText().toString(), inboxEvent);
                        }
                    }
                })
                .setNegativeButton(mContext.getString(R.string.inbox_edit_text_negative), null)
                .setCancelable(false)
                .show();
    }

    private void removeInbox(int position) {
        InboxEvent event = eventList.get(position);
        event.setDeleted(InboxEvent.YES);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        event.setDeleteTime(format.format(new Date()));
        mInboxModel.updateInbox(event);
        eventList.remove(event);
        //两个一起调用，有移除动画
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eventList.size() - position);

        Message msg = Message.obtain();
        msg.what = InboxEvent.INBOX_DELETED;
        msg.obj = event;
        MessageManager.getInstance().publishMessage(msg);
    }

    /**
     * 修改收件箱条目的内容
     *
     * @param newContent 新的内容
     * @param inboxEvent 要修改的条目对象
     */
    private void updateInboxEvent(String newContent, InboxEvent inboxEvent) {
        inboxEvent.setContent(newContent);
        mInboxModel.updateInbox(inboxEvent);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout inboxView;
        TextView inboxContent;
        TextView inboxLastTime;
        TextView inbox2agenda;
        TextView inbox2todo;
        TextView inbox2memo;
        TextView inbox2trash;

        public ViewHolder(View itemView) {
            super(itemView);
            inboxView = (RelativeLayout) itemView.findViewById(R.id.inbox_item_layout);
            inboxContent = (TextView) itemView.findViewById(R.id.inbox_item_content);
            inboxLastTime = (TextView) itemView.findViewById(R.id.inbox_item_time);
            inbox2agenda = (TextView) itemView.findViewById(R.id.inbox_menu_to_agenda);
            inbox2todo = (TextView) itemView.findViewById(R.id.inbox_menu_to_todo);
            inbox2memo = (TextView) itemView.findViewById(R.id.inbox_menu_to_memo);
            inbox2trash = (TextView) itemView.findViewById(R.id.inbox_menu_to_trash);
        }
    }
}
