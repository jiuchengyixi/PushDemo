package com.common.android.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

/**
 * 浮动弹窗
 * Created by yuanht on 16/7/3.
 */
public class PopupWindowUtil {

    private static PopupWindow popupWindow;

    /**
     * 底部显示弹窗
     */
    public static PopupWindow showPopupWindow(Context context, View view, View anchor) {
        return showPopupWindow(context, view, anchor, 0);
    }

        /**
         * 显示弹窗
         *
         * @param location 显示位置，0-下方，1-上方，2-左边，3-右边
         */
    public static PopupWindow showPopupWindow(Context context, View view, View anchor, int location) {
        popupWindow = new PopupWindow(view,  LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] loc = new int[2];
        anchor.getLocationOnScreen(loc);
        switch (location) {
            case 0:
                popupWindow.showAsDropDown(anchor);
                break;
            case 1:
                popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, loc[0], loc[1] - popupWindow.getHeight());
                break;
            case 2:
                popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, loc[0] - popupWindow.getWidth(), loc[1]);
                break;
            case 3:
                popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, loc[0] + view.getWidth(), loc[1]);
                break;
        }

        return popupWindow;
    }

    public static void dismissPopupWindow(){
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }
}
