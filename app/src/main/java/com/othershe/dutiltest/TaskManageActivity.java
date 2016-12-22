package com.othershe.dutiltest;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.othershe.baseadapter.ViewHolder;
import com.othershe.baseadapter.interfaces.OnItemChildClickListener;
import com.othershe.dutil.data.DownloadData;
import com.othershe.dutil.download.DownloadMangerPool;

import java.util.ArrayList;
import java.util.List;

public class TaskManageActivity extends AppCompatActivity {
    private String url1 = "http://1.82.242.43/imtt.dd.qq.com/16891/DC9E925209B19E7913477E7A0CCE6E52.apk";//欢乐斗地主
    private String url2 = "http://117.23.1.170/imtt.dd.qq.com/16891/37F5264B6EDC71F9A7888B5017A5A6C1.apk";//球球大作战
    private String url3 = "http://117.23.1.172/imtt.dd.qq.com/16891/8AFB093FEFF9DE2A81EDC28EB1AF89C6.apk";//节奏大师

    private String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    private RecyclerView downloadList;
    private DownloadListAdapter downloadListAdapter;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manage);

        mContext = this;

        downloadList = (RecyclerView) findViewById(R.id.download_list);
        List<DownloadData> datas = new ArrayList<>();
        datas.add(new DownloadData(url1, path, "欢乐斗地主.apk"));
        datas.add(new DownloadData(url2, path, "球球大作战.apk"));
        datas.add(new DownloadData(url3, path, "节奏大师.apk"));

        downloadListAdapter = new DownloadListAdapter(this, datas, false);

        //开始
        downloadListAdapter.setOnItemChildClickListener(R.id.start, new OnItemChildClickListener<DownloadData>() {
            @Override
            public void onItemChildClick(ViewHolder viewHolder, DownloadData data, int position) {
                DownloadMangerPool.getInstance(mContext).start(data);
            }
        });

        //暂停
        downloadListAdapter.setOnItemChildClickListener(R.id.pause, new OnItemChildClickListener<DownloadData>() {
            @Override
            public void onItemChildClick(ViewHolder viewHolder, DownloadData data, int position) {
                DownloadMangerPool.getInstance(mContext).pause(data.getUrl());
            }
        });

        //继续
        downloadListAdapter.setOnItemChildClickListener(R.id.resume, new OnItemChildClickListener<DownloadData>() {
            @Override
            public void onItemChildClick(ViewHolder viewHolder, DownloadData data, int position) {
                DownloadMangerPool.getInstance(mContext).resume(data.getUrl());
            }
        });

        //取消
        downloadListAdapter.setOnItemChildClickListener(R.id.cancel, new OnItemChildClickListener<DownloadData>() {
            @Override
            public void onItemChildClick(ViewHolder viewHolder, DownloadData data, int position) {
                DownloadMangerPool.getInstance(mContext).cancel(data.getUrl());
            }
        });

        //重新开始
        downloadListAdapter.setOnItemChildClickListener(R.id.restart, new OnItemChildClickListener<DownloadData>() {
            @Override
            public void onItemChildClick(ViewHolder viewHolder, DownloadData data, int position) {
                DownloadMangerPool.getInstance(mContext).restart(data.getUrl());
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        downloadList.setLayoutManager(layoutManager);
        downloadList.setAdapter(downloadListAdapter);
    }
}
