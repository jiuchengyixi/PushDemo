package com.common.android.widget;

import android.app.Dialog;
import android.content.Context;

import com.common.android.R;


/**
 * Created by weifeng on 15/12/12.
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        this(context, R.style.dialog);

    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public LoadingDialog setDefaultView() {
        setContentView(R.layout.layout_loading_dialog);
        return this;
    }

    public LoadingDialog setView(int layoutResID) {
        setContentView(layoutResID);
        return this;
    }

}
