package com.alphagao.done365.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Alpha on 2017/3/3.
 */

public class SwipeItemLayout extends FrameLayout {

    private View menu;
    private View content;
    private final ViewDragHelper dragHelper;
    private boolean isOpen;
    private int currentState;


    public SwipeItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        dragHelper = ViewDragHelper.create(this, rightCallback);
    }

    //当所有视图被填充完毕的时候调用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menu = getChildAt(0);
        content = getChildAt(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //拦截视图的触摸事件
        dragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //通过 dragHelper 来决定是否截断触摸事件
        return  dragHelper.shouldInterceptTouchEvent(ev);
    }

    public void close() {
        dragHelper.smoothSlideViewTo(content, 0, 0);
        isOpen = false;
        invalidate();
    }


    public void open() {
        dragHelper.smoothSlideViewTo(content, -menu.getWidth(), 0);
        isOpen = true;
        invalidate();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int getState() {
        return currentState;
    }

    private Rect outRect = new Rect();

    public Rect getMenuRect() {
        menu.getHitRect(outRect);
        return outRect;
    }

    //子视图需要计算动画移动的话会被俯视图调用
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (dragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        //把父视图的点击事件传递到内容视图
        content.setOnClickListener(l);
    }

    private ViewDragHelper.Callback rightCallback = new ViewDragHelper.Callback() {
        //触摸到 View 的时候就会调用该方法，return true 表示捕获该 view
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return content == child;
        }

        /**
         * 拖拽的视图被松开的时候调用
         * @param releasedChild 被拖拽的视图
         * @param xvel x 轴触点移动速度 像素/秒
         * @param yvel y 轴触点移动速度 像素/秒
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (isOpen) {
                //右滑速度为正，左滑速度为负
                //当右滑速度大于菜单宽度，或内容左边界移出宽度小于菜单宽度的一半，合上菜单
                if (xvel > menu.getWidth() || -content.getLeft() < menu.getWidth() / 2) {
                    close();
                } else {
                    open();
                }
            } else {
                //当左滑速度大于菜单宽度，或内容左边界大于菜单宽度的一半，展开菜单
                if (-xvel > menu.getWidth() || -content.getLeft() > menu.getWidth() / 2) {
                    open();
                } else {
                    close();
                }
            }
        }

        /**
         * 对水平位置的固定
         * @param child
         * @param left 水平方向尝试移动的距离,向左移动为正
         * @param dx 水平方向建议的移动距离
         * @return 新的固定后的水平位置
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //如果是右滑则回到原位；如果是左滑并且滑动的距离超过了菜单的宽度则展开菜单
            return left > 0 ? 0 : left < -menu.getWidth() ? -menu.getWidth() : left;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            //不会水平移动的 view 应该返回 0
            return 1;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            //不会竖直移动的 view 应该返回 0
            return 1;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            currentState = state;
        }
    };


}
