package com.binbin.slidedelmenu.item;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
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
    /**第一次触摸的X坐标*/
    private float mFirstX = -1;
    private int ratio;
    //防止多只手指一起滑动的flag 在每次down里判断， touch事件结束清空
    private boolean canDrag;
    private int mRightMenuWidths;//右侧菜单总宽度
    private VelocityTracker mVelocityTracker;
    private float mMaxVelocity;
    private int mPointerId;//多点触摸只算第一根手指的速度
    private boolean isQQ=true;//是否是qq效果
    private boolean qqInterceptFlag;//qq效果判断标志
    private static MenuItem mViewCache;//存储的是当前正在展开的View
    private boolean isUserSwiped;// 判断手指起始落点，如果距离属于滑动了，就屏蔽一切点击事件
    //仿QQ，侧滑菜单展开时，点击除侧滑菜单之外的区域，关闭侧滑菜单。
    //增加一个布尔值变量，dispatch函数里，每次down时，为true，move时判断，如果是滑动动作，设为false。
    //在Intercept函数的up时，判断这个变量，如果仍为true 说明是点击事件，则关闭菜单。
    private boolean isUnMoved = true;

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
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
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
        acquireVelocityTracker(ev);
        final VelocityTracker verTracker = mVelocityTracker;
        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
//                Log.e("tianbin","======MenuItem dispatchTouchEvent======ACTION_DOWN==="+isTouching);
                if(ev.getPointerId(ev.getActionIndex())==0){
                    canDrag=true;
                    mLastX=ev.getX();
                    mFirstX=ev.getX();
                    //求第一个触点的id， 此时可能有多个触点，但至少一个，计算滑动速率用
                    mPointerId = ev.getPointerId(0);
                    isUserSwiped = false;
                    qqInterceptFlag=false;
                    isUnMoved=true;
                    //如果down，view和cacheview不一样，则立马让它还原。且把它置为null
                    if (mViewCache != null) {
                        if (mViewCache != this) {
                            mViewCache.smoothClose();
                            qqInterceptFlag = isQQ;//当前有侧滑菜单的View，且不是自己的，就该拦截事件咯。
                        }
                        //只要有一个侧滑菜单处于打开状态， 就不给外层布局上下滑动了
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }else{
                    //如果有别的指头摸过了，那么就return false。这样后续的move...等事件也不会再来找这个View了。
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!canDrag){
                    break;
                }
                int pointerIndex=ev.findPointerIndex(0);
                if(qqInterceptFlag){//当前有侧滑菜单的View，且不是自己的，就该拦截事件咯。滑动也不该出现
                    break;
                }
//                Log.e("tianbin","======MenuItem dispatchTouchEvent======ACTION_MOVE===11111111111111");
                float deltaX= ev.getX(pointerIndex)-mLastX;
                mLastX=ev.getX(pointerIndex);
                //为了在水平滑动中禁止父类ListView等再竖直滑动
                if (Math.abs(deltaX) > 10 || Math.abs(getScrollX()) > 10) {//使屏蔽父布局滑动更加灵敏，
                    getParent().requestDisallowInterceptTouchEvent(true);
//                    Log.e("tianbin","======MenuItem dispatchTouchEvent======ACTION_MOVE===222222222222222");
                }
                if (Math.abs(deltaX) > mTouchSlop) {
                    isUnMoved = false;
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
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            default:
                if(ev.getPointerId(ev.getActionIndex())==0){
                    canDrag=false;
                }
//                Log.e("tianbin","======MenuItem dispatchTouchEvent======ACTION_UP===");
                if (Math.abs(ev.getX() - mFirstX) > mTouchSlop) {
                    isUserSwiped = true;
                }
                if(!qqInterceptFlag){
                    //求伪瞬时速度
                    verTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    final float velocityX = verTracker.getXVelocity(mPointerId);
                    final float velocityY = verTracker.getYVelocity(mPointerId);
//                    Log.e("tianbin",qqInterceptFlag+"=============velocityX:"+velocityX);
                    if (Math.abs(velocityX) > 1000 && Math.abs(velocityY) < 1000) {//滑动速度超过阈值
                        if (velocityX < -1000) {
                            //平滑展开Menu
                            smoothExpand();
                        } else {
                            // 平滑关闭Menu
                            smoothClose();
                        }
                    } else {
                        if (Math.abs(getScrollX()) > ratio) {//否则就判断滑动距离
                            //平滑展开Menu
                            smoothExpand();
                        } else {
                            // 平滑关闭Menu
                            smoothClose();
                        }
                    }
                }
                //释放
                releaseVelocityTracker();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_MOVE:
                if(!canDrag){
                    break;
                }
                int pointerIndex=ev.findPointerIndex(0);
                //屏蔽滑动时的事件(长按事件和侧滑的冲突)
//                Log.e("tianbin","======MenuItem onInterceptTouchEvent======ACTION_MOVE===111111111111111");
                if (Math.abs(ev.getX(pointerIndex) - mFirstX) > mTouchSlop) {
//                    Log.e("tianbin","======MenuItem onInterceptTouchEvent======ACTION_MOVE===22222222222222222");
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            default:
                if (getScrollX() > mTouchSlop) {
                    //这里判断落点在内容区域屏蔽点击，内容区域外，允许传递事件继续向下的。。。
                    if (ev.getX() < getWidth() - getScrollX()) {
                        //仿QQ，侧滑菜单展开时，点击内容区域，关闭侧滑菜单。
                        if (isUnMoved) {
                            smoothClose();
                        }
                        return true;//true表示拦截
                    }
                }
                if (isUserSwiped) {
                    return true;
                }
                break;
        }
        if(qqInterceptFlag){
            return true;
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

    private void smoothExpand(){
        mViewCache=MenuItem.this;
        mScroller.startScroll(getScrollX(),0,mRightMenuWidths-getScrollX(),0);
        invalidate();
    }

    private void smoothClose(){
        mViewCache=null;
        mScroller.startScroll(getScrollX(),0,-getScrollX(),0);
        invalidate();
    }

    /**
     * 快速关闭。
     * 用于 点击侧滑菜单上的选项,同时想让它快速关闭(删除 置顶)。
     * 这个方法在ListView里是必须调用的，
     * 在RecyclerView里，视情况而定，如果是mAdapter.notifyItemRemoved(pos)方法不用调用。
     */
    public void quickClose() {
        if (this == mViewCache) {
//            mViewCache.scrollTo(0, 0);//关闭
            mScroller.startScroll(0,0,0,0,0);
            mViewCache = null;
        }
    }

    //每次ViewDetach的时候，判断一下 ViewCache是不是自己，如果是自己，关闭侧滑菜单，且ViewCache设置为null，
    // 理由：1 防止内存泄漏(ViewCache是一个静态变量)
    // 2 侧滑删除后自己后，这个View被Recycler回收，复用，下一个进入屏幕的View的状态应该是普通状态，而不是展开状态。
    @Override
    protected void onDetachedFromWindow() {
        if (this == mViewCache) {
            mViewCache.smoothClose();
            mViewCache = null;
        }
        super.onDetachedFromWindow();
    }

    //展开时，禁止长按
//    @Override
//    public boolean performLongClick() {
//        if (Math.abs(getScrollX()) > mTouchSlop) {
//            return false;
//        }
//        return super.performLongClick();
//    }

    /**
     * @param event 向VelocityTracker添加MotionEvent
     * @see VelocityTracker#obtain()
     * @see VelocityTracker#addMovement(MotionEvent)
     */
    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * * 释放VelocityTracker
     *
     * @see VelocityTracker#clear()
     * @see VelocityTracker#recycle()
     */
    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
