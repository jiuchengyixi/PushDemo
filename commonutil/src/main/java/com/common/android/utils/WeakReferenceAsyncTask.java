package com.common.android.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class WeakReferenceAsyncTask extends AsyncTask<Object, Integer, Object> {
    protected WeakReference<Context> context;

    protected WeakReferenceAsyncTask(Context context) {
        this.context = new WeakReference<Context>(context);
    }

    @Override
    protected Object doInBackground(Object... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Object s) {
        super.onPostExecute(s);
        if (context != null && context.get() != null) {
            //do something
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
