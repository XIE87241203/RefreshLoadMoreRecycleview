package com.xie.rlrecycleview.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Anthony-XIE on 2017/7/6.
 */

public abstract class RefreshLoadRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 100001;
    private static final int SPECIAL_ITEM_TYPE_REFRESH_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;
    private static final int SPECIAL_ITEM_TYPE_LOAD_FOOTER = 1000000;

    protected Context context;

    //加载更多布局
    private BaseLoadMoreFooter loadMoreFooterView;
    //自动加载开关
    private boolean isAutoLoadMore = false;

    //自动加载开关
    private boolean isPullToRefresh = false;
    //剩下多少个item时才开始加载
    private int loadMoreKey = 0;

    //容器
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();
    private OnRefreshListener onRefreshListener;
    private OnLoadMoreListener onLoadMoreListener;

    //代替onCreateViewHolder
    protected abstract BaseRecyclerViewHolder onCreateViewHolderNew(ViewGroup parent, int viewType);

    //代替getItemViewType
    protected abstract int getItemViewTypeNew(int position);

    //代替onBindViewHolder
    protected abstract void onBindViewHolderNew(BaseRecyclerViewHolder holder, int position);

    //获取内容Item数量
    protected abstract int getRealItemCount();

    public RefreshLoadRecyclerAdapter(Context context) {
        this.context = context;
        refreshHeader = new RefreshHeader(context);
        setRefreshHeader(refreshHeader);
        loadMoreFooterView = new LoadMoreFooter(context);
        setLoadMoreFooter(loadMoreFooterView);
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            //头部
            return BaseRecyclerViewHolder.createViewHolder(mHeaderViews.get(viewType));
        } else if (mFootViews.get(viewType) != null) {
            //尾部
            return BaseRecyclerViewHolder.createViewHolder(mFootViews.get(viewType));
        }
        //内容部分
        return onCreateViewHolderNew(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return getItemViewTypeNew(position - getHeadersCount());
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder holder, int position) {
        if (isHeaderViewPos(position)) {
            return;
        }
        if (isFooterViewPos(position)) {
            return;
        }
        onBindViewHolderNew(holder, position - getHeadersCount());
    }

    @Override
    public int getItemCount() {
        return getHeadersCount() + getFootersCount() + getRealItemCount();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseRecyclerViewHolder holder) {
        //处理StaggeredGridLayout类型
        int position = holder.getAdapterPosition();
        if (position == RecyclerView.NO_POSITION) {
            position = holder.getLayoutPosition();
        }
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        //处理gridLayout类型

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (mHeaderViews.get(viewType) != null) {
                        return gridLayoutManager.getSpanCount();
                    } else if (mFootViews.get(viewType) != null) {
                        return gridLayoutManager.getSpanCount();
                    }
                    if (spanSizeLookup != null)
                        return spanSizeLookup.getSpanSize(position);
                    return 1;
                }
            });
            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }
    }

    /**
     * 判断是不是Header
     *
     * @param position position
     * @return boolean
     */
    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    /**
     * 判断是不是Footer
     *
     * @param position position
     * @return boolean
     */
    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    /**
     * 添加Header
     *
     * @param view view
     */
    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    /**
     * 删除Header
     *
     * @param view view
     */
    public void removeHeaderView(View view) {
        int index = mHeaderViews.indexOfValue(view);
        if (index != -1) {
            mHeaderViews.removeAt(index);
        }
    }


    /**
     * 添加Footer
     *
     * @param view view
     */
    public void addFooterView(View view) {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    /**
     * 删除Footer
     *
     * @param view view
     */
    public void removeFooterView(View view) {
        int index = mFootViews.indexOfValue(view);
        if (index != -1) {
            mFootViews.removeAt(index);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    /**
     * 设置刷新footer
     *
     * @param loadMoreFooter loadMoreFooter
     */
    public void setLoadMoreFooter(@NonNull BaseLoadMoreFooter loadMoreFooter) {
        loadMoreFooterView = loadMoreFooter;
        loadMoreFooterView.setOnLoadMoreListener(onLoadMoreListener);
        loadMoreFooterView.setLoadMoreState(BaseLoadMoreFooter.STATE_LOAD_FINISH);
        mFootViews.put(SPECIAL_ITEM_TYPE_LOAD_FOOTER + BASE_ITEM_TYPE_FOOTER, loadMoreFooterView);
        notifyDataSetChanged();
    }

    /**
     * 获取Header Item数量
     *
     * @return int
     */
    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    /**
     * 获取HeaderViews
     *
     * @return SparseArrayCompat
     */
    public SparseArrayCompat<View> getHeaderViews() {
        return mHeaderViews;
    }

    /**
     * 获取HeaderViews
     *
     * @return SparseArrayCompat
     */
    public SparseArrayCompat<View> getFootViews() {
        return mFootViews;
    }

    /**
     * 获取Footer Item数量
     *
     * @return int
     */
    public int getFootersCount() {
        return mFootViews.size();
    }


    /**
     * 开始加载
     */
    public void startLoadMore() {
        if (loadMoreFooterView == null) return;
        loadMoreFooterView.startLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        loadMoreFooterView.setOnLoadMoreListener(onLoadMoreListener);
    }

    /**
     * 加载完成
     */
    public void finishLoadMore() {
        if (loadMoreFooterView == null) return;
        loadMoreFooterView.setLoadMoreState(BaseLoadMoreFooter.STATE_LOAD_FINISH);
    }

    /**
     * 停止加载,显示“没有更多了”
     */
    public void showNoMoreHint() {
        if (loadMoreFooterView == null) return;
        loadMoreFooterView.setLoadMoreState(BaseLoadMoreFooter.STATE_NO_MORE);
    }

    /**
     * 显示加载错误
     */
    public void showLoadMoreError(){
        if (loadMoreFooterView == null) return;
        loadMoreFooterView.setLoadMoreState(BaseLoadMoreFooter.STATE_LOAD_ERROR);
    }

    /**
     * 重设自动加载状态
     */
    public void resetLoadMoreState() {
        if (loadMoreFooterView == null) return;
        loadMoreFooterView.setLoadMoreState(BaseLoadMoreFooter.STATE_LOAD_FINISH);
    }

    boolean isPullLoading() {
        return loadMoreFooterView.getState() != BaseLoadMoreFooter.STATE_LOAD_FINISH;
    }

    public boolean isLoading() {
        return loadMoreFooterView.getState() == BaseLoadMoreFooter.STATE_LOADING;
    }

    public boolean isNoMore() {
        return loadMoreFooterView.getState() == BaseLoadMoreFooter.STATE_NO_MORE;
    }

    public boolean isLoadMoreError(){
        return loadMoreFooterView.getState() == BaseLoadMoreFooter.STATE_LOAD_ERROR;
    }

    /**
     * 自动加载开关
     *
     * @return isAutoLoadMore
     */
    public boolean isAutoLoadMore() {
        return isAutoLoadMore;
    }

    /**
     * 设置自动加载
     *
     * @param autoLoadMore boolean 是否可自动刷新
     * @param loadMoreKey  剩下多少个item没划的时候开始加载，默认是0
     */
    public void setAutoLoadEnable(boolean autoLoadMore, int loadMoreKey) {
        isAutoLoadMore = autoLoadMore;
        this.loadMoreKey = loadMoreKey;
    }

    /**
     * 获取自动加载关键值
     *
     * @return 剩下多少个item没划的时候开始加载，默认是0
     */
    public int getLoadMoreKey() {
        return loadMoreKey;
    }

    //--------------------------------下拉刷新部分--------------------------------//
    private BaseRefreshHeader refreshHeader;
    private float startY = -1;
    private float startX = -1;
    private float allStartY = -1;
    private float allStartX = -1;
    private boolean isTouch = false;//防止惯性滑动触发刷新用
    private boolean isDispatch = false;//是否处理掉触摸事件
    //头部滑动的阻力系数
    private float MOVE_RESISTANCE_FACTOR = 2.5F;

    public interface OnRefreshListener {
        void onRefresh();
    }

    boolean dispatchTouchEvent(MotionEvent e, RefreshLoadRecyclerView recyclerView) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Log.i("testMsg", "dispatchTouchEvent: ACTION_DOWN");
                //记录与上一次移动的位移
                startY = e.getRawY();
                startX = e.getRawX();
                //记录开始点的位移
                allStartX = e.getRawX();
                allStartY = e.getRawY();
                isTouch = true;
                isDispatch = false;
                return false;
            case MotionEvent.ACTION_MOVE:
//                Log.i("testMsg", "dispatchTouchEvent: ACTION_MOVE");
                float deltaY = (e.getRawY() - startY);
                float deltaX = (e.getRawX() - startX);
                //距离开始点的位移
//                float offsetX = (e.getRawX() - allStartX);
                float offsetY = (e.getRawY() - allStartY);
                startY = e.getRawY();
                startX = e.getRawX();
//                Log.i("testMsg", "dispatchTouchEvent: isTouch:" + isTouch + " checkOnTop:" + recyclerView.checkOnTop() + " deltaY:" + deltaY + "refreshHeader.getVisibleHeight()" + refreshHeader.getVisibleHeight());
                //分别判断是否是触摸拖动、是否垂直触摸、是否越界拖动、是否处于拖动头部的状态
//                Log.i("testMsg", "isTouch: " + isTouch + " offsetY:" + offsetY + "offsetX:" + offsetX);
                if (isTouch && Math.abs(deltaY) > Math.abs(deltaX)) {
                    if (((deltaY > 0 && recyclerView.checkOnTop()) || refreshHeader.getVisibleHeight() > BaseRefreshHeader.MIN_HEIGHT)) {
                        //防止异常回弹(需要根据屏幕密度判断)
//                      if(Math.abs(deltaY)<100){
                        refreshHeader.onMove(deltaY / MOVE_RESISTANCE_FACTOR);
//                      }
                        isDispatch = true;
                    } else {
                        isDispatch = false;
                    }
                }
                return isDispatch;
            case MotionEvent.ACTION_UP:
//                Log.i("testMsg", "dispatchTouchEvent: ACTION_UP");
                refreshHeader.onRelease();
                isTouch = false;
                startY = -1;
                allStartX = -1;
                allStartY = -1;
                //如果有下拉刷新触摸的话，不分发触摸事件
                if (isDispatch) {
                    isDispatch = false;
                    return true;
                } else {
                    return false;
                }
        }
        return false;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        //下拉刷新监听
        this.onRefreshListener = onRefreshListener;
        if (refreshHeader != null) refreshHeader.setOnRefreshListener(onRefreshListener);
    }

    /**
     * 设置刷新头部
     *
     * @param refreshHeader refreshHeader
     */
    public void setRefreshHeader(@NonNull BaseRefreshHeader refreshHeader) {
        this.refreshHeader = refreshHeader;
        refreshHeader.setOnRefreshListener(onRefreshListener);
        refreshHeader.setVisibleHeight(1);
        mHeaderViews.put(SPECIAL_ITEM_TYPE_REFRESH_HEADER, refreshHeader);
        notifyDataSetChanged();
    }

    public void startRefresh(){
        if (refreshHeader != null)
            refreshHeader.startRefresh();
    }

    public void finishRefresh() {
        if (refreshHeader != null)
            refreshHeader.finishRefresh();
        finishLoadMore();
    }

    /**
     * 设置下拉刷新
     *
     * @param pullToRefresh 是否开启下拉刷新
     */
    public void setPullToRefreshEnable(boolean pullToRefresh) {
        isPullToRefresh = pullToRefresh;
    }

    /**
     * 获取下拉刷新开关状态
     *
     * @return 下拉刷新开关状态
     */
    public boolean isPullToRefresh() {
        return isPullToRefresh;
    }

}
