package com.xie.rlrecycleview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xie.rlrecycleview.view.BaseRefreshHeader;

/**
 * Created by Anthony on 2018/11/23.
 * Describe:
 */
public class RefreshHeader extends BaseRefreshHeader {
    private TextView textView;

    public RefreshHeader(Context context) {
        super(context);
    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onPrepare() {
        super.onPrepare();
        textView.setText("松开刷新");
    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
        textView.setText("刷新中...");
    }

    @Override
    protected void setRefreshNormal() {
        super.setRefreshNormal();
        textView.setText("继续下拉刷新");
    }

    @Override
    public void onRefreshFinish() {
        super.onRefreshFinish();
        textView.setText("刷新完成");
    }

    @Override
    public int getContentHeight() {
        return 200;
    }

    @Override
    public int getMaxHeight() {
        return -1;
    }

    @NonNull
    @Override
    public View getContentView(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setGravity(Gravity.BOTTOM);
        textView = new TextView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContentHeight());
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setText("继续下拉刷新");
        linearLayout.addView(textView, layoutParams);
        return linearLayout;
    }
}
