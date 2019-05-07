package com.common.android.utils.maputil;

import android.graphics.drawable.Drawable;

/**
 * Created by yuanht on 2017/12/20.
 */

public class IconListItem {
    private final String mTitle;
    private final Drawable mResource;
    private final Object mAttach;

    public IconListItem(String title, Drawable resource, Object attach) {
        mResource = resource;
        mTitle = title;
        mAttach = attach;
    }

    public String getTitle() {
        return mTitle;
    }

    public Drawable getResource() {
        return mResource;
    }

    public Object getAttach() {
        return mAttach;
    }
}
