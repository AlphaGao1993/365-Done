package com.alphagao.done365.app;

import android.app.Application;
import android.content.Context;

import com.alphagao.done365.R;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by Alpha on 2017/3/5.
 */

public class App extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        //科大讯飞语音听写初始化
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + getString(R.string.iflytek_app_id));
    }

    public static Context getAppContext() {
        return appContext;
    }
}
