package com.binbin.slidedelmenu;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by -- on 2016/11/8.
 */

public class SlideMenuListView2 extends ListView {
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

    }
}
