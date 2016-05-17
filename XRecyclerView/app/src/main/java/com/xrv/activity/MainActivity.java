package com.xrv.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.xlibs.xrv.LayoutManager.XLinearLayoutManager;
import com.xlibs.xrv.listener.OnLoadMoreListener;
import com.xlibs.xrv.listener.OnRefreshListener;
import com.xlibs.xrv.view.XRecyclerView;
import com.xrv.R;
import com.xrv.adapter.MyAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xxoo on 2016/5/14.
 */
public class MainActivity extends BaseActivity {
    private final static String TAG = "MainActivity";
    private XRecyclerView mXRecyclerView;
    private View mHeaderView;
    private View mFooterView;
    private MyAdapter mMyAdapter;
    private List<List<String>> mLists = new ArrayList<>();
    private Handler mHandler = new Handler();

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }

        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_main);
        mXRecyclerView = (XRecyclerView)$(R.id.xr_test);
        XLinearLayoutManager xLinearLayoutManager = new XLinearLayoutManager(this);
//        XGridLayoutManager xGridLayoutManager = new XGridLayoutManager(this,2);
//        XStaggeredGridLayoutManager xStaggeredGridLayoutManager =
//                new XStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mXRecyclerView.setLayoutManager(xLinearLayoutManager);

        mHeaderView = LayoutInflater.from(this).inflate(R.layout.custom_header_view, null);
        mFooterView = LayoutInflater.from(this).inflate(R.layout.footer_view, null);
        mXRecyclerView.addHeaderView(mHeaderView, 50);
        mXRecyclerView.addFootView(mFooterView, 50);

        initData();

        mMyAdapter = new MyAdapter(mLists);

        mXRecyclerView.setAdapter(mMyAdapter);

        mXRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });
        mXRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

//        mXRecyclerView.setEnableRefreshAndLoadMore(false);
//        mXRecyclerView.setRefresh(true);
    }

    /**
     * init Load More Data
     */
    private void initLoadMoreData(){
        for(int i = 0; i < 2; i ++){
            List<String> url = new ArrayList<>();
            url.add("http://qq1234.org/uploads/allimg/140404/3_140404114555_5.jpg");
            url.add("http://qq1234.org/uploads/allimg/140404/3_140404114555_4.jpg");
            mLists.add(url);
        }
    }

    /**
     * init refrsh data
     */
    private void initRefreshData(){
        for(int i = 0; i < 1; i ++){
            List<String> url = new ArrayList<>();
            url.add("http://qq1234.org/uploads/allimg/140404/3_140404114606_1.jpg");
            url.add("http://qq1234.org/uploads/allimg/140404/3_140404114555_6.jpg");
            mLists.add(url);
        }
    }

    /**
     * init Data
     */
    private void initData(){
        for(int i = 0; i < 2; i ++){
            List<String> url = new ArrayList<>();
            url.add("http://g.hiphotos.baidu.com/image/pic/item/c75c10385343fbf25431e823b27eca8065388f95.jpg");
            url.add("http://g.hiphotos.baidu.com/image/pic/item/960a304e251f95ca2f34115acd177f3e6609521d.jpg");
            mLists.add(url);
        }
    }

    /**
     * refresh
     */
    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initRefreshData();
                mXRecyclerView.refreshComplate();
            }
        }, 2000);
    }

    /**
     * load more
     */
    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initLoadMoreData();
                mXRecyclerView.loadMoreComplate();
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mXRecyclerView.destroyHandler();
    }
}
