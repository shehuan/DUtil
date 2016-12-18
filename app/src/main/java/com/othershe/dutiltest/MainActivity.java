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
     * http://1.198.5.23/imtt.dd.qq.com/16891/B8723A0DB2F2702C04D801D9FD19822C.apk //阴阳师
     * http://1.82.215.170/imtt.dd.qq.com/16891/85B6221DE84C466310575D9FBCA453A8.apk  //ttkp
     * http://1.198.5.22/imtt.dd.qq.com/16891/8EEC7D8996760973B5CEA15ECA1700E3.apk  //xxl
     * http://download.apk8.com/d2/soft/bohe.apk
     *
     * @param savedInstanceState
     */

    private TextView mTip;
    private TextView mProgress;
    private TextView mPause;
    private TextView mResume;
    private TextView mCancel;
    private TextView mRestart;

    DownloadManger manger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTip = (TextView) findViewById(R.id.tip);
        mProgress = (TextView) findViewById(R.id.progress);
        mPause = (TextView) findViewById(R.id.pause);
        mResume = (TextView) findViewById(R.id.resume);
        mCancel = (TextView) findViewById(R.id.cancel);
        mRestart = (TextView) findViewById(R.id.restart);

//
//        Intent intent = new Intent(this, DownloadService.class);
//        startService(intent);

        manger = DUtil.initDownload(this)
                .url("http://1.82.215.170/imtt.dd.qq.com/16891/85B6221DE84C466310575D9FBCA453A8.apk")
                .path(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath())
                .name("test1.apk")
                .thread(2)
                .build()
                .execute(new DownloadCallback() {

                    @Override
                    public void onStart(long currentSize, long totalSize, float progress) {
                        mTip.setText("准备中...");
                        mProgress.setText(Utils.formatSize(currentSize) + " / " + Utils.formatSize(totalSize) + " #### " + progress + "%");
                    }

                    @Override
                    public void onProgress(long currentSize, long totalSize, float progress) {
                        mTip.setText("下载中...");
                        mProgress.setText(Utils.formatSize(currentSize) + " / " + Utils.formatSize(totalSize) + " #### " + progress + "%");
                    }

                    @Override
                    public void onPause() {
                        mTip.setText("暂停中...");
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

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manger.cancel();
            }
        });

        mRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manger.restart();
            }
        });
    }

    @Override
    protected void onDestroy() {
        manger.destroy();
        super.onDestroy();
    }
}
