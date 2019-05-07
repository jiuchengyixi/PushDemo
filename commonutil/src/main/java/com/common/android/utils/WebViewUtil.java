package com.common.android.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.File;

/**
 * Created by weifeng on 15/7/13.
 */
public class WebViewUtil {
    private static ValueCallback<Uri> mUploadMessage;
    private static ValueCallback<Uri[]> mUploadMessage5;
    public static final int FILE_CHOOSER_RESULT_CODE = 0x5173;
    public static final int FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5 = 0x5174;

    public static void initWebView(WebView webView, String cacheDirPath) {
        initWebView(webView, cacheDirPath, null, null);
    }

    public static void initWebView(WebView webView, String cacheDirPath, WebViewClient webViewClient, WebChromeClient webChromeClient) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCacheMaxSize(1024 * 1024 * 8);
        if (cacheDirPath == null) {
            cacheDirPath = webView.getContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        }

        settings.setAppCachePath(cacheDirPath);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        settings.setDatabaseEnabled(true);
        String dbPath = webView.getContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabasePath(dbPath);
//        webView.requestFocus();

//        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        //使用缓存
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //无网络情况下使用缓存数据
        //settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //默认不使用缓存！
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        settings.setDefaultTextEncodingName("UTF-8"); // 非常关键，否则设置了WebChromeClient后会出现乱码
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //取消滚动条白边效果


        if (webViewClient != null) {
            webView.setWebViewClient(webViewClient);
        }
        if (webChromeClient != null) {
            webView.setWebChromeClient(webChromeClient);
        }
    }

    public static void initWebViewSimple(WebView webView) {
        initWebViewSimple(webView, null, null);
    }

    public static void initWebViewSimple(WebView webView, String cacheDirPath, WebViewClient webViewClient) {
        if (webViewClient == null) {
            webViewClient = new BaseWebViewClient();
        }
        initWebView(webView, cacheDirPath, webViewClient, new BaseWebChromeClient());
    }

    /**
     * 带文件选择器的 WebChromeClient
     */
    public static WebChromeClient getChromeClientWithFileOpen(final Activity activity, final TextView titleView, final OnFileSelectListener listener) {
        return new BaseWebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (titleView != null) {
                    titleView.setText(title);
                }
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                this.openFileChooser(uploadMsg, "*/*");
            }

            // For Android >= 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                this.openFileChooser(uploadMsg, acceptType, null);
            }

            // For Android >= 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                if (acceptType == null || acceptType.length() == 0) {
                    acceptType = "*/*";
                }
                i.setType(acceptType);
                activity.startActivityForResult(Intent.createChooser(i, "File Browser"), FILE_CHOOSER_RESULT_CODE);
            }

            // For Lollipop 5.0+ Devices
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                if (mUploadMessage5 != null) {
                    mUploadMessage5.onReceiveValue(null);
                    mUploadMessage5 = null;
                }
                mUploadMessage5 = filePathCallback;

                if (listener == null) {
                    Intent intent = fileChooserParams.createIntent();
                    try {
                        activity.startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5);
                    } catch (ActivityNotFoundException e) {
                        mUploadMessage5 = null;
                        return false;
                    }
                } else {
                    listener.onFileSelect();
                }
                return true;
            }
        };
    }

    public interface OnFileSelectListener {
        void onFileSelect();
    }

    @SuppressLint("NewApi")
    public static void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (requestCode == FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5) {
            if (null == mUploadMessage5) {
                return;
            }
            mUploadMessage5.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            mUploadMessage5 = null;

            //通用方法
//            if (intent != null) {
//                // 这里是针对从文件中选图片的处理
//                Uri[] results;
//                Uri uriData = intent.getData();
//                if (uriData != null) {
//                    results = new Uri[]{uriData};
//                    for (Uri uri : results) {
//                        Logger.d("系统返回URI：" + uri.toString());
//                    }
//                    mUploadMessage5.onReceiveValue(results);
//                } else {
//                    mUploadMessage5.onReceiveValue(null);
//                }
//            }
        }
    }

    public static void onImageSelected(String imagePath) {
        //图片本地选中的
        if (null == mUploadMessage5) {
            return;
        }
        if (imagePath != null) {
            mUploadMessage5.onReceiveValue(new Uri[]{Uri.fromFile(new File(imagePath))});
            mUploadMessage5 = null;
        }
    }

    /**
     * 带标题更新的 WebChromeClient
     */
    public static WebChromeClient getBaseWebChromeClientWithTitle(final TextView titleView) {
        return new BaseWebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                titleView.setText(title);
            }
        };
    }

    public static class BaseWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Logger.d("标题改变为：" + title);
        }
    }

    public static class BaseWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //todo 一些特殊地址的处理
//            if(url.trim().startsWith("tel")){//特殊情况tel，调用系统的拨号软件拨号【<a href="tel:1111111111">1111111111</a>】
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }else{
//                String port = url.substring(url.lastIndexOf(":")+1,url.lastIndexOf("/"));//尝试要拦截的视频通讯url格式(808端口)：【http://xxxx:808/?roomName】
//                if(port.equals("808")){//特殊情况【若打开的链接是视频通讯地址格式则调用系统浏览器打开】
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    startActivity(i);
//                }else {//其它非特殊情况全部放行
//                    view.loadUrl(url);
//                }
//            }


            //使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
            Logger.d(url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
//            if (view.canGoBack()) {
//                view.goBack();
//            }
        }

        @Override
        public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
            handler.proceed();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }

    public static void loadData(WebView webView, String content) {
        webView.loadData(content, "text/html; charset=UTF-8", null);
    }

    public static void loadDataWithMobileHeader(WebView webView, String content) {
        String sb = "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                getWebWiewImageStyle() +
                "</head><body>" +
                content +
                "</body></html>";
        loadData(webView, sb);
    }

    public static String getWebWiewImageStyle() {
        return "<style>img {max-width: 99%;text-align:center;} body {overflow-y: scroll !important;}</style>";
    }

    public static String resetTencentVideoUrl(String videoContent, int height) {
        if (CommonUtils.isEmpty(videoContent)) {
            return videoContent;
        }
        videoContent = videoContent.replace("\"", "'");
        int index = videoContent.indexOf("src='");
        if (index > 0) {
            String url = videoContent.substring(index + 5, videoContent.length());
            index = url.indexOf("'");
            url = url.substring(0, index);
            if (videoContent.contains("<iframe")) {
                videoContent = "<iframe frameborder='0' width='100%' height='100%' src='" + url + "' allowFullScreen='true'></iframe>";
            } else if (videoContent.contains("<embed")) {
                videoContent = "<embed src='" + url + "' allowFullScreen='true' quality='high' width='100%' height='100%' align='middle' allowScriptAccess='always' type='application/x-shockwave-flash'></embed>";
            }
        }
        Logger.d(videoContent);
        return videoContent;

    }

    public static String getAssertUrl(String htmlPath) {
        return "file:///android_asset/" + htmlPath;
    }

    public static String getSdCardUrl(String htmlPath) {
        return "file:///" + htmlPath;
    }

}
