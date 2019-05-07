package com.common.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.common.android.R;

/**
 * 底部浮出Dialog
 * */
public class ActionSheet {

    private ActionSheet() {

    }

    public static Dialog showSheet(Context context, View view) {
        Dialog dlg = new Dialog(context, R.style.ActionSheet);
        int cFullFillWidth = 10000;
        view.setMinimumWidth(cFullFillWidth);

        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(view);

        return dlg;
    }

}
