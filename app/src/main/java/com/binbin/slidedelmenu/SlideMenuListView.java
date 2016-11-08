package com.binbin.slidedelmenu;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;

/**
 * Created by -- on 2016/11/3.
 */

public class SlideMenuListView extends ListView {
    /**最小滑动距离，超过了，才认为开始滑动  */
    private int mTouchSlop = 0 ;
    /**上次触摸的X坐标*/
    private float mLastX = -1;
    /**上次触摸的Y坐标*/
    private float mLastY = -1;
    public SlideMenuListView(Context context) {
        this(context,null);
    }

    public SlideMenuListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlideMenuListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlideMenuListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        mTouchSlop= ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX=ev.getX();
                mLastY=ev.getY();
                for (int i = 0; i <=getLastVisiblePosition()-getFirstVisiblePosition(); i++) {
                    MenuItem mi=(MenuItem)getChildAt(i);
                    if(mi.getIsMenuVisible()){
                        //当有菜单出现时，按下非菜单区域就隐藏菜单，并消费此次触摸事件，不再向下传递
                        if(!inRangeOfView(mi.getChildAt(1),ev)){
                            mi.hideMenuSmooth();
                            return true;
                        }
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(mLastX-ev.getX())>mTouchSlop&&
                        Math.abs(mLastX-ev.getX())>Math.abs(mLastY-ev.getY())){
                    //满足水平方向滑动条件，就不拦截触摸事件，让其继续传递给子view（item），同时也不会走自己的onTouchEvent
//                    requestDisallowInterceptTouchEvent(true);//或者返回false也可以
                    return false;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);//调用listView中的拦截方法，会走自己的onTouchEvent
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        Log.e("tianbin","=========onTouchEvent=========");
//        return super.onTouchEvent(ev);
//    }

    /**
     * 判断是否点击在view的内部
     * @param view
     * @param ev
     * @return
     *            true 点击在view的内部
     *            false 点击在view的外部
     */
    private boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y=location[1];
        if (ev.getRawX() < x || ev.getRawX() > (x + view.getWidth())||ev.getRawY()<y||ev.getRawY()>(y+view.getHeight())) {
            return false;
        }
        return true;
    }
}
