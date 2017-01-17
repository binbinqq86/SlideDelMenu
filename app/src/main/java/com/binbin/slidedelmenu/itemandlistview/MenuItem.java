package com.binbin.slidedelmenu.itemandlistview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by -- on 2016/11/3.
 * 带侧滑菜单的自定义item
 */
@Deprecated
public class MenuItem extends ViewGroup {
    private GestureDetector mGestureDetector;
    private ViewDragHelper mDragger;
    private ViewDragHelper.Callback callback;
    private int contentWidth;
    private int maxWidth,maxHeight;//viewGroup的宽高
    private static final int MIN_FLING_VELOCITY = 600; // dips per second
    private View item,menu;
    /**最小滑动距离，超过了，才认为开始滑动  */
    private int mTouchSlop = 0 ;
    /**上次触摸的X坐标*/
    private float mLastX = -1;
    /**上次触摸的Y坐标*/
    private float mLastY = -1;
    private int ratio;
    private boolean isMenuVisible=false;


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
        final float density = getContext().getResources().getDisplayMetrics().density;
        contentWidth=getContext().getResources().getDisplayMetrics().widthPixels;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        callback=new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return item==child;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                //保持在0到-menu.getMeasuredWidth()之间滑动
                final int newLeft = Math.min(0,Math.max(left, -menu.getMeasuredWidth()));
                return newLeft;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return 0;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return  -menu.getMeasuredWidth();
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return 0;
            }


            //手指释放的时候回调,后面两个参数代表速度，向右，下滑动为正值
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int left=item.getLeft();
//                Log.e("tianbin","====onViewReleased======"+xvel+"@"+mDragger.getMinVelocity()+"##");
//                //此处也可以通过速度正负来判断滑动方向
                if(xvel>=0&&(Math.abs(left)<=ratio||xvel>=mDragger.getMinVelocity())){
                    //说明是向右滑动，并且距离或者速度符合标准，自动隐藏
                    mDragger.smoothSlideViewTo(item,0,0);
                    isMenuVisible=false;
                }else if(xvel<=0&&(Math.abs(left)>=ratio||Math.abs(xvel)>=mDragger.getMinVelocity())){
                    mDragger.smoothSlideViewTo(item,-menu.getMeasuredWidth(),0);
                    isMenuVisible=true;
                }else{
                    //回弹
                    mDragger.settleCapturedViewAt(0, 0);
                }
                invalidate();
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
//                Log.e("tianbin",left+"=========onViewPositionChanged============");
                menu.setTranslationX(left);
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
//                Log.e("tianbin",state+"=========onViewDragStateChanged============");
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
//                Log.e("tianbin",capturedChild+"=========onViewCaptured============");
            }
        };
        mDragger = ViewDragHelper.create(this, 0.5f,callback );
        mDragger.setMinVelocity(MIN_FLING_VELOCITY * density);
        mGestureDetector=new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
//                Log.e("tianbin","=====onLongPress======");
                if(onClickListener!=null){
                    onClickListener.onLongClick(null);
                }
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
//                Log.e("tianbin","=====onSingleTapUp======");
                if(onClickListener!=null){
                    onClickListener.onClick(item,0);
                }
                return true;
            }

//            @Override
//            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                Log.e("tianbin","=====onScroll======");
//                mDragger.processTouchEvent(e2);
//                return true;
//            }
//
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                Log.e("tianbin","=====onFling======");
//                mDragger.processTouchEvent(e2);
//                return true;
//            }
        });
        setClickable(true);
//        setLongClickable(true);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDragger.processTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    /**
     * 两种方案
     * 1.通过反射，使动画时间为0
     * 2.在动画结束后去更新列表
     */
    public void delItem(){
        setOnDelAnimationEnd(new OnDelAnimationEnd() {
            @Override
            public void onDelAnimationEnd() {
                ListAdapter adapter=((ListView)MenuItem.this.getParent()).getAdapter();
                if(adapter instanceof BaseAdapter){
                    ((BaseAdapter)adapter).notifyDataSetChanged();
                }
            }
        });
        hideMenuSmooth();
    }

