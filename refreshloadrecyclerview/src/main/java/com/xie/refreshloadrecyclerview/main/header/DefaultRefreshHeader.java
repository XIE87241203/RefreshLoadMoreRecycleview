package com.xie.refreshloadrecyclerview.main.header;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Anthony on 2018/11/23.
 * Describe:
 */
public class DefaultRefreshHeader extends BaseRefreshHeader {
    private TextView textView;

    public DefaultRefreshHeader(Context context) {
        super(context);
    }

    public DefaultRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onRefreshPrepare() {
        super.onRefreshPrepare();
        textView.setText("松开刷新");
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        textView.setText("刷新中...");
    }

    @Override
    protected void onRefreshNormal() {
        super.onRefreshNormal();
        textView.setText("继续下拉刷新");
    }

    @Override
    public void onRefreshFinish() {
        super.onRefreshFinish();
        textView.setText("刷新完成");
    }

    @Override
    public int getRefreshingContentHeight() {
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
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getRefreshingContentHeight());
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setText("继续下拉刷新");
        linearLayout.addView(textView, layoutParams);
        return linearLayout;
    }
}
