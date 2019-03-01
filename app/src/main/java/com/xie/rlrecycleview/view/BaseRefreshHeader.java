package com.xie.rlrecycleview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Anthony on 2018/11/23.
 * Describe:
 */
public abstract class BaseRefreshHeader extends LinearLayout {
    public final static int STATE_REFRESH_NORMAL = 0;//正常状态
    public final static int STATE_REFRESH_PREPARE = 1;//准备刷新的状态
    public final static int STATE_REFRESHING = 2;//正在刷新的状态
    public final static int STATE_REFRESH_FINISH = 3;//刷新完成

    public final static int MIN_HEIGHT = 0;

    //下拉高度超过90%就判定为需要刷新
    private final static double REFRESH_HEIGHT_FACTOR = 0.9;

    private ValueAnimator releaseAnimator;
    private int state = STATE_REFRESH_NORMAL;
    public double allOffset = 0;//当前总位移
    private View contentView;
    private boolean isAnimatorCancel = false;
    //下拉刷新监听
    private RefreshLoadRecyclerAdapter.OnRefreshListener onRefreshListener;

    public BaseRefreshHeader(Context context) {
        super(context);
        initView(context);
    }

    public BaseRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BaseRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 正在刷新
     */
    protected void onRefreshStart() {
        state = STATE_REFRESHING;
        if (onRefreshListener != null) onRefreshListener.onRefresh();
    }

    /**
     * 设置Header的显示高度
     */
    void setVisibleHeight(double height) {
        if (height < MIN_HEIGHT) height = MIN_HEIGHT;
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = (int) height;
        setLayoutParams(lp);
        allOffset = height;
    }

    double getVisibleHeight() {
        return allOffset;
    }

    /**
     * 下拉松开
     */
    public void onRelease() {
        if (state == STATE_REFRESHING) return;
        //判断是否可以开始刷新
        if (state == STATE_REFRESH_PREPARE) {
            startRefresh();
        } else {
            int height = getLayoutParams().height;
            if (height == MIN_HEIGHT){
                onRefreshNormal();
                return;
            }
            showHeightAnimator(height, MIN_HEIGHT);
        }
    }

    public void startRefresh() {
        int startHeight = getLayoutParams().height;
        if (startHeight == MIN_HEIGHT) return;
        int endHeight = getRefreshingContentHeight();
        showHeightAnimator(startHeight, endHeight);
    }

    /**
     * 播放改变高度的动画
     *
     * @param startHeight startHeight
     * @param endHeight   endHeight
     */
    private void showHeightAnimator(float startHeight, float endHeight) {
        if (releaseAnimator != null) releaseAnimator.cancel();
        //不刷新，隐藏
        releaseAnimator = ValueAnimator.ofFloat(startHeight, endHeight);
        releaseAnimator.setDuration(200);
        releaseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                setVisibleHeight(value.intValue());
            }

        });
        releaseAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isAnimatorCancel) {
                    switch (state) {
                        case STATE_REFRESH_PREPARE:
                            onRefreshStart();
                            break;
                        case STATE_REFRESH_FINISH:
                            onRefreshNormal();
                            break;
                    }
                } else {
                    isAnimatorCancel = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimatorCancel = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        //开启
        releaseAnimator.start();
    }

    /**
     * 下拉移动
     *
     * @param offSet 单次移动的位移
     */
    public void onMove(double offSet) {
        if (state == STATE_REFRESHING || state == STATE_REFRESH_FINISH) return;
        if (releaseAnimator != null && releaseAnimator.isStarted()) releaseAnimator.cancel();
        double height = offSet + allOffset;
        if (getMaxHeight() != -1 && height > getMaxHeight()) height = getMaxHeight();
        setVisibleHeight(height);
        if (allOffset >= getRefreshingContentHeight() * REFRESH_HEIGHT_FACTOR) {
//            Log.i("testMsg", "onMove1: state:" + state +" allOffset:"+ allOffset);
            if (state != STATE_REFRESH_PREPARE) {
                onRefreshPrepare();
            }
        } else {
//            Log.i("testMsg", "onMove2: state:" + state +" allOffset:"+ allOffset);
            if (state != STATE_REFRESH_NORMAL) {
                onRefreshNormal();
            }
        }
    }

    protected void onRefreshNormal() {
        state = STATE_REFRESH_NORMAL;
    }

    /**
     * 处于可以刷新的状态，已经过了指定距离
     */
    protected void onRefreshPrepare() {
        state = STATE_REFRESH_PREPARE;
    }

    public int getState() {
        return state;
    }

    public void setOnRefreshListener(RefreshLoadRecyclerAdapter.OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    private void initView(Context context) {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        contentView = getContentView(context);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        addView(contentView,lp);
        setVisibleHeight(MIN_HEIGHT);
    }

    protected void onRefreshFinish() {
    }

    public void finishRefresh(){
        state = STATE_REFRESH_FINISH;
        onRelease();
    }

    /**
     * 获取刷新时布局内容的高度
     */
    public abstract int getRefreshingContentHeight();

    /**
     * 获取刷新布局最大下拉高度，-1为无限大
     *
     * @return 刷新布局最大下拉高度
     */
    public abstract int getMaxHeight();

    @NonNull
    public abstract View getContentView(Context context);
}
