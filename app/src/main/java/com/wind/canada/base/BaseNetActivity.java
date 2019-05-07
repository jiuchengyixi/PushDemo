package com.wind.canada.base;

import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.TypeReference;
import com.common.android.adapter.RecyclerViewAdapter;
import com.common.android.adapter.SpacesItemDecoration;
import com.common.android.utils.CommonUtils;
import com.common.android.utils.HttpUtils;
import com.common.android.utils.Logger;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.wind.canada.R;

import java.util.Map;

/**
 * Created by yuanht on 16/6/8.
 */
public abstract class BaseNetActivity extends BaseActivity {
    protected XRecyclerView mXRecyclerView;
    protected RecyclerViewAdapter mAdapter;
    protected boolean mRefresh;
    public static final int ERROR_TYPE_NETWORK = 1;
    public static final int ERROR_TYPE_SERVER = 2;
    public static final int ERROR_TYPE_DATA = 3;
    protected int mStartIndex;

    /**
     * 初始化RecyclerView
     */
    protected void initRecyclerView(int viewId) {
        View view = findViewById(viewId);
        if (view == null || !(view instanceof XRecyclerView)) {
            return;
        }
        mXRecyclerView = (XRecyclerView) view;
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {

            @Override
            public void onRefresh() {
                //refresh data here
                onViewRefresh();
            }

            @Override
            public void onLoadMore() {
                //load more data here
                onViewLoadMore();
            }
        });
        mXRecyclerView.setLayoutManager(getLayoutManager());
        //设置item之间的间隔
        SpacesItemDecoration decoration = new SpacesItemDecoration(spacesItem());
        decoration.setSpaceType(getSpaceType());
        mXRecyclerView.addItemDecoration(decoration);
        mXRecyclerView.setItemAnimator(null);
//        getAdapter().setHasStableIds(true);
        mAdapter = getAdapter();
        mXRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                onItemClickListener(view, position);
            }
        });
        mXRecyclerView.setEmptyView(getEmptyView());
    }

    public int getSpaceType() {
        return SpacesItemDecoration.SPACE_TYPE_ALL;
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


    protected void requestHttpData(boolean isPost, final int requestCode, String url, Map<String, String> headers,
                                   Map params, final String content, final boolean showDialog, final TypeReference type, String filePath) {
        if (showDialog) {
            showLoadingDialog();
        }
        if ((!url.startsWith("http:") && !url.startsWith("https:"))) {
            url = mDBManager.getHost() + url;
        }

        HttpUtils.requestHttpData(this, isPost, requestCode, url, headers, params, content, filePath, new HttpUtils.OnResponseListener() {
            @Override
            public void onNetError(int requestCode) {
                if (showDialog) {
                    dismissLoadingDialog();
                    showNetworkError();
                    onResponseError(requestCode, ERROR_TYPE_NETWORK);
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
        onLoadFinish();
        if (showDialog) {
            dismissLoadingDialog();
        }
        if (CommonUtils.isEmpty(response)) {
            if (showDialog) {
                showDataError();
            }
            onResponseError(requestCode, ERROR_TYPE_DATA);
            return;
        }

        if (type == null) {
            onResponseSuccess(requestCode, response);
            return;
        }

        //对数据进行统一分析
        //onResponseSuccess(requestCode, response);
        Object obj = CommonUtils.parseJson(response, type);
        if (obj == null) {
            if (showDialog) {
                showDataError();
            }
            onResponseError(requestCode, ERROR_TYPE_DATA);
            return;
        }

        try {
            onResponseSuccess(requestCode, obj);
        } catch (Exception e) {
            Logger.e(e);
            e.printStackTrace();
            if (showDialog) {
                showDataError();
            }
        }
    }

    private void onRspError(int requestCode, boolean showDialog, Exception e) {
        onLoadFinish();
        if (showDialog) {
            dismissLoadingDialog();
            showDataError();
        }

        onResponseError(requestCode, ERROR_TYPE_SERVER);
    }


    protected void onResponseSuccess(int requestCode, Object response) {
    }

    protected void onResponseError(int requestCode, int errorType) {

    }

    protected void showNetworkError() {
        showToast(getString(R.string.network_not_available));
    }

    protected void showDataError() {
        showToast(getString(R.string.network_busy_or_data_error));
    }


    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(mContext);
    }

    public View getEmptyView () {
        return null;
    }


    /**
     * 行间距
     */
    public int spacesItem() {
        return (int) getResources().getDimension(R.dimen.divider_height);
    }

    /**
     * 刷新
     */
    public void onViewRefresh() {
        requestData(true, false);
    }

    /**
     * 加载更多
     */
    public void onViewLoadMore() {
        requestData(false, false);
    }


    public RecyclerViewAdapter getAdapter() {
        return null;
    }

    public void onItemClickListener(View view, int position) {
    }

    public void loadingMoreCompleted() {
        mXRecyclerView.loadMoreComplete();
    }

    public void refreshingCompleted() {
        mXRecyclerView.refreshComplete();
    }

    public void enablePullRefresh(boolean enable) {
        mXRecyclerView.setPullRefreshEnabled(enable);
    }

    public void enableLoadingMore(boolean enable) {
        mXRecyclerView.setLoadingMoreEnabled(enable);
    }

    protected void requestData(boolean refresh, boolean showDialog) {
        mRefresh = refresh;
        if (refresh) {
            mStartIndex = 0;
        } else {
            if (mAdapter.getDatas() != null) {
//                mStartIndex = mAdapter.getDatas().size() / 10;
                mStartIndex = mAdapter.getDatas().size();
            }
        }
    }

    protected void onLoadFinish() {
        if (mXRecyclerView != null) {
            if (mRefresh) {
                refreshingCompleted();
            } else {
                loadingMoreCompleted();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    // -----------------请求验证码 BEGIN-------------------------------------------
    protected CountDownTimer countDownTimer;

    protected void countDownTimer(int totalTime, final TextView view) {
        String sAgeFormat = getResources().getString(R.string.request_delay);
        view.setEnabled(false);
        view.setText(String.format(sAgeFormat, totalTime));
        countDownTimer = new CountDownTimer(totalTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String sAgeFormat = getResources().getString(R.string.request_delay);
                view.setText(String.format(sAgeFormat, (millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                view.setEnabled(true);
                view.setText(R.string.get_rand_code);
            }
        };
        countDownTimer.start();
    }

    protected void cancelCountTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    // -----------------请求验证码 END-------------------------------------------

}
