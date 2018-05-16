package com.android.eng.drydemo.Utils;

import android.content.Context;

/**
 * Created by Eng on 2018/5/16.
 * Dimension conversion tool
 */

public class SizeUtils {
    /**
     * convert dp to px
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * convert px to dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
