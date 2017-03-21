package com.othershe.dutil.upload;

import com.othershe.dutil.Utils.Utils;
import com.othershe.dutil.callback.UploadCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {
    private RequestBody requestBody;
    private UploadCallback callback;
    private CountingSink countingSink;

    public ProgressRequestBody(RequestBody requestBody, UploadCallback callback) {
        this.requestBody = requestBody;
        this.callback = callback;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        try {
            countingSink = new CountingSink(sink);
            BufferedSink bufferedSink = Okio.buffer(countingSink);
            requestBody.writeTo(bufferedSink);
            bufferedSink.flush();
        } catch (Exception e) {
            if (callback != null) {
                callback.onError(e.toString());
            }
        }
    }

    protected class CountingSink extends ForwardingSink {

        private long currentLength = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            currentLength += byteCount;
            if (callback != null) {
                callback.onProgress(currentLength, contentLength(), Utils.getPercentage((int) currentLength, (int) contentLength()));
            }
        }

    }
}
