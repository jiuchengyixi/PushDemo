package com.common.android.utils.screen;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * This is the class that is responsible for adding the filter on the screen: It
 * works as a service, so that the view persists across all activities.
 */
public class ScreenService extends Service {
    private LinearLayout mView;
    private SharedMemory shared;

    public static final String ACTION_REMOVE_FILTER_VIEW = "com.android.ACTION_REMOVE_FILTER_VIEW";

    public static int STATE;

    public static final int INACTIVE = 0;
    public static final int ACTIVE = 0;

    static {
        STATE = INACTIVE;
    }

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        shared = new SharedMemory(this);
        mView = new LinearLayout(this);
        mView.setBackgroundColor(shared.getColor());
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (wm != null) {
            wm.addView(mView, params);
        }
        STATE = ACTIVE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (wm != null) {
                wm.removeView(mView);
            }
            STATE = INACTIVE;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mView != null) {
            mView.setBackgroundColor(shared.getColor());
        }
        if (intent != null) {
            if (ACTION_REMOVE_FILTER_VIEW.equals(intent.getAction())) {
                if (mView != null) {
                    WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                    if (wm != null) {
                        wm.removeView(mView);
                    }

                    STATE = INACTIVE;
                    mView = null;
                }
                this.stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
