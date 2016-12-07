package com.othershe.dutiltest;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.othershe.dutil.DUtil;
import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.callback.FileCallback;
import com.othershe.dutil.callback.SimpleDownloadCallback;
import com.othershe.dutil.download.DownloadManger;
import com.othershe.dutil.service.DownloadService;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        Intent intent = new Intent(this, DownloadService.class);
//        startService(intent);

        DUtil.initDownload(MainActivity.this)
                .url("http://download.apk8.com/d2/soft/bohe.apk")
                .path(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath())
                .name("test001.apk")
                .build()
                .execute(new DownloadCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onProgress(long currentSize, long totalSize, int progress) {
                        Log.e(TAG, "onProgress: " + progress);
                    }

                    @Override
                    public void onPause() {

                    }

                    @Override
                    public void onFinish(File file) {

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }
}
