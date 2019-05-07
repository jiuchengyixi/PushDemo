package com.wind.canada.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.TypeReference;
import com.common.android.utils.CommonUtils;
import com.common.android.utils.HttpUtils;
import com.common.android.utils.Logger;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.wind.canada.R;
import com.wind.canada.db.DBManager;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by yuanht on 16/6/3.
 */
public abstract class BaseFragment extends Fragment {
    protected BaseActivity mActivity;
    protected DBManager mDBManager;
    private TextView mEmptyTextView;
    private ImageView mEmptyImageView;
    protected TextView mEmptyTextViewReconnect;
    private View mEmptyView;

    protected static final String TAB_INDEX = "tab_index";
    protected static final String TAB_DATA = "tab_data";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDBManager = DBManager.getInstance(getContext());
        return inflater.inflate(getLayoutResId(), null);
    }

    protected abstract int getLayoutResId();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //recyclerview 空数据
        mEmptyImageView = view.findViewById(R.id.img_none);
        mEmptyTextView = view.findViewById(R.id.tv_none);
        mEmptyTextViewReconnect = view.findViewById(R.id.tv_reconnect);
        mEmptyView = view.findViewById(R.id.layout_data_none);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            Fresco.getImagePipeline().clearMemoryCaches();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    protected void initData() {
    }

    public void refreshData() {
    }

    public void showLoadingDialog() {
        if (mActivity != null) {
            mActivity.showLoadingDialog();
        } else {
            Logger.e("mActivity = null");
        }
    }

    public void dismissLoadingDialog() {
        if (mActivity != null) {
            mActivity.dismissLoadingDialog();
        }
    }


    public void showToast(int resId) {
        if (mActivity != null) {
            mActivity.showToast(resId);
        }
    }

    public void showToast(String txt) {
        if (mActivity != null) {
            mActivity.showToast(txt);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    /**
     * 请求HTTP数据
     *
     * @param type //eg. new TypeReference<ResultModel<WindowList>>(){}
     */
    protected <T> void requestHttpData(boolean isPost, final int requestCode, String url, final Map<String, Object> params, final boolean showDialog, TypeReference type) {
//        String content = null;
//        if (data != null) {
//            content = CommonUtils.toJSON(data);
//        }

        requestHttpData(isPost, requestCode, url, null, params, null, showDialog, type, null);
    }

    /**
     * 请求HTTP数据，上传文件
     */
    protected <T> void requestHttpData(final int requestCode, String url, Map params, final boolean showDialog, TypeReference type, String filePath) {
        requestHttpData(true, requestCode, url, null, params, null, showDialog, type, filePath);
    }

    /**
     * 请求HTTP数据，url不需要刻意封装
     *
     * @param isPost true POST, false GET
     */
    protected void requestHttpData(boolean isPost, final int requestCode, String url, Map<String, String> headers,
                                   Map params, final String content, final boolean showDialog, final TypeReference type, String filePath) {
        if (showDialog) {
            showLoadingDialog();
        }
        if (!url.startsWith("http:") || !url.startsWith("https:")) {
            url = mDBManager.getHost() + url;
        }

        HttpUtils.requestHttpData(getActivity(), isPost, requestCode, url, headers, params, content, filePath, new HttpUtils.OnResponseListener() {
            @Override
            public void onNetError(int requestCode) {
                if (showDialog) {
                    dismissLoadingDialog();
                    showNetworkError();
                    onResponseError(requestCode, BaseNetActivity.ERROR_TYPE_NETWORK);
                }
                onLoadFinish();
            }

            @Override
            public void onError(int requestCode, Exception e) {
                onRspError(requestCode, showDialog, e);
            }

            @Override
            public void onSuccess(int requestCode, String response) {
                onRspSuccess(requestCode, response, showDialog, type);
            }
        });
    }

    private void onRspSuccess(int requestCode, String response, boolean showDialog, TypeReference type) {
        if (mActivity == null) {
            return;
        }
        onLoadFinish();
        if (showDialog) {
            dismissLoadingDialog();
        }
        if (response == null || CommonUtils.isEmpty(response.toString())) {
            if (showDialog) {
                showDataError();
            }
            onResponseError(requestCode, BaseNetActivity.ERROR_TYPE_DATA);
            return;
        }

        Object obj = CommonUtils.parseJson(response, type);
        if (obj == null) {
            if (showDialog) {
                showDataError();
            }
            onResponseError(requestCode, BaseNetActivity.ERROR_TYPE_DATA);
            return;
        }

        onResponseSuccess(requestCode, obj);
    }

    private void onRspError(int requestCode, boolean showDialog, Exception e) {
        if (mActivity == null) {
            return;
        }
        onLoadFinish();
        if (showDialog) {
            dismissLoadingDialog();
            showDataError();
        }

        onResponseError(requestCode, BaseNetActivity.ERROR_TYPE_SERVER);
    }


    protected void onResponseSuccess(int requestCode, Object response) {
    }

    protected void onResponseError(int requestCode, int errorType) {
    }

    protected <T> T parseResult(Object response, boolean showError) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            return ((BaseActivity) getActivity()).parseResult(response, showError);
        }
        return null;
    }

    protected boolean parseResultSuccess(Object response, boolean showError) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            return ((BaseActivity) getActivity()).parseResultSuccess(response, showError);
        }
        return false;
    }

    protected void showNetworkError() {
        showToast(getString(R.string.network_not_available));
    }

    protected void showDataError() {
        showToast(getString(R.string.network_busy_or_data_error));
    }

    protected void onLoadFinish() {
    }


    @Override
    public void startActivity(Intent intent) {
        if (mActivity != null) {
            mActivity.startActivity(intent);
        }
    }

    protected void startActivityWithSlide(Intent intent) {
        if (mActivity != null) {
            mActivity.startActivityWithSlide(intent);
        }
    }

    protected void requestFocus(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    protected boolean checkLoginAndStartActivity(boolean showToast) {
//        if (mDBManager.isLogin()) {
//            return true;
//        }
//
//        if (showToast) {
//            showToast(R.string.request_login);
//        }
//        startActivity(new Intent(getContext(), LoginActivity.class));
        return false;
    }

    /**
     * 带有点击重试的emptyview
     *
     * @param text
     * @param textReconnect
     * @param imgResId
     * @return
     */
    protected View getEmptyView(String text, String textReconnect, int imgResId) {
        if (mEmptyTextView != null) {
            mEmptyTextView.setText(text);
        }
        if (mEmptyImageView != null) {
            mEmptyImageView.setImageResource(imgResId);
        }
        if (mEmptyTextViewReconnect != null) {
            mEmptyTextViewReconnect.setText(textReconnect);
        }
        return mEmptyView;
    }

}
