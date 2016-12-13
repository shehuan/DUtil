package com.othershe.dutiltest;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.othershe.dutil.DUtil;
import com.othershe.dutil.Utils.Utils;
import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.download.DownloadManger;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /**
     * https://gdl.25pp.com/wm/8/24/com.yunchang.buliangren.uc_9981432_1802333051c8.apk
     * https://gdl.25pp.com/s/2/2/20160912113718937754_tcsdzz_1473311582981.apk
     * http://download.apk8.com/d2/soft/bohe.apk
     *
     * @param savedInstanceState
     */

    private TextView mSize;
    private TextView mProgress;
    private TextView mPause;
    private TextView mResume;

    DownloadManger manger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSize = (TextView) findViewById(R.id.size);
        mProgress = (TextView) findViewById(R.id.progress);
        mPause = (TextView) findViewById(R.id.pause);
        mResume = (TextView) findViewById(R.id.resume);

//
//        Intent intent = new Intent(this, DownloadService.class);
//        startService(intent);

        manger = DUtil.initDownload(this)
                .url("http://download.apk8.com/d2/soft/bohe.apk")
                .path(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath())
                .name("test1.apk")
                .thread(2)
                .build()
                .execute(new DownloadCallback() {

                    @Override
                    public void onStart(long currentSize, long totalSize, float progress) {
                        mSize.setText(Utils.formatSize(totalSize));
                        mProgress.setText(Utils.formatSize(currentSize) + " / " + Utils.formatSize(totalSize) + " #### " + progress + "%");
                    }

                    @Override
                    public void onProgress(long currentSize, long totalSize, float progress) {
                        mProgress.setText(Utils.formatSize(currentSize) + " / " + Utils.formatSize(totalSize) + " #### " + progress + "%");
                    }

                    @Override
                    public void onPause() {

                    }

                    @Override
                    public void onFinish(File file) {
                        Uri uri = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manger.pause();
            }
        });

        mResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manger.resume();
            }
        });
    }

    @Override
    protected void onDestroy() {
        manger.pause();
        super.onDestroy();
    }
}
