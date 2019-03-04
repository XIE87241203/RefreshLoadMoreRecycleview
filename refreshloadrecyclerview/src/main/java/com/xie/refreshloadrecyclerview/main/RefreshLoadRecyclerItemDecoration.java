package com.xie.refreshloadrecyclerview.main;

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
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (refreshLoadRecyclerAdapter != null) {
            int position = parent.getChildAdapterPosition(view);
            if (position > refreshLoadRecyclerAdapter.getHeadersCount() && position < (refreshLoadRecyclerAdapter.getItemCount() - refreshLoadRecyclerAdapter.getFootersCount()))
                outRect.top = space;
        }

    }
}
