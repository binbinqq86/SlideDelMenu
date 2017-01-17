package com.binbin.slidedelmenu.singlelistview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Scroller;

import com.binbin.slidedelmenu.R;
import com.binbin.slidedelmenu.Utils;

/**
 * Created by -- on 2016/11/8.
 */
@Deprecated
public class SlideMenuListView2 extends ListView {
    private static final String TAG="tianbin";
    /**最小滑动距离，超过了，才认为开始滑动  */
    private int mTouchSlop = 0 ;
    /**上次触摸的X坐标*/
    private float mLastX = -1;
    /**上次触摸的Y坐标*/
    private float mLastY = -1;
    private int contentWidth;
    private int menuWidth;
    private static final int MIN_FLING_VELOCITY = 600; // dips per second
    private ViewGroup singleChild;
    private float ratio;
    private boolean isMenuVisible;
    private boolean isSliding;
    private Scroller mScroller;

    public SlideMenuListView2(Context context) {
        this(context,null);
    }

    public SlideMenuListView2(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlideMenuListView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlideMenuListView2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        final float density = getContext().getResources().getDisplayMetrics().density;
        contentWidth=getResources().getDisplayMetrics().widthPixels;
        menuWidth=getResources().getDimensionPixelOffset(R.dimen.menu_width);
        ratio=menuWidth/3;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mScroller=new Scroller(getContext());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX=ev.getX();
                mLastY=ev.getY();
                for (int i = 0; i <getChildCount() ; i++) {
                    View child=getChildAt(i);
                    if(Utils.inRangeOfView(child,ev)){
                        singleChild= (ViewGroup) child;
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(mLastX-ev.getX())>mTouchSlop&&Math.abs(mLastX-ev.getX())>Math.abs(mLastY-ev.getY())){
                    isSliding=true;
                }else{
                    isSliding=false;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                mLastX=ev.getX();
//                mLastY=ev.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.e(TAG,"1111111111111111111111");
//                if(Math.abs(mLastX-ev.getX())>mTouchSlop&&
//                        Math.abs(mLastX-ev.getX())>Math.abs(mLastY-ev.getY())){
//                    //满足水平方向滑动条件，就不拦截触摸事件，让其继续传递给子view（item），同时也不会走自己的onTouchEvent
////                    requestDisallowInterceptTouchEvent(true);//或者返回false也可以
//                    isSliding=true;
//                    Log.e(TAG,"22222222222222222222");
//                    return false;
//                }else{
//                    Log.e(TAG,"333333333333333333333333");
//                    isSliding=false;
//                }
//                break;
//        }
//        return super.onInterceptTouchEvent(ev);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isSliding){
            switch (ev.getAction()){
                case MotionEvent.ACTION_MOVE:
                    float deltaX=mLastX-ev.getX();
                    singleChild.scrollBy((int)deltaX,0);
                    mLastX=ev.getX();
                    mLastY=ev.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }else{
        }
    }
}
