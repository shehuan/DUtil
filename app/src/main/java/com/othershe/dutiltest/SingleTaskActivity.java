package com.othershe.dutiltest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.othershe.dutil.DUtil;
import com.othershe.dutil.utils.Utils;
import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.download.DownloadManger;

import java.io.File;

public class SingleTaskActivity extends AppCompatActivity {

    /**
     * http://1.198.5.23/imtt.dd.qq.com/16891/B8723A0DB2F2702C04D801D9FD19822C.apk //阴阳师
     * http://1.82.215.170/imtt.dd.qq.com/16891/85B6221DE84C466310575D9FBCA453A8.apk  //天天酷跑
     * http://1.198.5.22/imtt.dd.qq.com/16891/8EEC7D8996760973B5CEA15ECA1700E3.apk  //消消乐
     */

    private TextView mTip;
    private TextView mProgress;
    private TextView mPause;
    private TextView mResume;
    private TextView mCancel;
    private TextView mRestart;
    private ProgressBar progressBar;

    private Context mContext;

    private String url;

    private DownloadManger downloadManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_task);

        mContext = this;

        mTip = (TextView) findViewById(R.id.tip);
        mProgress = (TextView) findViewById(R.id.progress);
        mPause = (TextView) findViewById(R.id.pause);
        mResume = (TextView) findViewById(R.id.resume);
        mCancel = (TextView) findViewById(R.id.cancel);
        mRestart = (TextView) findViewById(R.id.restart);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()

        final String name = "消消乐";
        url = "http://1.198.5.22/imtt.dd.qq.com/16891/8EEC7D8996760973B5CEA15ECA1700E3.apk";

        downloadManger = DUtil.init(mContext)
                .url(url)
                .path(Environment.getExternalStorageDirectory() + "/DUtil/")
                .name(name + ".apk")
                .childTaskCount(3)
                .build()
                .start(new DownloadCallback() {

                    @Override
                    public void onStart(long currentSize, long totalSize, float progress) {
                        mTip.setText(name + "：准备下载中...");
                        progressBar.setProgress((int) progress);
                        mProgress.setText(Utils.formatSize(currentSize) + " / " + Utils.formatSize(totalSize) + "--------" + progress + "%");
                    }

                    @Override
                    public void onProgress(long currentSize, long totalSize, float progress) {
                        mTip.setText(name + "：下载中...");
                        progressBar.setProgress((int) progress);
                        mProgress.setText(Utils.formatSize(currentSize) + " / " + Utils.formatSize(totalSize) + "--------" + progress + "%");
                    }

                    @Override
                    public void onPause() {
                        mTip.setText(name + "：暂停中...");
                    }

                    @Override
                    public void onCancel() {
                        mTip.setText(name + "：已取消...");
                    }

                    @Override
                    public void onFinish(File file) {
                        mTip.setText(name + "：下载完成...");
                        Uri uri = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        startActivity(intent);
                    }

                    @Override
                    public void onWait() {

                    }

                    @Override
                    public void onError(String error) {
                        mTip.setText(name + "：下载出错...");
                    }
                });

        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManger.pause(url);
            }
        });

        mResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManger.resume(url);
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManger.cancel(url);
            }
        });

        mRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManger.restart(url);
            }
        });
    }

    @Override
    protected void onDestroy() {
        downloadManger.destroy(url);
        super.onDestroy();
    }
}
