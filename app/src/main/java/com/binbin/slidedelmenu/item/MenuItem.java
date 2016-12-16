package com.binbin.slidedelmenu.item;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by -- on 2016/11/3.
 * 带侧滑菜单的自定义item
 */

public class MenuItem extends ViewGroup {
    private int contentWidth;
    private Scroller mScroller;
    private int maxWidth,maxHeight;//viewGroup的宽高
    private static final int MIN_FLING_VELOCITY = 600; // dips per second
    /**最小滑动距离，超过了，才认为开始滑动  */
    private int mTouchSlop = 0 ;
    /**上次触摸的X坐标*/
    private float mLastX = -1;
    /**上次触摸的Y坐标*/
    private float mLastY = -1;
    private int ratio;
    //防止多只手指一起滑动的flag 在每次down里判断， touch事件结束清空
    private static boolean isTouching;
    private int mRightMenuWidths;//右侧菜单总宽度

    public MenuItem(Context context) {
        this(context,null);
    }

    public MenuItem(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MenuItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        contentWidth=getContext().getResources().getDisplayMetrics().widthPixels;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mScroller=new Scroller(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setClickable(true);//令自己可点击，从而获取触摸事件（很重要，必须写在onMesaure中）
        mRightMenuWidths=0;//由于ViewHolder的复用机制，每次这里要手动恢复初始值
        /**
         * 根据childView计算的出的宽和高，计算容器的宽和高，主要用于容器是warp_content时
         */
        for (int i = 0,count = getChildCount(); i < count; i++) {
            View childView = getChildAt(i);
            //令每一个子View可点击，从而获取触摸事件
            childView.setClickable(true);
            if(childView.getVisibility()!=View.GONE){
                //获取每个子view的自己高度宽度，取最大的就是viewGroup的大小
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                maxWidth = Math.max(maxWidth,childView.getMeasuredWidth());
                maxHeight = Math.max(maxHeight,childView.getMeasuredHeight());
                if(i>0){//第一个是content，后面的都是菜单
                    if(childView.getLayoutParams().width== LayoutParams.MATCH_PARENT){
                        //菜单的宽不能MATCH_PARENT
                        throw new IllegalArgumentException("======menu'width can't be MATCH_PARENT=====");
                    }
                    mRightMenuWidths+=childView.getMeasuredWidth();
                }else{
                    if(childView.getLayoutParams().width!= LayoutParams.MATCH_PARENT){
                        //content的宽必须MATCH_PARENT
                        throw new IllegalArgumentException("======content'width must be MATCH_PARENT=====");
                    }
                }
            }
        }
        //为ViewGroup设置宽高
        setMeasuredDimension(maxWidth,maxHeight);
        ratio=mRightMenuWidths/3;//可能每个item的菜单不同，所以ratio要每次计算

        /**
         * 根据最大宽高重新设置子view，保证高度充满
         */
        //首先判断params.width的值是多少，有三种情况。
        //如果是大于零的话，及传递的就是一个具体的值，那么，构造MeasupreSpec的时候可以直接用EXACTLY。
        //如果为-1的话，就是MatchParent的情况，那么，获得父View的宽度，再用EXACTLY来构造MeasureSpec。
        //如果为-2的话，就是wrapContent的情况，那么，构造MeasureSpec的话直接用一个负数就可以了。
        for (int i = 0,count = getChildCount(); i < count; i++) {
            View childView = getChildAt(i);
            if(childView.getVisibility()!=View.GONE){
                //宽度采用测量好的
                int widthSpec = MeasureSpec.makeMeasureSpec(childView.getMeasuredWidth(), MeasureSpec.EXACTLY);
                //高度采用最大的
                int heightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
                childView.measure(widthSpec, heightSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Log.e("tianbin",content.getMeasuredWidth()+"#"+content.getMeasuredHeight()+"$$$"+menu.getMeasuredWidth()+"#"+menu.getMeasuredHeight());
        int left=0;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                if (i == 0) {//第一个子View是内容 宽度设置为全屏
                    childView.layout(0, 0, maxWidth, maxHeight);
                    left += maxWidth;
                } else {
                    childView.layout(left, 0, left + childView.getMeasuredWidth(), getPaddingTop() + maxHeight);
                    left += childView.getMeasuredWidth();
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
//                Log.e("tianbin","======MenuItem dispatchTouchEvent======ACTION_DOWN===");
//                if (isTouching) {//如果有别的指头摸过了，那么就return false。这样后续的move..等事件也不会再来找这个View了。
//                    return false;
//                } else {
//                    isTouching = true;//第一个摸的指头，赶紧改变标志，宣誓主权。
//                }
                mLastX=ev.getRawX();
                mLastY=ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.e("tianbin","======MenuItem dispatchTouchEvent======ACTION_MOVE===11111111111111");
                float deltaX= ev.getRawX()-mLastX;
                mLastY=ev.getRawY();
                mLastX=ev.getRawX();
                //为了在水平滑动中禁止父类ListView等再竖直滑动
                if (Math.abs(deltaX) > 10 || Math.abs(getScrollX()) > 10) {//使屏蔽父布局滑动更加灵敏，
                    getParent().requestDisallowInterceptTouchEvent(true);
//                    Log.e("tianbin","======MenuItem dispatchTouchEvent======ACTION_MOVE===222222222222222");
                }
                scrollBy(-(int)deltaX,0);
                //越界修正
                if (getScrollX() < 0) {
                    scrollTo(0, 0);
                }
                if (getScrollX() > mRightMenuWidths) {
                    scrollTo(mRightMenuWidths, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
//                Log.e("tianbin","======MenuItem dispatchTouchEvent======ACTION_UP===");
                isTouching = false;//没有手指在摸我了
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                //屏蔽滑动时的事件(长按事件和侧滑的冲突)
//                Log.e("tianbin","======MenuItem onInterceptTouchEvent======ACTION_MOVE===111111111111111");
                if (Math.abs(ev.getRawX() - mLastX) > mTouchSlop) {
//                    Log.e("tianbin","======MenuItem onInterceptTouchEvent======ACTION_MOVE===22222222222222222");
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
