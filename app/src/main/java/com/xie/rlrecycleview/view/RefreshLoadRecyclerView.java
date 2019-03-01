package com.xie.rlrecycleview.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by iSmartGo-XIE on 2017/7/5.
 * 自动加载功能带有RecyclerView需要配合BaseWaterFallAdapter使用
 * 滚动监听需要使用setWaterfallOnScrollListener监听
 */

public class RefreshLoadRecyclerView extends RecyclerView {

    private RefreshLoadRecyclerAdapter refreshLoadRecyclerAdapter;

    /**
     * @param context context
     */
    public RefreshLoadRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public RefreshLoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RefreshLoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof RefreshLoadRecyclerAdapter) {
            refreshLoadRecyclerAdapter = (RefreshLoadRecyclerAdapter) adapter;
        }
    }

    private void initView(Context context) {
        setLayoutManager(new LinearLayoutManager(context));

        //设置滚动监听
        addOnScrollListener(new OnScrollListener() {
            //            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
////                if(null!=getLayoutManager() && getLayoutManager() instanceof StaggeredGridLayoutManager){
////                    ((StaggeredGridLayoutManager) getLayoutManager()).invalidateSpanAssignments();
////                }
//            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (refreshLoadRecyclerAdapter != null && refreshLoadRecyclerAdapter.isAutoLoadMore() && dy > 0) {
                    //上滑操作
                    checkTheBottomLoadMore(refreshLoadRecyclerAdapter);
                }
            }
        });
    }

    boolean checkOnTop() {
        int index = -1;
        boolean isFirstViewOnTop = false;
        View topView = null;
        if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            index = staggeredGridLayoutManager.findFirstVisibleItemPositions(null)[0];
            if (index == 1) {
                topView = staggeredGridLayoutManager.findViewByPosition(1);
            }
        } else if (getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
            index = linearLayoutManager.findFirstVisibleItemPosition();
            if (index == 1) {
                topView = linearLayoutManager.findViewByPosition(1);
            }
        }
        if (topView != null) {
            isFirstViewOnTop = getViewTopWithOutMarginPadding(topView);
        }
        //分别是第一个看到的item为0；是否能向下滑动（有view隐藏时有bug，比如刷新头部）；除刷新头部外第一个看到的view的top是否为0
        return index == 0 || !canScrollVertically(-1) || isFirstViewOnTop;
    }

    /**
     * 获取View的top减去边距是否为0
     * @param view
     * @return
     */
    private boolean getViewTopWithOutMarginPadding(@NonNull View view) {
        int marginTop = 0;
        if (view.getLayoutParams() != null) {
            marginTop = ((LayoutParams) view.getLayoutParams()).topMargin;
        }
        return (view.getTop() - marginTop) == 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean isDispatch = false;
        if (refreshLoadRecyclerAdapter != null && refreshLoadRecyclerAdapter.isPullToRefresh()) {
            isDispatch = refreshLoadRecyclerAdapter.dispatchTouchEvent(ev, this);
        }
        return isDispatch || super.dispatchTouchEvent(ev);
    }

    /**
     * 检测是否需要自动加载
     * 当滑动到底部的时候开始自动加载更多
     */
    private void checkTheBottomLoadMore(@NonNull RefreshLoadRecyclerAdapter refreshLoadRecyclerAdapter) {
        if (getLayoutManager() == null) return;
        int startLoadIndex = refreshLoadRecyclerAdapter.getRealItemCount() - refreshLoadRecyclerAdapter.getLoadMoreKey();
        //判断是否滚动到底部
        if (!refreshLoadRecyclerAdapter.isPullLoading() && refreshLoadRecyclerAdapter.getRealItemCount() > 0) {
            int visibleIndex = 0;
            if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                visibleIndex = ((StaggeredGridLayoutManager) getLayoutManager()).findLastVisibleItemPositions(null)[0] - refreshLoadRecyclerAdapter.getHeadersCount();
            } else if (getLayoutManager() instanceof LinearLayoutManager) {
                visibleIndex = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition() - refreshLoadRecyclerAdapter.getHeadersCount();
            }
            //自动加载
            if (visibleIndex >= startLoadIndex)
                refreshLoadRecyclerAdapter.startLoadMore();
        }
    }

    /**
     * 打开默认局部刷新动画
     */
    public void openDefaultAnimator() {
        this.getItemAnimator().setAddDuration(120);
        this.getItemAnimator().setChangeDuration(250);
        this.getItemAnimator().setMoveDuration(250);
        this.getItemAnimator().setRemoveDuration(120);
        ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(true);
    }

    /**
     * 关闭默认局部刷新动画
     */
    public void closeDefaultAnimator() {
        this.getItemAnimator().setAddDuration(0);
        this.getItemAnimator().setChangeDuration(0);
        this.getItemAnimator().setMoveDuration(0);
        this.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(false);
    }
}
