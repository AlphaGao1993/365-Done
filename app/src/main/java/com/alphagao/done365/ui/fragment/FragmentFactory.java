package com.alphagao.done365.ui.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alpha on 2017/3/20.
 * 借助于工厂模式来构建 fragment ，同时设置共同的参数
 */

public class FragmentFactory {

    private static Map<Class, Fragment> mFragments = new HashMap<>();

    public static Fragment create(Context context, Class cls) {
        Fragment fragment = Fragment.instantiate(context, cls.getName());

        /*Bundle bundle = new Bundle();
        bundle.putParcelable("event_manager", MessageManager.getInstance());
        fragment.setArguments(bundle);*/
        if (fragment != null) {
            mFragments.put(cls, fragment);
        }
        return fragment;
    }
}
