package com.othershe.dutil.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.othershe.dutil.callback.FileCallback;
import com.othershe.dutil.data.Ranges;
import com.othershe.dutil.net.OkHttpManager;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class DownloadManger {
    private FileCallback fileCallback;

    private String url;
    private String path;
    private String name;
    private Context context;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public DownloadManger(Context context, String url, String path, String name) {
        this.url = url;
        this.path = path;
        this.name = name;
        this.context = context;
    }

    public void execute(final FileCallback fileCallback) {
        this.fileCallback = fileCallback;

        OkHttpManager.getInstance().initRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                boolean isSupportRange = Utils.isSupportRange(response);
                if (!isSupportRange) {
                    return;
                }

                final File saveFile = new File(path, name);
                final File tempFile = new File(path, name + ".temp");

                Utils.deleteFile(saveFile, tempFile);

                if (!tempFile.exists()) {
                    FileUtil.prepareRangeFile(response, saveFile, tempFile);
                }

                final Ranges range = FileUtil.readDownloadRange(tempFile);

                for (int i = 0; i < 3; i++) {
                    final int finalI = i;
                    final int finalI1 = i;
                    OkHttpManager.getInstance().initRequest(url, range.start[i], range.end[i], new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            FileUtil.saveRangeFile(response, finalI, range.start[finalI1], range.end[finalI1], saveFile, tempFile);
                        }
                    });
                }
            }
        });

    }
}
