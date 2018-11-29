package com.xie.rlrecycleview.view;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Anthony on 2018/11/29.
 * Describe:
 */
public class RefreshLoadRecyclerItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private RefreshLoadRecyclerAdapter refreshLoadRecyclerAdapter;

    public RefreshLoadRecyclerItemDecoration(@NonNull RefreshLoadRecyclerAdapter refreshLoadRecyclerAdapter, int space) {
        this.space = space;
        this.refreshLoadRecyclerAdapter = refreshLoadRecyclerAdapter;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (refreshLoadRecyclerAdapter != null && parent.getChildAdapterPosition(view) > refreshLoadRecyclerAdapter.getHeadersCount())
            outRect.top = space;
    }
}
