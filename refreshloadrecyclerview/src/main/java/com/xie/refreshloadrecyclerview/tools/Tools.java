package com.xie.refreshloadrecyclerview.tools;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Anthony on 2019/01/11.
 * Describe:
 */
public class Tools {
    /**
     * dp转px
     *
     * @param context
     * @param n
     * @return
     */
    public static int dp2px(Context context, float n) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        n = (int) (n * metrics.density);
        return (int) n;
    }
}
