package com.xlibs.xrv.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.xlibs.xrv.viewHolder.FooterViewHolder;
import com.xlibs.xrv.viewHolder.HeaderViewHolder;

import java.util.ArrayList;

/**
 * recycleview adpter for head and foot view
 * just like the listview
 *
 * Created by xxoo on 2016/5/14.
 */
public class XWrapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Used as a placeholder in case the provided info views are indeed null.
    // Currently only used by some CTS tests, which may be removed.
    final ArrayList<View> EMPTY_INFO_LIST = new ArrayList<>();// empty view

    private RecyclerView.Adapter mAdapter;
    // These two ArrayList are assumed to NOT be null.
    // They are indeed created when declared in RecyclerView and then shared.
    private ArrayList<View> mHeaderViews;
    private ArrayList<View> mFootViews;
    private int headerPosition = 0;

    public XWrapAdapter(ArrayList<View> mHeaderViews, ArrayList<View> mFootViews, RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
        if (mHeaderViews == null) {
            this.mHeaderViews = EMPTY_INFO_LIST;
        } else {
            this.mHeaderViews = mHeaderViews;
        }
        if (mFootViews == null) {
            this.mFootViews = EMPTY_INFO_LIST;
        } else {
            this.mFootViews = mFootViews;
        }
    }

    @Override
    public int getItemViewType(int position) {
        // head view
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return RecyclerView.INVALID_TYPE;
        }
        // the normal view
        int adjPosition = position - numHeaders;
        int adapterCount;
        if (mAdapter != null) {
            adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemViewType(adjPosition);
            }
        }
        // footer view
        return RecyclerView.INVALID_TYPE - 1;
    }

    @Override
    public long getItemId(int position) {
        int numHeaders = getHeadersCount();
        if (mAdapter != null && position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemId(adjPosition);
            }
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == RecyclerView.INVALID_TYPE) {
            return new HeaderViewHolder(mHeaderViews.get(headerPosition++));
        } else if (viewType == RecyclerView.INVALID_TYPE - 1) {
            return new FooterViewHolder(mFootViews.get(0));
        }
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Header (negative positions will throw an IndexOutOfBoundsException)
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return;
        }
        // Adapter
        int adjPosition = position - numHeaders;
        int adapterCount;
        if (mAdapter != null) {
            adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                mAdapter.onBindViewHolder(holder, adjPosition);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mAdapter != null) {
            return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
        } else {
            return getHeadersCount() + getFootersCount();
        }
    }

    /**
     * jude is head view
     * @param position pos
     * @return boolean
     */
    public boolean isHeader(int position) {
        return position >= 0 && position < mHeaderViews.size();
    }

    /**
     * jude is foot view
     * @param position pos
     * @return boolean
     */
    public boolean isFooter(int position) {
        return position < getItemCount() && position >= getItemCount() - mFootViews.size();
    }

    /**
     * headview count
     * @return int
     */
    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    /**
     * foot view count
     * @return int
     */
    public int getFootersCount() {
        return mFootViews.size();
    }
}
