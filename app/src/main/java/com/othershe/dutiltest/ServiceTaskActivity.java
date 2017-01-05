package com.othershe.dutiltest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ServiceTaskActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mStart;

    private DownloadService.DownloadBinder downloadBinder;

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

        mStart = (Button) findViewById(R.id.start_service);
        mStart.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                Intent intent = new Intent(this, DownloadService.class);
                intent.putExtra("url", "http://1.82.242.43/imtt.dd.qq.com/16891/A5D16F8C32981E2BBD86DF472E542757.apk");
                intent.putExtra("name", "快手.apk");
                intent.putExtra("path", Environment.getExternalStorageDirectory() + "/DUtil/");
                intent.putExtra("notifyId", (int) System.currentTimeMillis());
                bindService(intent, connection, BIND_AUTO_CREATE);

                break;
        }
    }
}
