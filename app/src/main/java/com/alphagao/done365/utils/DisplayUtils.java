package com.alphagao.done365.utils;

import android.content.Context;

/**
 * Created by Alpha on 2017/3/8.
 */

public class DisplayUtils {
    public static float dp2Px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }
}
