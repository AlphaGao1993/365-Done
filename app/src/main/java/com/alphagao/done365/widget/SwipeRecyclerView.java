package com.alphagao.done365.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by Alpha on 2017/3/3.
 */

public class SwipeRecyclerView extends RecyclerView {
    private static final String TAG = "SwipeRecyclerView";
    private float startX;
    private float startY;
    //触摸偏移临界值
    private int touchSlop;
    private boolean isChildHandle;
    private View touchView;
    private float distanceX;
    private float distanceY;

    public SwipeRecyclerView(Context context) {
        super(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public SwipeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public SwipeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //如果是按下
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            isChildHandle = false;
            startX = ev.getX();
            startY = ev.getY();
            distanceX = 0;
            distanceY = 0;
            int position = pointToPosition((int) startX, (int) startY);
            touchView = getChildAt(position);
            if (hasChildOpen()) {
                if (touchView != null && touchView instanceof SwipeItemLayout
                        && ((SwipeItemLayout) touchView).isOpen()) {
                    isChildHandle = true;//将触摸事件交给 child 处理
                } else {
                    closeAllSwipeItem();
                    return false;
                }
            }
        }

        //禁用多点触控
        if (ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int action = e.getActionMasked();
        boolean consume = false;
        //如果竖向滑动则拦截，否则不拦截
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //如果拦截则子 view 不会再接受到事件
                consume = false;
                break;
            case MotionEvent.ACTION_MOVE:
                //获取当前手指位置
                float endX = e.getX();
                float endY = e.getY();
                distanceX = Math.abs(endX - startX);
                distanceY = Math.abs(endY - startY);

                //如果 child 已经持有事件，那么不拦截它的事件，直接 return false
                if (isChildHandle) {
                    return false;
                }
                //如果 x 轴位移大于 y 轴位移，则将事件交给 child
                if (distanceX > touchSlop && distanceX > distanceY) {
                    isChildHandle = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (touchView != null && touchView instanceof SwipeItemLayout) {
                    SwipeItemLayout item = (SwipeItemLayout) this.touchView;
                    //state!=1 没有滑动过，关闭打开的菜单
                    if (item.isOpen() && item.getState() != 1) {
                        if (distanceX < touchSlop && distanceY < touchSlop) {
                            item.close();
                        }
                        Rect rect = item.getMenuRect();
                        //拦截 item 点击事件，但是 菜单能收到事件
                        if (!(startX > rect.left && startX < rect.right
                                && startY > touchView.getTop()
                                && startY < touchView.getBottom())) {
                            return true;
                        }
                    }
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    private void closeAllSwipeItem() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null && child instanceof SwipeItemLayout) {
                if (((SwipeItemLayout) child).isOpen()) {
                    ((SwipeItemLayout) child).close();
                }
            }
        }
    }

    /**
     * 是否有子视图菜单展开
     *
     * @return 是否有子视图菜单展开
     */
    private boolean hasChildOpen() {
        int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child != null && child instanceof SwipeItemLayout) {
                if (((SwipeItemLayout) child).isOpen()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Rect touchFrame;

    private int pointToPosition(int startX, int startY) {
        Rect frame = touchFrame;
        if (frame == null) {
            touchFrame = new Rect();
            frame = touchFrame;
        }
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(startX, startY)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
