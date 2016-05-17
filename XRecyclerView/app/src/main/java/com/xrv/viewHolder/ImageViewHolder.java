package com.xrv.viewHolder;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xrv.R;

import java.util.List;

/**
 * @author xxoo
 * @date 5/12/2016.
 */
public class ImageViewHolder extends BaseViewHolder<String>{
    private static final String TAG = "ImageViewHolder";
    private LinearLayout mLayout;
    private SimpleDraweeView mImage_1;
    private SimpleDraweeView mImage_2;
    public ImageViewHolder(View itemView) {
        super(itemView);
        mLayout = (LinearLayout)itemView.findViewById(R.id.layout);
        mImage_1 = (SimpleDraweeView)itemView.findViewById(R.id.image2_1);
        mImage_2 = (SimpleDraweeView)itemView.findViewById(R.id.image2_2);
    }

    @Override
    public void setData(List<String> data) {
        super.setData(data);
        mImage_1.setImageURI(Uri.parse(data.get(0)));
        mImage_2.setImageURI(Uri.parse(data.get(1)));
        mImage_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Log.e(TAG,"click left");
            }
        });
        mImage_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"click right");
            }
        });
    }


}
