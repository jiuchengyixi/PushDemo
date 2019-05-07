package com.common.android.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.android.common.httpclient.callback.ResultCallback;
import com.android.common.httpclient.request.OkHttpRequest;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yuanht on 2018/7/21.
 * HTTP数据访问工具类
 */
public class HttpUtils {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void requestHttpData(Context context, final int requestCode, final String url, final String content, final OnResponseListener listener) {
        requestHttpData(context, true, requestCode, url, null, null, content, null, listener);
    }

    public static void requestHttpData(Context context, boolean isPost, final int requestCode, final String url, Map<String, String> headers,
                                       final Map params, final String content, String filePath, final OnResponseListener listener) {
        if (!NetWorkUtil.isNetworkAvailable(context)) {
            if (listener != null) {
                listener.onNetError(requestCode);
            }
            return;
        }

        if (params != null) {
            Iterator<Map.Entry> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                if (entry.getValue() != null) {
                    entry.setValue(String.valueOf(entry.getValue()));
//                } else {
//                    entry.setValue("");
//                }
                }
            }
        }

        Logger.d("REQUEST:url=" + url + ", params=" + params + ", content=" + content);
        OkHttpRequest.Builder builder = new OkHttpRequest.Builder().url(url).tag(context.toString());

        if (headers != null) {
            builder.headers(headers);
        }

        if (params != null) {
            builder.params(params);
        }
        if (content != null) {
            builder.content(content).mediaType(JSON);
        }

        if (!isPost) {
            builder.get(new ResultCallback<String>() {
                @Override
                public void onError(Request request, Exception e) {
                    Logger.e("RESPONSE:url=" + url + ", error=" + e);
                    if (listener != null) {
                        listener.onError(requestCode, e);
                    }

                }

                @Override
                public void onResponse(String response) {
                    Logger.i("RESPONSE:url=" + url + ", response=" + response);
                    if (listener != null) {
                        listener.onSuccess(requestCode, response);
                    }
                }
            });
        } else {
            ResultCallback<String> callback = new ResultCallback<String>() {
                @Override
                public void onError(Request request, Exception e) {
                    Logger.e("RESPONSE:url=" + url + ", error=" + e);
                    if (listener != null) {
                        listener.onError(requestCode, e);
                    }
                }

                @Override
                public void onResponse(String response) {
                    Logger.i("RESPONSE:url=" + url + ", response=" + response);
                    if (listener != null) {
                        listener.onSuccess(requestCode, response);
                    }
                }
            };
            if (filePath != null && !TextUtils.isEmpty(filePath)) {
                builder.files(new Pair("file", new File(filePath)));
                builder.upload(callback);
                return;
            }
            builder.post(callback);
        }
    }

    public static void downloadFile(Context context, final String url, Map<String, String> headers,
                                    final Map<String, String> params, String tempFileDir, String destFileDir,
                                    String destFileName, boolean resume, final OnDownloadResponseListener listener) {
        if (!NetWorkUtil.isNetworkAvailable(context)) {
            if (listener != null) {
                listener.onNetError(url);
            }
            return;
        }

        Logger.d("REQUEST:url=" + url + ", params=" + params);
        OkHttpRequest.Builder builder = new OkHttpRequest.Builder().url(url).tag(context.toString()).
                tempFileDir(tempFileDir).destFileDir(destFileDir).destFileName(destFileName);

        if (headers != null) {
            builder.headers(headers);
        }

        if (params != null) {
            builder.params(params);
        }

        ResultCallback<String> callback = new ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                Logger.e("RESPONSE:url=" + url + ", error=" + e);
                if (listener != null) {
                    listener.onError(url, e);
                }
            }

            @Override
            public void onResponse(String response) {
                Logger.i("RESPONSE:url=" + url + ", response=" + response);
                if (listener != null) {
                    listener.onSuccess(url, response);
                }
            }

            @Override
            public void inProgress(long total, long current, float progress) {
                if (listener != null) {
                    listener.inProgress(url, total, current, progress);
                }
            }
        };
        builder.download(callback, resume);
    }

    public interface OnResponseListener {
        void onNetError(int requestCode);

        void onError(int requestCode, Exception e);

        void onSuccess(int requestCode, String response);
    }

    public interface OnDownloadResponseListener {
        void onNetError(String url);

        void onError(String url, Exception e);

        void onSuccess(String url, String response);

        void inProgress(String url, long total, long current, float progress);
    }
}
