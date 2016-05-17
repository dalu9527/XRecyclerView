# XRecyclerView

## 介绍 ##

XRecyclerView：对 Recyclerview 进行了扩展，仿照 ListView 实现了 RecyclerView 下拉刷新和上拉加载更多操作。效果如下：

![](http://i.imgur.com/6lEnaOf.gif)

## 鸣谢 ##

[RecyclerView 添加头部和尾部布局](http://blog.csdn.net/jxxfzgy/article/details/47012097)：本项目的原理就是来源这里

[AnimRefreshRecyclerView](https://github.com/shichaohui/AnimRefreshRecyclerView)：该控件的实现原理也是同上，其含有动画

## 使用方法 ##

**在build.gradle中添加：**

	compile'com.dalu9527:xrecyclerview:0.0.1'


**在布局中使用：**

	<com.xlibs.xrv.view.XRecyclerView
        android:id="@+id/xr_test"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:overScrollMode="never"
        />

RecyclerView 的其他属性不做任何修改

**在 Activity or Fragment 中使用：**

### 基本操作 ###

	private XRecyclerView mXRecyclerView;

	// $ 等价于 findViewById，使用时请使用原来的 findViewById

 	mXRecyclerView = (XRecyclerView)$(R.id.xr_test);
	// 请勿使用系统本身的 LayoutManager ，而是需要使用以下三种 LayoutManager
    XLinearLayoutManager xLinearLayoutManager = new XLinearLayoutManager(this);
    XGridLayoutManager xGridLayoutManager = new XGridLayoutManager(this,2);
    XStaggeredGridLayoutManager xStaggeredGridLayoutManager =
         new XStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    mXRecyclerView.setLayoutManager(xLinearLayoutManager);
	// 添加下拉刷新的头部 和 加载更多的底部，如果不加，默认含有下拉刷新的头部，而没有加载更多的底部
	mHeaderView = LayoutInflater.from(this).inflate(R.layout.custom_header_view, null);
    mFooterView = LayoutInflater.from(this).inflate(R.layout.footer_view, null);

    mXRecyclerView.addHeaderView(mHeaderView, 50);
    mXRecyclerView.addFootView(mFooterView, 50);
	// 设置adapter
 	mMyAdapter = new MyAdapter(mLists);

    mXRecyclerView.setAdapter(mMyAdapter);
	// 添加下拉刷新	
    mXRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
    });
	// 加载更多（如果没有添加加载更多的布局，下面那LoadMore不会执行）
	mXRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
    });
	// mXRecyclerView.setEnableRefreshAndLoadMore(false);
	// mXRecyclerView.setRefresh(false);

 	@Override
    protected void onDestroy() {
        super.onDestroy();
        mXRecyclerView.destroyHandler();
    }

### 详细说明 ###

1. LayoutManager 勿使用系统自身的，下面说明其对应的关系
	1. XLinearLayoutManager --> LinearLayoutManager
	2. XGridLayoutManager --> GridLayoutManager
	3. XStaggeredGridLayoutManager --> StaggeredGridLayoutManager

2. 如果不使用 mXRecyclerView.addHeaderView() 方法，会有一个默认的下拉刷新布局

3. mXRecyclerView.addHeaderView(View view, int viewHeight)，对应的参数分别表示为 下拉刷新头部 view 和 view 的高度（**必须指定高度**），这个方法会默认下拉刷新的最大距离为 2 倍的 viewHeight

4. mXRecyclerView.addHeaderView(View view, int viewHeight, int expandHeight)，这个方法多了一个参数， expandHeight 表示下拉刷新的滑动距离，最终的滑动距离 为 expandHeight + viewHeight

5. 如果不使用 mXRecyclerView.addFootView() 方法，则不存在加载更多操作

6. mXRecyclerView.addFootView(View view, int footerHeight)，对应的方法参数表示为 上拉加载更多的 view 和 view 的高度（**必须指定高度**），这个方法默认是滑倒底部就会自动加载

7. mXRecyclerView.addFootView(View view, int footerHeight, boolean isManual)，这个方法多了一个参数， isManual 表示是否手动上拉加载触发加载更多数据， false 表示 自动触发， true 表示 手动触发，上一个方法默认是 false

8. setOnRefreshListener 必须声明，否则也就达不到下拉刷新的操作了

9. setOnLoadMoreListener 可有可无，如果没有使用 addFootView 方法，则不用声明

10. mXRecyclerView.setEnableRefreshAndLoadMore(boolean) 表示是否开启下拉刷新和加载更多操作，false 表示 不开启， true 表示开启，默认是 true

