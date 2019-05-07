package com.android.common.httpclient.request;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.android.common.httpclient.OkHttpClientManager;
import com.android.common.httpclient.callback.ResultCallback;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by zhy on 15/11/6.
 */
public class OkHttpDownloadRequest extends OkHttpGetRequest {
    private static final int BUFFER_SIZE = 4 * 1024;
    private String tempFileDir;
    private String destFileDir;
    private String destFileName;
    private boolean resume;

    protected OkHttpDownloadRequest(
            String url, Object tag, Map<String, String> params, Map<String, String> headers,
            String tempFileDir, String destFileName, String destFileDir, boolean resume) {
        super(url, tag, params, headers);
        this.tempFileDir = tempFileDir;
        this.destFileName = destFileName;
        this.destFileDir = destFileDir;
        this.resume = resume;
    }

    @Override
    public void invokeAsyn(final ResultCallback callback) {
        prepareInvoked(callback);

        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                mOkHttpClientManager.sendFailResultCallback(request, e, callback);
            }

            @Override
            public void onResponse(Response response) {
                try {
                    String filePath = saveFile(response, callback);
                    OkHttpClientManager.getInstance().sendSuccessResultCallback(filePath, callback);
                } catch (IOException e) {
                    e.printStackTrace();
                    OkHttpClientManager.getInstance().sendStopResultCallback(e, callback);
                }
            }
        });

    }

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    @Override
    public <T> T invoke(Class<T> clazz) throws IOException {
        final Call call = mOkHttpClient.newCall(request);
        Response response = call.execute();
        return (T) saveFile(response, null);
    }

    public String saveFile(Response response, final ResultCallback callback) throws IOException {
        BufferedInputStream is = null;
        byte[] buf = new byte[BUFFER_SIZE];
        int len = 0;
        BufferedOutputStream fos = null;
        try {
            is = new BufferedInputStream(response.body().byteStream());
            File dir = new File(tempFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            long sum = 0;
            FileOutputStream fileOutputStream;
            if (resume) {
                sum = file.length();
                fileOutputStream = new FileOutputStream(file, true);
            } else {
                fileOutputStream = new FileOutputStream(file);
            }

            final long total = response.body().contentLength() + sum;

            fos = new BufferedOutputStream(fileOutputStream);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);

                if (callback != null) {
                    final long finalSum = sum;
                    mOkHttpClientManager.getDelivery().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.inProgress(total, finalSum, finalSum * 1.0f / total);
                        }
                    });
                }
            }
            fos.flush();

            //更新到下载目录
            File destDir = new File(destFileDir);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            file.renameTo(new File(destDir, destFileName));

//            return file.getAbsolutePath();
            return destDir.getAbsolutePath() + File.separator + destFileName;

        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }

        }
    }


}
