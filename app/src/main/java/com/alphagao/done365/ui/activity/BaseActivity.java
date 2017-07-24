package com.alphagao.done365.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.alphagao.done365.message.MessageManager;
import com.alphagao.done365.message.Observer;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.utils.MyToast;
import com.alphagao.done365.utils.Prefs;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Alpha on 2017/3/11.
 */


public abstract class BaseActivity extends AppCompatActivity implements Observer {

    protected MessageManager messageManager;
    protected Prefs prefs;
    protected Prefs userPrefs;
    protected static List<Activity> startedActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPrefs();

        saveSelf();

        initDao();

        if (!(this instanceof MainActivity) && !(this instanceof SettingsActivity)) {
            SlidrConfig config = new SlidrConfig.Builder()
                    .position(SlidrPosition.LEFT)//只能从左侧移除
                    .sensitivity(1f)
                    .scrimStartAlpha(0.8f)
                    .scrimEndAlpha(0f)
                    .velocityThreshold(2400)
                    .distanceThreshold(0.25f)
                    .edge(true)//是否只能触摸边缘关闭
                    .edgeSize(0.18f)//左侧边缘关闭比例
                    .build();
            Slidr.attach(this, config);
        }



        messageManager = MessageManager.getInstance();
        messageManager.registerObserver(this);
    }

    private void initDao() {
        if (!(this instanceof LoginActivity || this instanceof MainActivity)) {
            DaoUtil.generateDatabase(this, String.valueOf(userPrefs.getLong("user_id")));
        }
    }

    private void saveSelf() {
        if (startedActivity == null) {
            startedActivity = new ArrayList<>();
        }
        startedActivity.add(this);
    }

    private void initPrefs() {
        prefs = Prefs.getInstance(getApplicationContext());

        if (prefs.getLong("current_login_user") > 0 && !(this instanceof LoginActivity)) {
            userPrefs = Prefs.getUserInstance(getApplicationContext(),
                    prefs.getLong("current_login_user"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageManager.removeObserver(this);
        startedActivity.remove(this);
    }

    @Override
    public abstract void onNewMessage(Message msg);

    protected void toast(String message) {
        toast(message, Toast.LENGTH_SHORT);
    }

    protected void toast(String message, int duration) {
        MyToast.toast(message, duration);
    }
}
