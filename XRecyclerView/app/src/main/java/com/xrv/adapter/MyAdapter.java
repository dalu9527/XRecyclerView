package com.xrv.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xrv.R;
import com.xrv.viewHolder.ImageViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xxoo
 * @date 5/12/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<List<String>> mUrl = new ArrayList<>();

    public MyAdapter(List<List<String>> data) {
        mUrl = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_2_image_layout, parent, false);
            return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            if(mUrl.size() != 0){
                ((ImageViewHolder) holder).setData(mUrl.get(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mUrl.size();
    }
}
