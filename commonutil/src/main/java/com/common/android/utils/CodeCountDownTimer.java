package com.common.android.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.common.android.R;


/**
 * 验证码倒计时工具类
 * Created by yuanht on 2017/9/5.
 */
public class CodeCountDownTimer {
    private CountDownTimer countDownTimer;
    private Context context;

    public CodeCountDownTimer(Context context) {
        this.context = context;
    }

    /**
     * 启动定时器
     */
    public void startCountDown(final TextView view, int totalTime) {
        String sAgeFormat = context.getResources().getString(R.string.send_code_delay);
        view.setEnabled(false);
        view.setText(String.format(sAgeFormat, totalTime));
        countDownTimer = new CountDownTimer(totalTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String sAgeFormat = context.getResources().getString(R.string.send_code_delay);
                view.setText(String.format(sAgeFormat, (millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                view.setEnabled(true);
                view.setText(R.string.send_code);
            }
        };
        countDownTimer.start();
    }

    /**
     * 取消计时器
     */
    public void cancelCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
