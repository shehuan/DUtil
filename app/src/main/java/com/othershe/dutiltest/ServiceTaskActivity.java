package com.othershe.dutiltest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ServiceTaskActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mStart;
    private TextView mPause;
    private TextView mResume;
    private TextView mCancel;
    private TextView mRestart;

    private DownloadService.DownloadBinder downloadBinder;

    private String url = "http://1.82.242.43/imtt.dd.qq.com/16891/A5D16F8C32981E2BBD86DF472E542757.apk";

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
            downloadBinder.startDownload(Environment.getExternalStorageDirectory() + "/DUtil/",
                    "快手.apk",
                    url,
                    (int) System.currentTimeMillis());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_task);

        mStart = (TextView) findViewById(R.id.start);
        mPause = (TextView) findViewById(R.id.pause);
        mResume = (TextView) findViewById(R.id.resume);
        mCancel = (TextView) findViewById(R.id.cancel);
        mRestart = (TextView) findViewById(R.id.restart);
        mStart.setOnClickListener(this);
        mPause.setOnClickListener(this);
        mResume.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mRestart.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                Intent intent = new Intent(this, DownloadService.class);
                bindService(intent, connection, BIND_AUTO_CREATE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("sssssss", downloadBinder.getProgress(url) + "%");
                    }
                }, 3000);
                break;
            case R.id.pause:
                downloadBinder.pauseDownload(url);
                break;
            case R.id.resume:
                downloadBinder.resumeDownload(url);
                break;
            case R.id.cancel:
                downloadBinder.cancelDownload(url);
                break;
            case R.id.restart:
                downloadBinder.restartDownload(url);
                break;
        }
    }
}
