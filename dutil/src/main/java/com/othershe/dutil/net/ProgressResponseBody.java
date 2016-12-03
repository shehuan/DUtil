package com.othershe.dutil.net;

import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.callback.FileCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

    //自定义的文件传输回调
    private FileCallback fileCallback;

    //请求的响应体
    private ResponseBody responseBody;

    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, FileCallback fileCallback) {
        this.responseBody = responseBody;
        this.fileCallback = fileCallback;
    }

    @Override
    public MediaType contentType() {
        if (responseBody != null) {
            return responseBody.contentType();
        }
        return null;
    }

    @Override
    public long contentLength() {
        if (responseBody != null) {
            return responseBody.contentLength();
        }
        return 0;
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null && responseBody != null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    /**
     * 处理下载进度
     *
     * @param source
     * @return
     */
    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                if (fileCallback != null && fileCallback instanceof DownloadCallback) {
                    ((DownloadCallback) fileCallback).onProgress(totalBytesRead,
                            responseBody.contentLength(),
                            (int) (100 * totalBytesRead * 1.0 / responseBody.contentLength()));
                }
                return bytesRead;
            }
        };
    }
}