//    public void hideMenuNoAnim(){
//        try {
//            Class clazz=ViewDragHelper.class;
//            Field mCapturedView=clazz.getDeclaredField("mCapturedView");
//            Field mActivePointerId=clazz.getDeclaredField("mActivePointerId");
//            Object object = mDragger;
//            mCapturedView.setAccessible(true);
//            mCapturedView.set(object,item);
//            mCapturedView.setAccessible(false);
//            mActivePointerId.setAccessible(true);
//            mActivePointerId.set(object,-1);
//            mActivePointerId.setAccessible(false);
//            Method me=clazz.getDeclaredMethod("forceSettleCapturedViewAt", int.class,int.class,int.class,int.class);
//            me.setAccessible(true);
//            me.invoke(object, 0,0,Integer.MAX_VALUE,0);
//            me.setAccessible(false);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        invalidate();
//    }

    public void hideMenuSmooth(){
        mDragger.smoothSlideViewTo(item,0,0);
        invalidate();
        isMenuVisible=false;
    }

    public boolean getIsMenuVisible(){
        return isMenuVisible;
    }

    /**
     * 菜单布局的宽度要设置成wrap_content自适应
     * @param item
     * @param menu
     */
    public void setItemAndMenu(View item,View menu){
        this.item=item;
        this.menu=menu;
        addView(item,0,new LayoutParams(-1,-1));
        addView(menu,1,new LayoutParams(-2,-1));
        //菜单子view点击事件
        for (int i = 0; i < ((ViewGroup)menu).getChildCount(); i++) {
            View v=((ViewGroup)menu).getChildAt(i);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onClickListener!=null){
                        onClickListener.onClick(v,1);
                    }
                }
            });
        }
        //item子view点击事件
        int childCount=((ViewGroup)item).getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child=((ViewGroup)item).getChildAt(i);
            if(child.isClickable()){
                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onClick(v,1);
                    }
                });
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //当我们需要重写onMeasure时，记得要调用setMeasuredDimension来设置自身的mMeasuredWidth和mMeasuredHeight，否则，就会抛出异常
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        //item的宽高必须充满listView
//        if(widthMode!=MeasureSpec.EXACTLY){
//            widthMode=MeasureSpec.EXACTLY;
//        }
//        if(heightMode!=MeasureSpec.EXACTLY){
//            heightMode=MeasureSpec.EXACTLY;
//        }
//        int parentWidthMeasureSpec=MeasureSpec.makeMeasureSpec(measuredWidth,widthMode);

        /**
         * 根据childView计算的出的宽和高，计算容器的宽和高，主要用于容器是warp_content时
         */
        for (int i = 0,count = getChildCount(); i < count; i++) {
            View childView = getChildAt(i);
            //把下面的循环弄过来，重新研究。。。
            //获取每个子view的自己高度宽度，取最大的就是viewGroup的大小
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            maxWidth = Math.max(maxWidth,childView.getMeasuredWidth());
            maxHeight = Math.max(maxHeight,childView.getMeasuredHeight());
        }
        //为ViewGroup设置宽高
        setMeasuredDimension(maxWidth,maxHeight);

        // 计算出所有的childView的宽和高---可用
