package com.wind.canada.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.common.android.adapter.RecyclerViewAdapter;
import com.common.android.adapter.SpacesItemDecoration;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.wind.canada.R;

public abstract class BaseListFragment extends BaseFragment {
    protected XRecyclerView mRecyclerView;
    protected boolean mRefresh;
    protected RecyclerViewAdapter mAdapter;
    protected int mStartIndex;
    protected LinearLayout mLayoutDataNone;
    protected AnimatorSet animatorShow;
    private AnimatorSet animatorHide;
    protected ObjectAnimator scaleYShow, alphaShow;

    @Override
    protected int getLayoutResId() {
        return R.layout.layout_recyclerview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.id_recycler_view);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        mLayoutDataNone = view.findViewById(R.id.layout_data_none);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {

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
        mRecyclerView.setLayoutManager(getLayoutManager());
        //设置item之间的间隔
        SpacesItemDecoration decoration = new SpacesItemDecoration(spacesItem());
        decoration.setSpaceType(getSpaceType());
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setItemAnimator(null);
//        getAdapter().setHasStableIds(true);
        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                onItemClickListener(view, position);
            }
        });
        mRecyclerView.setEmptyView(getEmptyView());
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(mActivity);
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

    /**
     * 行间距
     */
    public int spacesItem() {
        return (int) getResources().getDimension(R.dimen.divider_height);
    }

    public int getSpaceType() {
        return SpacesItemDecoration.SPACE_TYPE_ALL;
    }

    public abstract RecyclerViewAdapter getAdapter();

    public void onItemClickListener(View view, int position) {
    }

    public void loadingMoreCompleted() {
        mRecyclerView.loadMoreComplete();
    }

    public void refreshingCompleted() {
        mRecyclerView.refreshComplete();
    }

    public void enablePullRefresh(boolean enable) {
        mRecyclerView.setPullRefreshEnabled(enable);
    }

    public void enableLoadingMore(boolean enable) {
        mRecyclerView.setLoadingMoreEnabled(enable);
    }

    public View getEmptyView() {
        return null;
    }

    protected void onLoadFinish() {
        if (mRecyclerView != null) {
            if (mRefresh) {
                refreshingCompleted();
            } else {
                loadingMoreCompleted();
            }
        }
    }

    protected void initRefreshToastAnim(final View refreshView) {
        animatorShow = new AnimatorSet();//开始动画
        scaleYShow = ObjectAnimator.ofFloat(refreshView, "translationY", -200, 0f);
        alphaShow = ObjectAnimator.ofFloat(refreshView, "alpha", 0, 1f);

        animatorShow.setDuration(1000);
        animatorShow.setInterpolator(new DecelerateInterpolator());
        animatorShow.play(scaleYShow).with(alphaShow);//两个动画同时开始
//        animatorShow.start();

        animatorHide = new AnimatorSet();
//        ObjectAnimator scaleY = ObjectAnimator.ofFloat(refreshView, "translationY", 0, -200);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(refreshView, "alpha", 1f, 0);

        animatorHide.setDuration(500);
        animatorHide.setInterpolator(new DecelerateInterpolator());
        animatorHide.play(alpha);

        animatorShow.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //refreshView.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animatorHide.start();
                    }
                }, 2000);
            }
        });
    }
}
