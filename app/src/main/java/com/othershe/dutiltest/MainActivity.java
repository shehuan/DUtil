package com.othershe.dutiltest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.othershe.dutil.DUtil;
import com.othershe.dutil.callback.SimpleUploadCallback;
import com.othershe.dutil.callback.UploadCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST = 1;
    private Button singleTask, taskManage, serviceTask, upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        singleTask = (Button) findViewById(R.id.single_task);
        taskManage = (Button) findViewById(R.id.task_manage);
        serviceTask = (Button) findViewById(R.id.service_task);
        upload = (Button) findViewById(R.id.upload);

        upload.setOnClickListener(this);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "下载需要文件存储的权限!", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST);
                }
            } else {
                singleTask.setOnClickListener(this);
                taskManage.setOnClickListener(this);
                serviceTask.setOnClickListener(this);
            }
        } else {
            singleTask.setOnClickListener(this);
            taskManage.setOnClickListener(this);
            serviceTask.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.single_task:
                startActivity(new Intent(this, SingleTaskActivity.class));
                break;
            case R.id.task_manage:
                startActivity(new Intent(this, TaskManageActivity.class));
                break;
            case R.id.service_task:
                startActivity(new Intent(this, ServiceTaskActivity.class));
                break;
            case R.id.upload:
                testUpload();
                break;
        }
    }

    private void testUpload() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
        DUtil.initFormUpload()
                .url("http://192.168.1.xxxxx/OA-serviceapp-datahandler/datahandler/photo/upload")
                .addParam("vcode", "31d13464e3c44cb495e992d61fcc759d")
                .addParam("unique", "869271025990968")
                .addByte("file", "BeautyImage.jpg", bs.toByteArray())
                .bytesUploadBuild()
                .upload(new UploadCallback() {
                    @Override
                    public void onStart() {
                        Log.e("upload", "start");
                    }

                    @Override
                    public void onProgress(long currentSize, long totalSize, float progress) {
                        Log.e("upload", progress + "");
                    }

                    @Override
                    public void onFinish(String response) {
                        Log.e("upload", "finish###" + response);
                        Toast.makeText(MainActivity.this, "finish", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("upload", error);
                    }
                });

//        DUtil.initFormUpload()
//                .url("")
//                .addParam("vcode", "31d13464e3c44cb495e992d61fcc759d")
//                .addParam("unique", "869271025990968")
//                .addFile("file", "BeautyImage.jpg", new File(Environment.getExternalStorageDirectory() + "/DUtil/", "aaa.jpg"))
//                .fileUploadBuild()
//                .upload(new SimpleUploadCallback() {
//                    @Override
//                    public void onStart() {
//                        super.onStart();
//                    }
//
//                    @Override
//                    public void onFinish(String response) {
//                        super.onFinish(response);
//                    }
//                });

//        DUtil.initUpload()
//                .url("")
//                .addFile(new File(Environment.getExternalStorageDirectory() + "/DUtil/", "aaa.jpg"))
//                .build()
//                .upload(new SimpleUploadCallback() {
//                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                singleTask.setOnClickListener(this);
                taskManage.setOnClickListener(this);
                serviceTask.setOnClickListener(this);
            } else {
                Toast.makeText(this, "没有文件存储的权限!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
