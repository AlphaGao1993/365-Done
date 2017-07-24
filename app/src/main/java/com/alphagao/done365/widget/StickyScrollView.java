package com.alphagao.done365.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Alpha on 2017/3/12.
 */

public class StickyScrollView extends ScrollView {
    public StickyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //将事件先传递给自身的触摸响应，否则 ScrollView 自己的滑动会受到影响
            onTouchEvent(ev);
            //这里直接返回 false 不会影响到自身，因为如果子 View 没有处理事件，那么最后事件还是会返还到这里的
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
