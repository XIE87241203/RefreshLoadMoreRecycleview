package com.xie.rlrecycleview.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by iSmartGo-XIE on 2017/7/5.
 * 通用ViewHolder
 */

public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }


    public static BaseRecyclerViewHolder createViewHolder(View itemView) {
        return new BaseRecyclerViewHolder(itemView);
    }

    public static BaseRecyclerViewHolder createViewHolder(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new BaseRecyclerViewHolder(itemView);
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId viewId
     * @return View
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }
}