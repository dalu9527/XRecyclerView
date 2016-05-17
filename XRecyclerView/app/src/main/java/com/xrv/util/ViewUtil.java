package com.xrv.util;

import android.app.Activity;
import android.view.View;

/**
 * @author xxoo
 * @date 5/12/2016.
 */
public final class ViewUtil {
    public static <E extends View> E findViewById(Activity activity, int resId){
        return (E) activity.findViewById(resId);
    }

    public static <E extends View> E findViewById(View view, int resId){
        return (E) view.findViewById(resId);
    }

}