//        measureChildren(widthMeasureSpec, heightMeasureSpec);

        /**
         * 根据最大宽高重新设置子view，保证高度充满
         */
        //首先判断params.width的值是多少，有三种情况。
        //如果是大于零的话，及传递的就是一个具体的值，那么，构造MeasupreSpec的时候可以直接用EXACTLY。
        //如果为-1的话，就是MatchParent的情况，那么，获得父View的宽度，再用EXACTLY来构造MeasureSpec。
        //如果为-2的话，就是wrapContent的情况，那么，构造MeasureSpec的话直接用一个负数就可以了。
        for (int i = 0,count = getChildCount(); i < count; i++) {
            View childView = getChildAt(i);
            int widthSpec = 0;
            int heightSpec = 0;
            LayoutParams params = childView.getLayoutParams();
            if(i==1){
                //菜单的宽度是自适应的，此处采用上面的测量好的
                widthSpec=MeasureSpec.makeMeasureSpec(childView.getMeasuredWidth(),MeasureSpec.EXACTLY);
            }else{
                if(params.width > 0){
                    widthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
                }else if (params.width == -1) {
                    widthSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY);
                } else if (params.width == -2) {
                    widthSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST);
                }
            }

            if(params.height > 0){
                heightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
            }else if (params.height == -1) {
                heightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
            } else if (params.height == -2) {
                heightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
            }
            childView.measure(widthSpec, heightSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Log.e("tianbin",item.getMeasuredWidth()+"#"+item.getMeasuredHeight()+"$$$"+menu.getMeasuredWidth()+"#"+menu.getMeasuredHeight());
        if(changed){
            item.layout(0, 0, r, maxHeight);
            menu.layout(r, 0, r+menu.getMeasuredWidth(), maxHeight);
        }
//        Log.e("tianbin",menu.getMeasuredWidth()+"@"+r+"@"+b+"=================="+changed);
        if(ratio==0){
            ratio=menu.getMeasuredWidth()/3;
        }
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                Log.e("tianbin","======MenuItem dispatchTouchEvent====ACTION_DOWN=====");
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.e("tianbin","======MenuItem dispatchTouchEvent======ACTION_MOVE===");
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.e("tianbin","======MenuItem dispatchTouchEvent======ACTION_UP===");
//                break;
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    /**
     * 处理侧滑菜单以及listview的item点击事件
     * 由于事件是从上往下，再从下往上逐级传递，所以如果这里返回true，listview的onTouchEvent再也不会执行(item点击事件在这里面)
     * 所以item点击无响应，所以必须在这里区分是菜单滑动还是item点击
     */
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
////        switch (ev.getAction()){
////            case MotionEvent.ACTION_DOWN:
////                Log.e("tianbin","======MenuItem onTouchEvent====ACTION_DOWN=====");
////                break;
////            case MotionEvent.ACTION_MOVE:
////                Log.e("tianbin","======MenuItem onTouchEvent======ACTION_MOVE===");
////                break;
////            case MotionEvent.ACTION_UP:
////                Log.e("tianbin","======MenuItem onTouchEvent======ACTION_UP===");
////                break;
////        }
//        mDragger.processTouchEvent(ev);
//        return true;
//    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                Log.e("tianbin","======MenuItem onInterceptTouchEvent=====ACTION_DOWN====");
//                mLastX=ev.getX();
//                mLastY=ev.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.e("tianbin","======MenuItem onInterceptTouchEvent====ACTION_MOVE=====");
//                if((Math.abs(mLastY-ev.getY())<mTouchSlop)&&
//                        (Math.abs(mLastX-ev.getX())>mTouchSlop)){
//                    //自己来处理左右滑动，上下滑动在listView中重写处理，否则交给子view处理
////                    return mDragger.shouldInterceptTouchEvent(ev);
//                    Log.e("tianbin","======MenuItem onInterceptTouchEvent====ACTION_MOVE111111111111111111111111111=====");
//                    return true;
//                }
//                break;
//        }
//        return super.onInterceptTouchEvent(ev);
//    }

    @Override
    public void computeScroll() {
        if(mDragger.continueSettling(true)) {
            invalidate();
        }else{
            if(onDelAnimationEnd!=null){
                onDelAnimationEnd.onDelAnimationEnd();
                onDelAnimationEnd=null;
            }
        }
    }
    private OnDelAnimationEnd onDelAnimationEnd;
    private void setOnDelAnimationEnd(OnDelAnimationEnd onDelAnimationEnd){
        this.onDelAnimationEnd=onDelAnimationEnd;
    }

    private interface OnDelAnimationEnd{
        void onDelAnimationEnd();
    }

    private OnClickListener onClickListener;
    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }
    public interface OnClickListener{
        /**
         *
         * @param view
         * @param type 点击类型：0-item,1-菜单
         */
        void onClick(View view,int type);
        void onLongClick(View view);
    }
}
