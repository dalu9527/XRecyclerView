package com.xlibs.xrv.LayoutManager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.xlibs.xrv.listener.OnScrollByListener;


/**
 * Created by xxoo on 2016/5/14.
 */
public class XStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

    private OnScrollByListener mListener;

    public XStaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public XStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
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
