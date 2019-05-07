package com.common.android.utils.screen;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;

public class ScreenFilterUtils {
	/**
	 * 更新背景颜色。参数取值范围 0-255
	 * */
	public static void updateWindowColor(Activity activity, int alpha, int red,
			int green, int blue) {
		int color = SharedMemory.getColor(alpha, red, green, blue);
		ColorDrawable cd = new ColorDrawable(color);
		activity.getWindow().setBackgroundDrawable(cd);
	}

	public static void setWindowOn(Activity activity) {
		updateWindowColor(activity, 33, 0, 0, 0);
	}

	public static void setWindowOff(Activity activity) {
		updateWindowColor(activity, 250, 0, 0, 0);
	}
}
