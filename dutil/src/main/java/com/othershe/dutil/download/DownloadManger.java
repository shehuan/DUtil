package com.othershe.dutil.download;

import android.content.Context;

import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.callback.FileCallback;
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
    private long fileSize;

    public DownloadManger(Context context, String url, String path, String name) {
        this.url = url;
        this.path = path;
        this.name = name;
        this.context = context;
    }

    public void execute(final FileCallback fileCallback) {
        this.fileCallback = fileCallback;

//        Intent intent = new Intent(context, DownloadService.class);
//        intent.putExtra("url", url);
//        intent.putExtra("path", path);
//        intent.putExtra("name", name);
//        context.startService(intent);

        OkHttpManager.getInstance().initRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (fileCallback instanceof DownloadCallback) {
//                    ((DownloadCallback) fileCallback).onError(e.toString());
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
//                FileUtil.saveFile(response, 0, path, name);

                boolean isSupportRange = Utils.isSupportRange(response);
                if (!isSupportRange) {
                    return;
                }

                final File saveFile = new File(path, name);
                final File tempFile = new File(path, name + ".temp");

                Utils.deleteFile(saveFile, tempFile);

                if (!tempFile.exists()) {
                    FileUtil.prepare(response, saveFile, tempFile);
                }

                final Range range = FileUtil.readDownloadRange(tempFile);

                for (int i = 0; i < 1; i++) {
                    final int finalI = i;
                    final int finalI1 = i;
                    OkHttpManager.getInstance().initRequest(url, range.start[i], range.end[i], new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            FileUtil.saveFile(response, finalI, range.start[finalI1], range.end[finalI1], saveFile, tempFile);
                        }
                    });
                }
            }
        });

    }
}
