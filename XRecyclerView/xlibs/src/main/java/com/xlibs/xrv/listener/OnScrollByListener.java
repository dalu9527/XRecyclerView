package com.xlibs.xrv.listener;

/**
 * jude scroll distance
 * Created by xxoo on 2016/5/14.
 */
public interface OnScrollByListener {
    /**
     * jude when to refresh or load more
     * @param dy distance
     */
    void overScrollBy(int dy);
}
