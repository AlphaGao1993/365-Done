package com.alphagao.done365.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.alphagao.done365.R;
import com.alphagao.done365.ui.activity.MainActivity;
import com.alphagao.done365.ui.activity.SingleFragmentActivity;
import com.alphagao.done365.message.MessageManager;
import com.alphagao.done365.message.Observer;
import com.alphagao.done365.utils.MyToast;
import com.alphagao.done365.utils.Prefs;

/**
 * Created by Alpha on 2017/3/15.
 */


public abstract class BaseFragment extends Fragment implements Observer {
    private static final String TAG = "BaseFragment";
    protected MessageManager messageManager;
    protected Prefs prefs;
    protected Prefs userPrefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        messageManager = MessageManager.getInstance();
        messageManager.registerObserver(this);
        prefs = Prefs.getInstance(getActivity().getApplicationContext());
        userPrefs = Prefs.getUserInstance(getActivity(), prefs.getLong("current_login_user"));
        super.onCreate(savedInstanceState);
    }

    @Override
    public abstract void onNewMessage(Message msg);

    @Override
    public void onDestroy() {
        messageManager.removeObserver(this);
        super.onDestroy();
    }

    protected Toolbar initToolbar(String title) {

        if (getActivity() instanceof SingleFragmentActivity) {
            SingleFragmentActivity activity = (SingleFragmentActivity) getActivity();
            Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
            toolbar.setTitle(title);
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            return toolbar;
        }
        return null;
    }

    protected Toolbar initToolbar(int resId) {
        String title = getResources().getString(resId);
        return initToolbar(title);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {//to publish MainActivity to change menu to displayed fragment.
            Message msg = Message.obtain();
            msg.what = MainActivity.CHANGE_MENU_GROUP;
            msg.obj = getClass().getName();
            messageManager.publishMessage(msg);
        }
        super.onHiddenChanged(hidden);
    }

    protected void toast(String message) {
        toast(message, Toast.LENGTH_SHORT);
    }

    protected void toast(String message, int duration) {
        MyToast.toast(message,duration);
    }
}