11. mXRecyclerView.setRefresh(boolean) 表示是否开启下拉刷新，主要用于第一次加载数据时，自动触发下拉刷新操作， false 表示 第一次加载数据不会触发下拉刷新， true 表示 会下拉刷新，默认是 false
12. mXRecyclerView.destroyHandler() 该方法最好在 onDestroy() 中调用

## Introduce ##

XRecyclerView：to extend the RecyclerView, just like the Listview, also can refresh and load more. As shown in the figure below:

![](http://i.imgur.com/6lEnaOf.gif)

## Thanks ##

[RecyclerView 添加头部和尾部布局](http://blog.csdn.net/jxxfzgy/article/details/47012097)：the principle source is here

[AnimRefreshRecyclerView](https://github.com/shichaohui/AnimRefreshRecyclerView)：The implementation principle of this project is the same as above, it contains the animation

## Usage ##

**In build.gradle add：**

	compile'com.dalu9527:xrecyclerview:0.0.1'

**In Your Layout：**

	<com.xlibs.xrv.view.XRecyclerView
        android:id="@+id/xr_test"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:overScrollMode="never"
        />

RecyclerView's other attrs is not change.

**In Your Activity or Fragment：**

### Basic Operation ###

	private XRecyclerView mXRecyclerView;
	// $ is equal findById
 	mXRecyclerView = (XRecyclerView)$(R.id.xr_test);

	// Do not use recyclerview's original LayoutManager ，just use LayoutManager as bellow

    XLinearLayoutManager xLinearLayoutManager = new XLinearLayoutManager(this);
    XGridLayoutManager xGridLayoutManager = new XGridLayoutManager(this,2);
    XStaggeredGridLayoutManager xStaggeredGridLayoutManager =
         new XStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    mXRecyclerView.setLayoutManager(xLinearLayoutManager);

	// add header and footer view

	mHeaderView = LayoutInflater.from(this).inflate(R.layout.custom_header_view, null);
    mFooterView = LayoutInflater.from(this).inflate(R.layout.footer_view, null);

    mXRecyclerView.addHeaderView(mHeaderView, 50);
    mXRecyclerView.addFootView(mFooterView, 50);

	// set adapter

 	mMyAdapter = new MyAdapter(mLists);

    mXRecyclerView.setAdapter(mMyAdapter);

	// setOnRefreshListener 	

    mXRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
    });

	// setOnLoadMoreListener

	mXRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
    });
	// mXRecyclerView.setEnableRefreshAndLoadMore(false);
	// mXRecyclerView.setRefresh(false);

 	@Override
    protected void onDestroy() {
        super.onDestroy();
        mXRecyclerView.destroyHandler();
    }

### Detailed Instruction ###

1. Do not use recyclerview's original LayoutManager, as follow is corresponding relations between them
	1. XLinearLayoutManager --> LinearLayoutManager
	2. XGridLayoutManager --> GridLayoutManager
	3. XStaggeredGridLayoutManager --> StaggeredGridLayoutManager

2. If you not use mXRecyclerView.addHeaderView() method, there is a default refresh layout

3. mXRecyclerView.addHeaderView(View view, int viewHeight), view is the refresh header view, the viewHeight is header view's height（**must have viewHeight**）, the max distance pull to refresh is two times than viewHeight

4. mXRecyclerView.addHeaderView(View view, int viewHeight, int expandHeight), the expandHeight is the distance to pull refresh, the final max distance pull to refresh is expandHeight + viewHeight

5. If you not use mXRecyclerView.addFootView() method, can not load more

6. mXRecyclerView.addFootView(View view, int footerHeight), view is the load more footer view, the viewHeight is footer view's height（**must have viewHeight**）, it can load more automatic

7. mXRecyclerView.addFootView(View view, int footerHeight, boolean isManual), isManual indicates whether or not to load more data on hand, if false, it auto load more, if true, it load more on hand

8. setOnRefreshListener() must declare

9. setOnLoadMoreListener() is optional, if you not have  addFootView method, just not setOnLoadMoreListener

10. mXRecyclerView.setEnableRefreshAndLoadMore(boolean)   indicate whether or not to open refresh and load more, if false, it not hava refresh and load more operation, if true, it can, default is true.

11. mXRecyclerView.setRefresh(boolean) indicate whether or not to open refresh, mainly to load data first time, if false, not refresh, just see the original data, if true, load original data and load refresh data, default is false
12. mXRecyclerView.destroyHandler() better is used on onDestroy() method