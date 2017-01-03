package com.othershe.dutiltest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button singleTask, taskManage, serviceTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        singleTask = (Button) findViewById(R.id.single_task);
        taskManage = (Button) findViewById(R.id.task_manage);
        serviceTask = (Button) findViewById(R.id.service_task);

        singleTask.setOnClickListener(this);
        taskManage.setOnClickListener(this);
        serviceTask.setOnClickListener(this);
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
        }
    }
}
