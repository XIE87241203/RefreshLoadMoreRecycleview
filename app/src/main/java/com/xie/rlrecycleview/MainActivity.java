package com.xie.rlrecycleview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.xie.rlrecycleview.view.RefreshLoadRecyclerAdapter;
import com.xie.rlrecycleview.view.RefreshLoadRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Integer> datas;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        datas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            datas.add(i);
        }
        RefreshLoadRecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setDatas(datas);
        adapter.setAutoLoadEnable(true, 3);
        adapter.setOnLoadMoreListener(new RefreshLoadRecyclerAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        Log.i("testMsg", "load more");
                        int startIndex = datas.get(datas.size() - 1) + 1;
                        datas.clear();
                        for (int i = 0; i < 20; i++) {
                            datas.add(startIndex + i);
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.addDatas(datas);
                                if (datas.get(0) > 200) {
                                    adapter.showNoMoreHint();
                                } else {
                                    adapter.finishLoadMore();
                                }
                            }
                        });
                    }
                });
                thread.start();
            }
        });
        adapter.setPullToRefreshEnable(true);
        adapter.setOnRefreshListener(new RefreshLoadRecyclerAdapter.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("testMsg", "onRefresh: ");
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.finishRefresh();
                            }
                        });
                    }
                });
                thread.start();
            }
        });
    }
}
