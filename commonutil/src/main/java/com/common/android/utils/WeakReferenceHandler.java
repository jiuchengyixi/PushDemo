package com.common.android.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class WeakReferenceHandler extends Handler {
    protected WeakReference<Context> context;

    public WeakReferenceHandler(Context context) {
        this.context = new WeakReference<Context>(context);
    }

    @Override
    public void handleMessage(Message msg) {
        if (context != null && context.get() != null) {
            //do something
        }
    }
}
