package com.othershe.dutiltest;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.othershe.dutil.DUtil;
import com.othershe.dutil.callback.DownloadCallback;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /**
     * https://gdl.25pp.com/wm/8/24/com.yunchang.buliangren.uc_9981432_1802333051c8.apk
     * https://gdl.25pp.com/s/2/2/20160912113718937754_tcsdzz_1473311582981.apk
     * http://download.apk8.com/d2/soft/bohe.apk
     * @param savedInstanceState
     */

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
                .name("test1.apk")
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

        Log.e(TAG, "onCreate: " + new Date().getTime());
    }
}