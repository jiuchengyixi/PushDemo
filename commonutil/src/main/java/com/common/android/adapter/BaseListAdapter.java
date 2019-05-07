package com.common.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 对ViewHolder的封装，以及更方便的控制ListView滑动过程中不加载图片
 *
 * @param <T>
 * @author kymjs (https://www.kymjs.com/)
 */
public abstract class BaseListAdapter<T> extends BaseAdapter implements
        AbsListView.OnScrollListener {

    protected List<T> mDatas;
    protected final int mItemLayoutId;
    protected AbsListView mList;
    protected boolean isScrolling;
    protected Context mCxt;
    protected LayoutInflater mInflater;

    private AbsListView.OnScrollListener listener;

    public BaseListAdapter(AbsListView view, List<T> mDatas, int itemLayoutId) {
        if (mDatas == null) {
            mDatas = new ArrayList<T>(0);
        }
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
        this.mList = view;
        mCxt = view.getContext();
        mInflater = LayoutInflater.from(mCxt);
        mList.setOnScrollListener(this);
    }

    public BaseListAdapter(AbsListView view, int itemLayoutId) {
        this(view, null, itemLayoutId);
    }

    public void refresh(List<T> datas) {
        if (datas == null) {
            datas = new ArrayList<>(0);
        }
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    public void addOnScrollListener(AbsListView.OnScrollListener l) {
        this.listener = l;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AdapterHolder viewHolder = getViewHolder(position, convertView,
                parent);
        convert(viewHolder, getItem(position), isScrolling, position);
        return viewHolder.getConvertView();

    }

    private AdapterHolder getViewHolder(int position, View convertView,
                                        ViewGroup parent) {
        return AdapterHolder.get(convertView, parent, mItemLayoutId, position);
    }

    public abstract void convert(AdapterHolder holder, T item,
                                 boolean isScrolling);

    public void convert(AdapterHolder holder, T item, boolean isScrolling,
                        int position) {
        convert(holder, getItem(position), isScrolling);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 设置是否滚动的状态
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            isScrolling = false;
            this.notifyDataSetChanged();
        } else {
            isScrolling = true;
        }
        if (listener != null) {
            listener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (listener != null) {
            listener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void add(T obj) {
        mDatas.add(obj);
        notifyDataSetChanged();
    }

    public void addAll(List<T> objs) {
        mDatas.addAll(objs);
        notifyDataSetChanged();
    }
}
