package com.common.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.android.R;

/**
 * 底部浮出的确认框
 * Created by yuanht on 16/6/22.
 */
public class ConfirmDialog implements View.OnClickListener {
    private String title;
    private ConfirmListener confirmListener;
    private Context context;
    private Dialog dialog;

    public ConfirmDialog(Context context, String title, ConfirmListener confirmListener) {
        this.context = context;
        this.title = title;
        this.confirmListener = confirmListener;
    }

    public void show () {
        View view = View.inflate(context, R.layout.layout_bottom_confirm_dialog, null);
        ((TextView)view.findViewById(R.id.tv_title)).setText(title);
        view.findViewById(R.id.tv_ok).setOnClickListener(this);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        dialog = ActionSheet.showSheet(context, view);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_ok) {
            dialog.dismiss();
            if (confirmListener != null) {
                confirmListener.onConfirmClick();
            }

        } else if (i == R.id.tv_cancel) {
            dialog.dismiss();

        }
    }

    public interface ConfirmListener {
        void onConfirmClick();
    }
}
