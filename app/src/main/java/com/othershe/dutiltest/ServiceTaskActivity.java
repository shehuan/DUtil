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

    private String url = "http://1.199.93.153/imtt.dd.qq.com/16891/5FE88135737E977CCCE1A4DAC9FAFFCB.apk";

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
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

        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                downloadBinder.startDownload(Environment.getExternalStorageDirectory() + "/DUtil/",
                        "高德地图.apk",
                        url,
                        (int) System.currentTimeMillis());
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

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
}
