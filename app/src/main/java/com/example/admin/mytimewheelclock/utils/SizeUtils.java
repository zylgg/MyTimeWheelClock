package com.example.admin.mytimewheelclock.utils;

import android.content.Context;
import android.view.View;

/**
 * 尺寸转换工具类
 */
public class SizeUtils {
    public static int dp2px(Context context, float dp) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    public static int px2dp(Context context, float px) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5);
    }

    /**
     * 测量控件尺寸
     *
     * @param measureSpec
     * @return
     */
    public static int measureSize(Context mContext,int measureSpec) {
        int result;
        int specSize = View.MeasureSpec.getSize(measureSpec);
        int specMode = View.MeasureSpec.getMode(measureSpec);
        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = SizeUtils.dp2px(mContext, 300);
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

}
