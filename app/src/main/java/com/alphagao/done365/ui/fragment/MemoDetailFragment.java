package com.alphagao.done365.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.MemoEvent;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Alpha on 2017/4/25.
 */

public class MemoDetailFragment extends BaseFragment {


    @BindView(R.id.memo_detail_view)
    EditText memoDetailView;
    Unbinder unbinder;
    private MenuItem memoModify;
    private MenuItem memoModifyDone;
    private MemoEvent mEvent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memo_detail, null, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        initData();
        return view;
    }

    private void initData() {
        Bundle bundle = getArguments();
        String memoStr = bundle.getString("memo");
        mEvent = new Gson().fromJson(memoStr, MemoEvent.class);
        memoDetailView.setText(mEvent.getContent());
        memoDetailView.setInputType(InputType.TYPE_NULL);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_memo_detail, menu);
        memoModify = menu.findItem(R.id.memo_modify);
        memoModifyDone = menu.findItem(R.id.memo_modify_done);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.memo_modify:
                startMemoModify();
                break;
            case R.id.memo_modify_done:
                stopMemoModify();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void stopMemoModify() {
        memoModifyDone.setVisible(false);
        memoModify.setVisible(true);

        memoDetailView.setInputType(InputType.TYPE_NULL);
        mEvent.setContent(memoDetailView.getText().toString());
        DaoUtil.daoSession.getMemoEventDao().update(mEvent);

        Message msg = Message.obtain();
        msg.what = MemoEvent.MEMO_UPDATE;
        messageManager.publishMessage(msg);
    }

    private void startMemoModify() {
        memoModify.setVisible(false);
        memoModifyDone.setVisible(true);

        memoDetailView.setInputType(InputType.TYPE_CLASS_TEXT);
        memoDetailView.setSelection(memoDetailView.length());
    }

    @Override
    public void onNewMessage(Message msg) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
