package com.common.android.adapter;

import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 抽象的PagerAdapter实现类,封装了内容为View的公共操作.
 * Author: msdx (645079761@qq.com)
 * Time: 14-8-18 下午2:34
 */
public abstract class AbstractViewPagerAdapter<T> extends PagerAdapter {
    protected List<T> mData;
    private SparseArray<View> mViews;

    public AbstractViewPagerAdapter(List<T> data) {
        mData = data;
        if (data != null) {
            mViews = new SparseArray<View>(data.size());
        } else {
            mViews = new SparseArray<>();
        }
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViews.get(position);
        if (view == null) {
            view = newView(position);
            mViews.put(position, view);
        }
        container.addView(view);
        return view;
    }

    public abstract View newView(int position);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void replaceData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }
}
