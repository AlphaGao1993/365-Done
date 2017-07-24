package com.alphagao.done365.message;

import android.os.Message;

/**
 * Created by Alpha on 2017/3/15.
 * 观察者接口，通知事件的接受者
 */

public interface Observer {

    void onNewMessage(Message msg);

}
