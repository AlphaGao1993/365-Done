package com.alphagao.done365.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Alpha on 2017/3/12.
 */

public class StickyListView extends ListView {

    private float lastY;

    public StickyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //确保 ScrollView 不会拦截按下事件
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (lastY > ev.getY()) {//向上滑动的时候
                if (!canScrollList(1)) {
                    //如果 ListView 不能在这个方向上滑动就把事件交给上层 ScrollView 处理
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            } else if (ev.getY() > lastY) {//向下滑动的时候
                if (!canScrollList(-1)) {
                    //如果 ListView 不能在这个方向上滑动就把事件交给上层 ScrollView 处理
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            }
        }
        lastY = ev.getY();//记录上次触摸的位置
        return super.dispatchTouchEvent(ev);
    }


    //让 ListView 自适应高度
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
