package com.xlibs.xrv.LayoutManager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.xlibs.xrv.listener.OnScrollByListener;


/**
 * Created by xxoo on 2016/5/14.
 */
public class XGridLayoutManager extends GridLayoutManager {

    private OnScrollByListener mListener;

    public XGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public XGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public XGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrollRange = super.scrollVerticallyBy(dy, recycler, state);

        mListener.overScrollBy(dy - scrollRange);

        return scrollRange;
    }


    public void setScrollByListener(OnScrollByListener listener) {
        mListener = listener;
    }

}
