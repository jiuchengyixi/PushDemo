package com.common.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.common.android.utils.CommonUtils;

import java.util.List;

/**
 * Created by yuanht on 2017/10/9.
 */

public class SingleListDialog {
    private Dialog mDialog;
    private List<String> list;
    private Context context;
    private OnItemSelectListener listener;

    public SingleListDialog(Context context, List<String> list, OnItemSelectListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    public void show() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (CommonUtils.isEmpty(list)) {
            return;
        }

        mDialog = new AlertDialog.Builder(context)
                .setItems(list.toArray(new String[list.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog.dismiss();
                        if (listener != null) {
                            listener.onItemSelect(which);
                        }
                    }
                }).create();
        mDialog.show();
    }

    public interface OnItemSelectListener {
        void onItemSelect(int position);
    }
}
