package com.xlibs.xrv.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.xlibs.R;
import com.xlibs.xrv.LayoutManager.XGridLayoutManager;
import com.xlibs.xrv.LayoutManager.XLinearLayoutManager;
import com.xlibs.xrv.LayoutManager.XStaggeredGridLayoutManager;
import com.xlibs.xrv.adapter.XWrapAdapter;
import com.xlibs.xrv.listener.OnLoadMoreListener;
import com.xlibs.xrv.listener.OnRefreshListener;
import com.xlibs.xrv.listener.OnScrollByListener;
import com.xlibs.xrv.util.ScreenUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * the recyclerview add head view and foot view
 * Created by xxoo on 2016/5/14.
 */
public class XRecyclerView extends RecyclerView implements Runnable{
    private static final String HEADERVIEW = "headerView";
    private static final String FOOTERVIEW = "footerView";
    private ArrayList<View> mHeaderViews = new ArrayList<>() ;
    private ArrayList<View> mFootViews = new ArrayList<>() ;
    private Adapter mAdapter ;
    private OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isManualLoadMoreData = false; // is manul to load more data
    private boolean isLoadingData = false;
    private boolean isLoadingMoreData = false;
    private int mHeaderViewHeight = -1;
    private int mFooterViewHeight = -1;
    private int mHeaderViewMaxHeight = -1;
    private int mHeaderViewExpandHeight = -1; // header expand view height
    private Handler mHandler = new MyHandler(this);
    private boolean isTouching = false;
    private boolean isEnable = true;
    private Context mContext;
    private View mHeaderView;
    private View mFooterView;


    public XRecyclerView(Context context) {
        super(context);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        startThread(context);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(mHeaderViews.isEmpty()){
            mHeaderView = LayoutInflater.from(mContext).inflate(R.layout.header_view, null);
            if (mHeaderView.getLayoutParams() == null) {
                mHeaderView.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(mContext, 50)));
            }

