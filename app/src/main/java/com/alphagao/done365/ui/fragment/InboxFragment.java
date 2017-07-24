package com.alphagao.done365.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.adapter.InboxEventAdapter;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.ui.presenter.InboxPresenter;
import com.alphagao.done365.ui.view.DataListener;
import com.alphagao.done365.ui.view.InboxViewInterface;
import com.alphagao.done365.utils.DisplayUtils;
import com.alphagao.done365.widget.SwipeRecyclerView;
import com.iflytech.Speech;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alpha on 2017/3/2.
 */


public class InboxFragment extends BaseFragment implements InboxViewInterface {
    public static final int SHOW = 7901;
    private static final String TAG = "InboxFragment";
    @BindView(R.id.inbox_list)
    SwipeRecyclerView inboxList;
    @BindView(R.id.inbox_action_add)
    FloatingActionButton inboxActionAdd;
    @BindView(R.id.inbox_action_bg)
    TextView inboxActionBg;
    @BindView(R.id.fab_action_voice)
    FloatingActionButton fabActionVoice;
    @BindView(R.id.fab_action_text)
    FloatingActionButton fabActionText;
    private boolean fabMenuOpened = false;
    private SimpleDateFormat format;
    private InboxEventAdapter inboxEventAdapter;
    private List<InboxEvent> events;
    private InboxPresenter inboxPresenter;
    private Speech speech;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        ButterKnife.bind(this, view);
        inboxPresenter = new InboxPresenter(this);
        initView();
        initData();
        return view;
    }

    private void initData() {
        events = new ArrayList<>();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        inboxEventAdapter = new InboxEventAdapter(events, getActivity(), null);
        inboxList.setAdapter(inboxEventAdapter);
        updateInboxData();
    }

    private void updateInboxData() {
        inboxPresenter.loadInboxFromDB();
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        inboxList.setLayoutManager(manager);

        speech = new Speech(getContext(), new DataListener<String>() {
            @Override
            public void onComplete(String result) {
                insertInbox(result);
            }
        });
    }

    private void closeFabMenu(View v) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotation", -135, 20, 0);
        animator.setDuration(200);
        animator.start();
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0);
        alpha.setDuration(200);
        inboxActionBg.startAnimation(alpha);
        inboxActionBg.setVisibility(View.GONE);
        fabMenuOpened = false;
        fabActionText.animate().translationY(0);
        fabActionVoice.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //如果不判断会导致与打开动画处于同一动画调用，只要动画结束就会隐藏，无论是打开还是关闭
                if (!fabMenuOpened) {
                    fabActionText.setVisibility(View.GONE);
                    fabActionVoice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void openFabMenu(View v) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotation", 0, -155, -135);
        animator.setDuration(200);
        animator.start();
        inboxActionBg.setVisibility(View.VISIBLE);
        AlphaAnimation alpha = new AlphaAnimation(0, 1.0f);
        alpha.setDuration(200);
        alpha.setFillAfter(true);
        inboxActionBg.startAnimation(alpha);
        fabMenuOpened = true;

        fabActionText.animate().translationY(-getResources().getDimension(R.dimen.fab_action_75));
        fabActionVoice.animate().translationY(-getResources().getDimension(R.dimen.fab_action_145));
        fabActionText.setVisibility(View.VISIBLE);
        fabActionVoice.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.inbox_action_bg, R.id.fab_action_voice, R.id.fab_action_text,
            R.id.inbox_action_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inbox_action_bg:
                if (fabMenuOpened) {
                    closeFabMenu(inboxActionAdd);
                }
                break;
            case R.id.fab_action_voice:
                attemptSpeech();
                closeFabMenu(inboxActionAdd);
                break;
            case R.id.fab_action_text:
                showTextDialog(null);
                closeFabMenu(inboxActionAdd);
                break;
            case R.id.inbox_action_add:
                if (fabMenuOpened) {
                    closeFabMenu(view);
                } else {
                    openFabMenu(view);
                }
                break;
            default:
                break;
        }
    }

    private void showTextDialog(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getContext());
        editText.setBackgroundColor(Color.WHITE);
        editText.setText(content);
        if (content != null) {
            editText.setSelection(content.length());
        }
        LinearLayout layout = new LinearLayout(getContext());
        int dp20 = (int) DisplayUtils.dp2Px(getContext(), 20);
        layout.setPadding(dp20, dp20, dp20, dp20);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editText);
        builder.setTitle(getString(R.string.inbox_add_text_title))
                .setView(layout)
                .setPositiveButton(getString(R.string.inbox_add_text_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(editText.getText().toString())) {
                            insertInbox(editText.getText().toString());
                        }
                    }
                })
                .setNegativeButton(getString(R.string.inbox_add_text_negative), null)
                .setCancelable(false)
                .show();
    }

    private void attemptSpeech() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            startSpeech();
        }
    }

    private void startSpeech() {
        speech.startSpeech(userPrefs.getBoolean("speechHasSymbol"));
    }

    //将输入内容存到收件箱数据库
    private void insertInbox(String content) {
        InboxEvent event = new InboxEvent(null, content, format.format(new Date()),
                InboxEvent.NO, null);
        Long id = inboxPresenter.insertInbox(event);
        event.setId(id);
        Log.d(TAG, "insertInbox: inboxEvent_ID:" + id);
        events.add(0, event);
        inboxEventAdapter.notifyItemInserted(0);//在显示缓冲区加入元素
        inboxEventAdapter.notifyItemRangeChanged(0, events.size());//确保内部 position 更新
        if (inboxList.getChildAt(0) != null && !inboxList.getChildAt(0).isShown()) {
            inboxList.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    startSpeech();
                } else {
                    toast("权限请求失败，无法进行录音~");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNewMessage(Message msg) {
        switch (msg.what) {
            case InboxEvent.INBOX_ADD:
                showTextDialog(msg.obj.toString());
                break;
            case InboxEvent.INBOX_REVOKE:
            case InboxEvent.INBOX_UPDATE:
                updateInboxData();
                break;
            case InboxEvent.INBOX_REMOVED:
                removeInbox(msg);
                break;
            default:
                break;
        }
    }

    private void removeInbox(Message msg) {
        InboxEvent inboxEvent = (InboxEvent) msg.obj;
        //经过 gson 传输的 inbox 已经不是从 inbox 列表里传输过来的了
        if (inboxEvent != null) {
            for (InboxEvent e : events) {
                if (e.getId().equals(inboxEvent.getId())) {
                    events.remove(e);
                    break;
                }
            }
            inboxPresenter.deleteInbox(inboxEvent);
            inboxEventAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showInbox(List<InboxEvent> inboxEvents) {
        if (inboxEvents != null) {
            events.clear();
            events.addAll(inboxEvents);
            inboxEventAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showDeletedInbox(List<InboxEvent> inboxEvents) {

    }
}
