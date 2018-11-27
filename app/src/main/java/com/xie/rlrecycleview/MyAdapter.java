package com.xie.rlrecycleview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xie.rlrecycleview.view.AutoLoadRecyclerAdapter;
import com.xie.rlrecycleview.view.BaseRecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anthony on 2018/11/23.
 * Describe:
 */
public class MyAdapter extends AutoLoadRecyclerAdapter {
    private List<Integer> datas;

    public MyAdapter(Context context) {
        super(context);
        datas = new ArrayList<>();
    }

    public void addDatas(List<Integer> datas) {
        if (datas.isEmpty()) return;
        int originalSize = this.datas.size();
        this.datas.addAll(datas);
        notifyItemRangeInserted(originalSize + getHeadersCount(), datas.size());
    }

    public void setDatas(List<Integer> datas) {
        this.datas.clear();
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    protected BaseRecyclerViewHolder onCreateViewHolderNew(ViewGroup parent, int viewType) {
        return BaseRecyclerViewHolder.createViewHolder(context, parent, R.layout.item_list);
    }

    @Override
    protected int getItemViewTypeNew(int position) {
        return 0;
    }

    @Override
    protected void onBindViewHolderNew(BaseRecyclerViewHolder holder, final int position) {
        TextView textview = holder.getView(R.id.textview);
        textview.setText(String.valueOf(datas.get(position)));
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,String.valueOf(position),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getRealItemCount() {
        return datas.size();
    }
}
