package com.xie.rlrecycleview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Anthony on 2018/11/23.
 * Describe:
 */
public abstract class BaseRefreshHeader extends RelativeLayout {
    public final static int STATE_REFRESH_NORMAL = 0;//正常状态
    public final static int STATE_PREPARE_REFRESH = 1;//准备刷新的状态
    public final static int STATE_REFRESHING = 2;//正在刷新的状态
    public final static int STATE_REFRESH_FINISH = 3;//刷新完成

    private final static int MIN_HEIGHT = 1;

    //下拉高度超过90%就判定为需要刷新
    private final static double REFRESH_HEIGHT_FACTOR = 0.9;

    private ValueAnimator releaseAnimator;
    private int state = STATE_REFRESH_NORMAL;
    public double allOffset = 0;//当前总位移
    private View contentView;
    private boolean isAnimatorCancel = false;
    //下拉刷新监听
    private AutoLoadRecyclerAdapter.OnRefreshListener onRefreshListener;

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
    public void startRefreshing() {
        state = STATE_REFRESHING;
        if (onRefreshListener != null) onRefreshListener.onRefresh();
    }

    /**
     * 设置Header的显示高度
     */
    void setVisibleHeight(int height) {
        if (height < MIN_HEIGHT) height = MIN_HEIGHT;
        ViewGroup.LayoutParams lp = contentView.getLayoutParams();
        lp.height = height;
        contentView.setLayoutParams(lp);
        allOffset = height;
    }

    /**
     * 下拉松开
     */
    public void onRelease() {
        if (state == STATE_REFRESHING) return;
        int height = contentView.getLayoutParams().height;
        if (height == MIN_HEIGHT) return;
        int minHeight = MIN_HEIGHT;
        //判断是否可以开始刷新
        if (state == STATE_PREPARE_REFRESH) {
            minHeight = getContentHeight();
        }
        if (releaseAnimator != null) releaseAnimator.cancel();
        Log.i("testMsg", "Animator: height:" + height + " minHeight" + minHeight);
        //不刷新，隐藏
        releaseAnimator = ValueAnimator.ofFloat(height, minHeight);
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
                Log.i("testMsg", "onAnimationEnd: ");
                if (!isAnimatorCancel) {
                    switch (state) {
                        case STATE_PREPARE_REFRESH:
                            startRefreshing();
                            break;
                        case STATE_REFRESH_FINISH:
                            setRefreshNormal();
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
        Log.i("testMsg", "state: " + state);
        if (state == STATE_REFRESHING || state == STATE_REFRESH_FINISH) return;
        if (releaseAnimator != null && releaseAnimator.isStarted()) releaseAnimator.cancel();
        int height = (int) (offSet + allOffset);
        if (getMaxHeight() != -1 && height > getMaxHeight()) height = getMaxHeight();
        setVisibleHeight(height);
        if (allOffset >= getContentHeight() * REFRESH_HEIGHT_FACTOR) {
            if (state != STATE_PREPARE_REFRESH) {
                onPrepare();
            }
        } else {
            if (state != STATE_REFRESH_NORMAL) {
                setRefreshNormal();
            }
        }
    }

    public void setRefreshNormal() {
        state = STATE_REFRESH_NORMAL;
        onRelease();
    }

    /**
     * 处于可以刷新的状态，已经过了指定距离
     */
    public void onPrepare() {
        state = STATE_PREPARE_REFRESH;
    }

    public int getState() {
        return state;
    }

    public void setOnRefreshListener(AutoLoadRecyclerAdapter.OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    private void initView(Context context) {
        contentView = getContentView(context);
        addView(contentView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setVisibleHeight(MIN_HEIGHT);
    }

    public void onRefreshFinish() {
        state = STATE_REFRESH_FINISH;
        onRelease();
    }

    /**
     * 获取刷新布局内容的高度
     */
    public abstract int getContentHeight();

    /**
     * 获取刷新布局最大下拉高度，-1为无限大
     *
     * @return 刷新布局最大下拉高度
     */
    public abstract int getMaxHeight();

    @NonNull
    public abstract View getContentView(Context context);
}
