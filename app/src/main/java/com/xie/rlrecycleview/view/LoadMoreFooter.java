package com.xie.rlrecycleview.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xie.rlrecycleview.Tools;

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
        int padding = Tools.dp2px(context, 15);
        textView.setPadding(0, padding, 0, padding);
        addView(textView,layoutParams);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getState() == STATE_LOAD_ERROR) {
                    //加载错误点击重新加载
                    startLoadMore();
                }
            }
        });
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
    protected void showNoMoreView() {
        textView.setVisibility(VISIBLE);
        textView.setText("没有更多了");
    }

    @Override
    protected void loadMoreError() {
        textView.setVisibility(VISIBLE);
        textView.setText("加载失败，点此重新加载");
    }
}
