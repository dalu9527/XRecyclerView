package com.xlibs.xrv.util;

import android.content.Context;

/**
 * the screen tool, include get the screen's width, height, dip to px, px to dip
 * Created by cherish on 2016/4/18.
 */
public class ScreenUtil {

    /**
     * dip to pixel
     *
     * @param context context
     * @param dipValue dp value
     * @return px
     */
    public static int dip2px(Context context, float dipValue) {
        if(context == null){
            return 0;
        }

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * pixel to dip
     *
     * @param context context
     * @param pxValue px value
     * @return dp
     */
    public static int px2dip(Context context, float pxValue) {
        if(context == null){
            return 0;
        }

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
