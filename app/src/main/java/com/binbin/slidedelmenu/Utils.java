package com.binbin.slidedelmenu;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by -- on 2016/11/11.
 */

public class Utils {
    /**
     * 判断是否点击在view的内部
     * @param view
     * @param ev
     * @return
     *            true 点击在view的内部
     *            false 点击在view的外部
     */
    public static boolean inRangeOfView(View view, MotionEvent ev) {
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
