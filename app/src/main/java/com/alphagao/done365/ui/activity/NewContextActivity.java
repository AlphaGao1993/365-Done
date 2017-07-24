package com.alphagao.done365.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.adapter.ContextAdapter;
import com.alphagao.done365.message.MessageManager;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.ContextEvent;
import com.alphagao.done365.greendao.gen.ContextEventDao;
import com.alphagao.done365.widget.StickyListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alpha on 2017/3/13.
 */


public class NewContextActivity extends BaseActivity {

    private static final String TAG = "NewContextActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.new_context_name)
    EditText newContextName;
    @BindView(R.id.up_level_context)
    TextView upLevelContext;
    @BindView(R.id.selectPosition)
    TextView selectPosition;
    @BindView(R.id.setPositionView)
    LinearLayout setPositionView;
    @BindView(R.id.add_context_to_list)
    Button addContextToList;
    @BindView(R.id.setParentContextLayout)
    LinearLayout setParentContextLayout;
    @BindView(R.id.contextLevelBack)
    TextView contextLevelBack;
    @BindView(R.id.contextPicker)
    StickyListView contextPicker;
    @BindView(R.id.contextPickerLayout)
    LinearLayout contextPickerLayout;

    //位置描述信息
    private String position;
    //经度
    private String longitude;
    //纬度
    private String latitude;
    private ContextEventDao contextEventDao;
    private List<ContextEvent> contextEvents;
    private ContextEvent selectedContext = null;
    private ContextAdapter contextAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_new_context);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.arrow_back_white_24dp);
        }
        initData();
        initViewEvent();
    }

    private void initData() {
        contextEventDao = DaoUtil.daoSession.getContextEventDao();
        contextEvents = contextEventDao
                .queryBuilder()
                .where(ContextEventDao.Properties.UpContextId.eq(0))
                .build()
                .list();
        contextAdapter = new ContextAdapter(contextEvents);
        contextPicker.setAdapter(contextAdapter);
    }

    private void initViewEvent() {
        contextPicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (contextEvents.size() > 0) {
                    selectedContext = contextEvents.get(position);
                    upLevelContext.setText(selectedContext.getName());
                    List<ContextEvent> nextLevelContexts = contextEventDao.queryBuilder()
                            .where(ContextEventDao.Properties.UpContextId
                                    .eq(selectedContext.getId()))
                            .build()
                            .list();
                    if (nextLevelContexts.size() > 0) {
                        contextEvents = nextLevelContexts;
                        contextAdapter.setData(contextEvents);
                        contextLevelBack.setEnabled(true);
                    }
                }
            }
        });
    }

    @OnClick({R.id.setPositionView, R.id.add_context_to_list, R.id.setParentContextLayout,
            R.id.contextLevelBack})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setPositionView:
                break;
            case R.id.add_context_to_list:
                saveContext();
                break;
            case R.id.setParentContextLayout:
                switchContextPickerLayout();
                break;
            case R.id.contextLevelBack:
                handleContextBack();
                break;
            default:
                break;
        }
    }

    private void handleContextBack() {
        List<ContextEvent> events;
        if (contextEvents.get(0).getUpContextId() != null) {
            //查询上层情境 id
            List<ContextEvent> lastContext = contextEventDao
                    .queryBuilder()
                    .where(ContextEventDao.Properties.Id
                            .eq(contextEvents.get(0).getUpContextId()))
                    .build()
                    .list();
            //说明再往上至少还有一层
            if (lastContext.get(0).getUpContextId() != null) {
                events = contextEventDao
                        .queryBuilder()
                        .where(ContextEventDao.Properties.UpContextId
                                .eq(lastContext.get(0).getUpContextId()))
                        .build()
                        .list();
            } else {//其上层就是顶层,加载顶层情境列表
                events = contextEventDao
                        .queryBuilder()
                        .where(ContextEventDao.Properties.UpContextId.eq(0))
                        .build()
                        .list();

                contextLevelBack.setEnabled(false);
            }
            contextEvents = events;
            contextAdapter.setData(contextEvents);
            if (contextEvents.get(0).getUpContextId() == 0) {
                contextLevelBack.setEnabled(false);
            }
        }
    }

    private void switchContextPickerLayout() {
        contextPickerLayout.setVisibility(
                contextPickerLayout.getVisibility() == View.VISIBLE ?
                        View.GONE : View.VISIBLE);
    }

    private void saveContext() {
        String newContext = newContextName.getText().toString();
        if (!TextUtils.isEmpty(newContext)) {
            ContextEvent event = new ContextEvent(null, newContext,
                    selectedContext == null ? 0 : selectedContext.getId(),
                    null, longitude, latitude, position);
            if (event != null) {
                long result = DaoUtil.daoSession.getContextEventDao().insert(event);
                if (result > 0) {
                    toast(getString(R.string.save_context_done, newContext));
                    //设置返回给上层 act 的结果码
                    Intent intent = new Intent();
                    intent.putExtra("new_context_return", "OK");
                    setResult(RESULT_OK, intent);
                    //告诉情境碎片更新情境数据
                    MessageManager manager = MessageManager.getInstance();
                    Message msg = Message.obtain();
                    msg.what = ContextEvent.CONTEXT_UPDATE;
                    manager.publishMessage(msg);
                    finish();
                }
            }
        }
    }

    @Override
    public void onNewMessage(Message msg) {

    }
}
