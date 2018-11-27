package com.xie.rlrecycleview.view;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Anthony on 2018/11/22.
 * Describe:
 */
public abstract class BaseLoadMoreFooter extends RelativeLayout {
    public final static int STATE_LOAD_FINISH = 1;
    public final static int STATE_LOADING = 2;
    public final static int STATE_NO_MORE = 3;

    @IntDef({STATE_LOADING, STATE_LOAD_FINISH, STATE_NO_MORE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    private int state = 1;

    public BaseLoadMoreFooter(Context context) {
        super(context);
        init(context);
    }

    public BaseLoadMoreFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseLoadMoreFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 设置状态
     *
     * @param state One of {@link #STATE_LOADING}, {@link #STATE_LOAD_FINISH}, or {@link #STATE_NO_MORE}.
     */
    public void setLoadMoreState(@State int state) {
        this.state = state;
        switch (state) {
            case STATE_LOAD_FINISH:
                loadMoreFinish();
                break;
            case STATE_LOADING:
                Loading();
                break;
            case STATE_NO_MORE:
                showLoadMoreView();
                break;
        }
    }

    public int getState() {
        return state;
    }

    protected abstract void init(Context context);

    protected abstract void Loading();

    protected abstract void loadMoreFinish();

    protected abstract void showLoadMoreView();
}
