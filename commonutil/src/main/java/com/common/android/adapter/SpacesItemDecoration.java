package com.common.android.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public static final int SPACE_TYPE_VERTICAL = 1;
    public static final int SPACE_TYPE_HORIZENTAL = 2;
    public static final int SPACE_TYPE_ALL = 3;

    private int spaceType = SPACE_TYPE_ALL;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }
    public SpacesItemDecoration(int space, int spaceType) {
        this.space = space;
        this.spaceType = spaceType;
    }

    public void setSpaceType (int spaceType) {
        this.spaceType = spaceType;
    }

    /**
     * 单列：自动设定
     * 单行：ListView左侧间距手动设定
     * 多行多列：ListView左侧间距手动设定
     * */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        outRect.left = 0;
//        outRect.right = space;
//        outRect.bottom = space;
//        if (parent.getChildAdapterPosition(view) == 0) {
//            outRect.top = space;
//        }

        if (parent.getPaddingLeft() != space) {
            switch (spaceType) {
                case SPACE_TYPE_VERTICAL:
//                    parent.setPadding(0, space, 0, space);
                    outRect.top = 0;
                    outRect.bottom = space;
                    outRect.left = 0;
                    outRect.right = 0;
                    break;
                case SPACE_TYPE_HORIZENTAL:
                    outRect.top = space;
                    outRect.bottom = space;
                    outRect.left = 0;
                    outRect.right = space;
//                    parent.setPadding(space, 0, space, 0);
                    break;
                case SPACE_TYPE_ALL:
                    outRect.top = 0;
                    outRect.bottom = space;
                    outRect.left = 0;
                    outRect.right = space;

//                    parent.setPadding(space, space, space, space);
                    break;
            }

            parent.setClipToPadding(false);
        }

//        outRect.top = 0;
//        outRect.bottom = space;
//        outRect.left = space;
//        outRect.right = space;
    }
}
