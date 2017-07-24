package com.alphagao.done365.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alphagao.done365.service.AlarmService;

/**
 * Created by Alpha on 2017/4/13.
 * 每分钟接受广播来包正服务长期运行
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent.getDataString());
        Intent i = AlarmService.newIntent(context);
        context.startService(i);
        AlarmService.setServiceAlarm(context, true);
    }
}
