package com.alphagao.done365.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Alpha on 2017/4/12.
 */

public class MyViewPager extends ViewPager {

    private boolean disenableScroll;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDisenableScroll(boolean disenableScroll) {
        this.disenableScroll = disenableScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (disenableScroll) {
            return false;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (disenableScroll) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }
}
