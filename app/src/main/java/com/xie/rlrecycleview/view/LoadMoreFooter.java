package com.xie.rlrecycleview.view;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Anthony on 2018/11/22.
 * Describe:
 */
public class LoadMoreFooter extends BaseLoadMoreFooter {
    private TextView textView;

    public LoadMoreFooter(Context context) {
        super(context);
    }

    @Override
    protected void init(Context context) {
        textView = new TextView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        addView(textView,layoutParams);
    }

    @Override
    protected void Loading() {
        textView.setVisibility(VISIBLE);
        textView.setText("加载中...");
    }

    @Override
    protected void loadMoreFinish() {
        textView.setVisibility(GONE);
    }

    @Override
    protected void showLoadMoreView() {
        textView.setVisibility(VISIBLE);
        textView.setText("没有更多了");
    }
}