            markHeaderViewHeight(mHeaderView);
            mHeaderView.getLayoutParams().height = 0;
            mHeaderViews.add(0, mHeaderView);
        }
        if(mFootViews.isEmpty()){
            super.setAdapter(adapter);
        }
        if(!mHeaderViews.isEmpty() || !mFootViews.isEmpty()){
            adapter = new XWrapAdapter(mHeaderViews, mFootViews, adapter) ;
            super.setAdapter(adapter);
        }
        mAdapter = adapter ;
    }

    /**
     * head view to back
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mHeaderView == null) return;
        // back to normal
        if (mHeaderView.getTop() < 0 && mHeaderView.getLayoutParams().height > mHeaderViewHeight) {
            mHeaderView.getLayoutParams().height += mHeaderView.getTop();
            mHandler.obtainMessage(1, mHeaderView.getTop(), 0, mHeaderView).sendToTarget();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                isTouching = false;
                /**time to refresh and hide head view*/
                if (mHeaderView.getLayoutParams().height > mHeaderViewHeight) {
                    refresh();
                    hideView(HEADERVIEW);
                }else{
                    hideView(HEADERVIEW);
                }

                /**time to load more and hide foot view*/
                if(mFooterView != null){
                    if(mFooterView.getLayoutParams().height > mFooterViewHeight){
                        loadMore();
                        hideView(FOOTERVIEW);
                    }else{
                        hideView(FOOTERVIEW);
                    }
                }
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * when to load more
     */
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE && mOnLoadMoreListener != null && !isLoadingMoreData) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof XGridLayoutManager) {
                lastVisibleItemPosition = ((XGridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof XStaggeredGridLayoutManager) {
                int[] into = new int[((XStaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((XStaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((XLinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

            if (isEnable && layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && mHeaderView.getLayoutParams().height <= 0) {
                if(mFooterView != null){
                    if(!isManualLoadMoreData ){// auto
                        mFooterView.setVisibility(VISIBLE);
                        isLoadingMoreData = false;
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoadingMoreData = false;
                }
            }
        }
    }

    @Override
    public void run() {
        final LayoutManager manager = getLayoutManager();
        if (manager instanceof XLinearLayoutManager) {
            ((XLinearLayoutManager) manager).setScrollByListener(mOnScrollListener);
        } else if (manager instanceof XGridLayoutManager) {
            ((XGridLayoutManager) manager).setScrollByListener(mOnScrollListener);
            ((XGridLayoutManager) manager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return ((XWrapAdapter) mAdapter).isHeader(position) ||
                            ((XWrapAdapter) mAdapter).isFooter(position) ? ((XGridLayoutManager) manager).getSpanCount() : 1;
                }
            });
            requestLayout();
        } else if (manager instanceof XStaggeredGridLayoutManager) {
            ((XStaggeredGridLayoutManager) manager).setScrollByListener(mOnScrollListener);

            View view;
            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                if (((XWrapAdapter) mAdapter).isHeader(i)) {
                    view = getChildAt(i);
                    ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams())
                            .setFullSpan(true);
                    view.requestLayout();
                } else {
                    break;
                }
            }
        }
        if (((XWrapAdapter) mAdapter).getFootersCount() > 0) {
            mFootViews.get(0).setVisibility(GONE);
        }
    }

    /**
     * enabale to refresh and load more
     */
    public void setEnableRefreshAndLoadMore(boolean isEnable) {
        this.isEnable = isEnable;
    }

    /**
     * add header View with the head height and expandheight
     */
    public void addHeaderView(View view, int viewHeight, int expandHeight){
        mHeaderViewExpandHeight = ScreenUtil.dip2px(mContext, expandHeight);
        mHeaderViews.clear();
        mHeaderView = view;
        if (mHeaderView.getLayoutParams() == null) {
            mHeaderView.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,  ScreenUtil.dip2px(mContext, viewHeight)));
        }
        markHeaderViewHeight(mHeaderView);
        mHeaderView.getLayoutParams().height = 0;
        mHeaderViews.add(mHeaderView);
        if (mAdapter != null){
            if (!(mAdapter instanceof XWrapAdapter)){
                mAdapter = new XWrapAdapter(mHeaderViews,mFootViews,mAdapter) ;
            }
        }
    }

    /**
     * add header View with the header height
     */
    public void addHeaderView(View view, int viewHeight){
        addHeaderView(view, viewHeight, -1);
    }

    /**
     * add footer view with the footer height
     */
    public void addFootView(View view, int footerHeight){
        addFootView(view, footerHeight, false);
    }

    public void addFootView(View view, int footerHeight, boolean isManual){
        isManualLoadMoreData = isManual;
        mFootViews.clear();
        final LayoutManager manager = getLayoutManager();
        if (view.getLayoutParams() == null) {
            if(manager instanceof XStaggeredGridLayoutManager){
                StaggeredGridLayoutManager.LayoutParams stglmParams = new StaggeredGridLayoutManager.LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(mContext, footerHeight));
                stglmParams.setFullSpan(true);
                view.setLayoutParams(stglmParams);
            }else {
                view.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,  ScreenUtil.dip2px(mContext, footerHeight)));
            }
        }
        markFooterViewHeight(view);
        if(isManualLoadMoreData){
            view.getLayoutParams().height = 0;
        }
        mFootViews.add(view);
        if (mAdapter != null){
            if (!(mAdapter instanceof XWrapAdapter)){
                mAdapter = new XWrapAdapter(mHeaderViews,mFootViews,mAdapter) ;
            }
        }
    }

    /**
     * after loadmore ,you should call this method on the UI thread
     */
    public void loadMoreComplate() {
        isLoadingMoreData = false;
        if (mFooterView != null) {
            if(isManualLoadMoreData){
                mFooterView.getLayoutParams().height = 0;
            }else {
                mFooterView.setVisibility(GONE);
            }
        }
        getAdapter().notifyDataSetChanged();
    }

    /**
     * after refresh ,you should call this method on the UI thread
     */
    public void refreshComplate() {
        isLoadingData = false;
        isLoadingMoreData = false;
        mHeaderView.getLayoutParams().height = 0;
        getAdapter().notifyDataSetChanged();
    }

    /**
     * set refresh
     */
    public void setRefresh(boolean isRefrsh) {
        if (isRefrsh) {
            refresh();
        } else {
            refreshComplate();
        }
    }

    /**
     * mark the footer height
     */
    public void markFooterViewHeight(View view) {
        this.mFooterView = view;
        mFooterViewHeight = mFooterView.getHeight();
        if (mFooterViewHeight <= 0) {
            mFooterViewHeight = mFooterView.getLayoutParams().height;
        } else {
            this.mFooterView.getLayoutParams().height = mFooterViewHeight;
        }
    }

    /**
     * mark the header height
     */
    public void markHeaderViewHeight(View view) {
        this.mHeaderView = view;
        mHeaderViewHeight = mHeaderView.getHeight();
        if (mHeaderViewHeight <= 0) {
            mHeaderViewHeight = mHeaderView.getLayoutParams().height;
        } else {
            mHeaderView.getLayoutParams().height = mHeaderViewHeight;
        }
        mHeaderViewMaxHeight = mHeaderViewExpandHeight < 0 ? mHeaderViewHeight * 2 : mHeaderViewHeight + mHeaderViewExpandHeight;
    }

    /**
     * destroy
     */
    public void destroyHandler(){
        if(mHandler != null){
            mHandler.removeCallbacks(this);
        }
        mHandler = null;
    }

    /**
     * set load more listener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    /**
     * set refresh listener
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    /**
     * start the thread
     */
    private void startThread(Context context) {
        this.mContext = context;
        post(this);
    }

    /**
     * hide the headerview use the animator
     */
    private void hideView(String type) {
        switch (type){
            case HEADERVIEW:
                if(mHeaderView.getLayoutParams().height < mHeaderViewHeight){
                    startAnimator(mHeaderView, mHeaderView.getLayoutParams().height, 0);
                }else {
                    startAnimator(mHeaderView, mHeaderView.getLayoutParams().height, mHeaderViewHeight);
                }
                isLoadingMoreData = false;
                break;
            case FOOTERVIEW:
                if(mFooterView.getLayoutParams().height < mFooterViewHeight){
                    startAnimator(mFooterView, mFooterView.getLayoutParams().height, 0);
                }else {
                    startAnimator(mFooterView, mFooterView.getLayoutParams().height, mFooterViewHeight);
                }
                isLoadingData = false;
                break;
        }
    }

    /**
     * start the anima
     */
    private void startAnimator(final View view, int start, int end){
        ValueAnimator animator = ValueAnimator.ofInt(
                start, end);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.getLayoutParams().height = (int) animation.getAnimatedValue();
                view.requestLayout();
            }
        });
        animator.start();
    }

    /**
     * refresh
     * not allow refresh and loadmore together
     */
    private void refresh() {
        isLoadingData = true;
        isLoadingMoreData = true;
        mOnRefreshListener.onRefresh();
    }

    /**
     * loadmore
     * not allow refresh and loadmore together
     */
    private void loadMore(){
        isLoadingData = true;
        isLoadingMoreData = true;
        mOnLoadMoreListener.onLoadMore();
    }

    /**
     * find the max pos
     */
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private OnScrollByListener mOnScrollListener = new OnScrollByListener() {
        @Override
        public void overScrollBy(int dy) {
            /**dy > 0 is load more; dy < 0 is refresh*/
            if (isEnable && !isLoadingData && isTouching
                    && ((dy < 0 && Math.abs(mHeaderView.getLayoutParams().height) < mHeaderViewMaxHeight)
                    || (dy > 0 && Math.abs(mHeaderView.getLayoutParams().height) > mHeaderViewHeight))) {
                mHandler.obtainMessage(0, dy, 0, null).sendToTarget();
                onScrollChanged(0, 0, 0, 0);
            }else if(isEnable && !isLoadingMoreData && isManualLoadMoreData && isTouching
                    &&(dy > 0 && Math.abs(mFooterView.getLayoutParams().height) < mFooterViewHeight)){
                mHandler.obtainMessage(2, dy, 0, null).sendToTarget();
            }
        }
    };
    /**
     * Instances of static inner classes do not hold an implicit
     * reference to their outer class.
     */
    private static class MyHandler extends Handler {
        private final WeakReference<XRecyclerView> mWeakReference;

        public MyHandler(XRecyclerView xRecyclerView) {
            mWeakReference = new WeakReference<XRecyclerView>(xRecyclerView);
        }

        @Override
        public void handleMessage(Message msg) {
            XRecyclerView xRecyclerView = mWeakReference.get();
            if (xRecyclerView != null) {
                switch (msg.what) {
                    case 0:
                        updateHeaderViewSize(msg.arg1, xRecyclerView);
                        break;
                    case 1:
                        updateHeaderViewSize(-msg.arg1, xRecyclerView);
                        break;
                    case 2:
                        updateFooterViewSize(msg.arg1, xRecyclerView);
                        break;
                }
            }
        }

        /**
         * reset the header view height
         */
        private void updateHeaderViewSize(int dy, XRecyclerView view) {
            if(dy < 0){
                if(view.mHeaderView.getLayoutParams().height > view.mHeaderViewHeight){// pull slowly
                    view.mHeaderView.getLayoutParams().height += -dy / 5;
                }else{// pull nomarl
                    view.mHeaderView.getLayoutParams().height += -dy / 2;
                }
            }else {
                view.isLoadingMoreData = true;// avoid when you pull up , can load more data
                view.mHeaderView.getLayoutParams().height -= dy;
            }
            view.mHeaderView.requestLayout();
        }

        /**
         * reset the footer view height
         */
        private void updateFooterViewSize(int dy, XRecyclerView view) {
            if(dy > 0){
                if(view.mFooterView.getLayoutParams().height < view.mFooterViewHeight){
                    view.mFooterView.getLayoutParams().height += dy / 2;
                }else{
                    view.mFooterView.getLayoutParams().height += dy / 4;
                }
            }
            view.mFooterView.setVisibility(VISIBLE);
            view.mFooterView.requestLayout();
        }
    }
}
