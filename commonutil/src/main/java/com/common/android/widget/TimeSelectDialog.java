package com.common.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import com.common.android.R;
import com.common.android.utils.Logger;
import com.common.android.widget.wheel.OnWheelChangedListener;
import com.common.android.widget.wheel.WheelView;
import com.common.android.widget.wheel.adapters.ArrayWheelAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 日期选择控件
 * Created by yuanht on 16/6/20.
 */
public class TimeSelectDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private OnTimeSelectedListener listener;
    // 自定义控件年 月 日 时 分
    private WheelView wv_year, wv_month, wv_day, wv_hour, wv_minute;
    // 初始化时间数据
    private static String[] yearContent = null, monthContent = null, dayContent = null,
            hourContent = null, minuteContent = null;

    private boolean withTime = true;
    private TimeModel mSelectedTime;
    private boolean forBankValidDate;

    public TimeSelectDialog(Context context, OnTimeSelectedListener listener, boolean withTime, TimeModel time) {
        super(context, R.style.dialog);
        this.context = context;
        this.listener = listener;
        this.withTime = withTime;
        this.mSelectedTime = time;
    }

    /**
     * 信用卡有效期选择
     */
    public TimeSelectDialog(Context context, OnTimeSelectedListener listener, TimeModel time) {
        super(context, R.style.dialog);
        this.context = context;
        this.listener = listener;
        forBankValidDate = true;
        withTime = false;
        this.mSelectedTime = time;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_time_select_dialog);

        // 确定
        TextView tv_choose_time_ok = (TextView) findViewById(R.id.tv_choose_time_ok);
        tv_choose_time_ok.setOnClickListener(this);
        // 取消
        TextView tv_choose_time_cancel = (TextView) findViewById(R.id.tv_choose_time_cancel);
        tv_choose_time_cancel.setOnClickListener(this);

        wv_year = (WheelView) findViewById(R.id.wv_choose_time_year);
        wv_month = (WheelView) findViewById(R.id.wv_choose_time_month);
        wv_day = (WheelView) findViewById(R.id.wv_choose_time_day);
        wv_hour = (WheelView) findViewById(R.id.wv_choose_time_h);
        wv_minute = (WheelView) findViewById(R.id.wv_choose_time_m);

        if (!withTime) {
            wv_hour.setVisibility(View.GONE);
            wv_minute.setVisibility(View.GONE);
            findViewById(R.id.tv_hour).setVisibility(View.GONE);
            findViewById(R.id.tv_minute).setVisibility(View.GONE);
        }
        if (forBankValidDate) {
            wv_day.setVisibility(View.GONE);
            findViewById(R.id.tv_day).setVisibility(View.GONE);
        }


        setCanceledOnTouchOutside(true);
        initData();
    }

    private void initData() {
        initContent();
    }


    /**
     * 初始化时间数据
     */
    private void initContent() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) + 1;
        yearContent = new String[year - 1970 + 1];
        for (int i = 0; i <= year - 1970; i++) {
            yearContent[i] = String.valueOf(i + 1970);
        }
        if (forBankValidDate) {
            year += 30;
            yearContent = new String[year - 2010];
            for (int i = 0; i < year - 2010; i++) {
                yearContent[i] = String.valueOf(i + 2010);
            }
        }

        monthContent = new String[12];
        for (int i = 0; i < 12; i++) {
            monthContent[i] = String.valueOf(i + 1);
            if (monthContent[i].length() < 2) {
                monthContent[i] = "0" + monthContent[i];
            }
        }
//        dayContent = new String[31];
//        for (int i = 0; i < 31; i++) {
//            dayContent[i] = String.valueOf(i + 1);
//            if (dayContent[i].length() < 2) {
//                dayContent[i] = "0" + dayContent[i];
//            }
//        }

        if (withTime) {
            hourContent = new String[24];
            for (int i = 0; i < 24; i++) {
                hourContent[i] = String.valueOf(i);
                if (hourContent[i].length() < 2) {
                    hourContent[i] = "0" + hourContent[i];
                }
            }
            minuteContent = new String[60];
            for (int i = 0; i < 60; i++) {
                minuteContent[i] = String.valueOf(i);
                if (minuteContent[i].length() < 2) {
                    minuteContent[i] = "0" + minuteContent[i];
                }
            }
        }

        if (mSelectedTime == null) {
            int curYear = calendar.get(Calendar.YEAR);
            int curMonth = calendar.get(Calendar.MONTH) + 1;
            int curDay = calendar.get(Calendar.DATE);
            int curHour = calendar.get(Calendar.HOUR_OF_DAY);
            int curMinute = calendar.get(Calendar.MINUTE);

            initShowChooseTime(curYear, curMonth, curDay, curHour, curMinute);
        } else {
            initShowChooseTime(mSelectedTime.getYear(), mSelectedTime.getMonth(), mSelectedTime.getDay(),
                    mSelectedTime.getHour(), mSelectedTime.getMinute());
        }
    }

    /**
     * 设置时间
     */
    private void initShowChooseTime(int curYear, int curMonth, final int curDay, int hour, int minute) {
        wv_year.setViewAdapter(new ArrayWheelAdapter(context, yearContent));

        if (forBankValidDate) {
            wv_year.setCurrentItem(curYear - 2010);
        } else {
            wv_year.setCurrentItem(curYear - 1970);
        }
        wv_year.setCyclic(false);
        wv_year.setInterpolator(new AnticipateOvershootInterpolator());
        wv_month.setViewAdapter(new ArrayWheelAdapter(context, monthContent));
        wv_month.setCurrentItem(curMonth - 1);
        wv_month.setCyclic(true);
        wv_month.setInterpolator(new AnticipateOvershootInterpolator());

        initDayView(curMonth - 1, curDay);

        if (withTime) {
            wv_hour.setViewAdapter(new ArrayWheelAdapter(context, hourContent));
            wv_hour.setCurrentItem(hour);
            wv_hour.setCyclic(true);
            wv_hour.setInterpolator(new AnticipateOvershootInterpolator());

            wv_minute.setViewAdapter(new ArrayWheelAdapter(context, minuteContent));
            wv_minute.setCurrentItem(minute);
            wv_minute.setCyclic(true);
            wv_minute.setInterpolator(new AnticipateOvershootInterpolator());
        }

        wv_month.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                initDayView(newValue, curDay);
            }
        });
    }

    private void initDayView(int month, int curDay) {
        Logger.d(month + "," + curDay);

        if (Integer.parseInt(monthContent[month]) <= 7) {
            if (Integer.parseInt(monthContent[month]) == 2) {
                dayContent = new String[28];
            } else if (Integer.parseInt(monthContent[month]) % 2 == 0) {
                dayContent = new String[30];
            } else {
                dayContent = new String[31];
            }
        } else {
            if (Integer.parseInt(monthContent[month]) % 2 == 0) {
                dayContent = new String[31];
            } else {
                dayContent = new String[30];
            }
        }
        for (int i = 0; i < dayContent.length; i++) {
            dayContent[i] = String.valueOf(i + 1);
            if (dayContent[i].length() < 2) {
                dayContent[i] = "0" + dayContent[i];
            }
        }
        wv_day.setViewAdapter(new ArrayWheelAdapter(context, dayContent));
        wv_day.setCurrentItem(curDay - 1);
        wv_day.setCyclic(true);
        wv_day.setInterpolator(new AnticipateOvershootInterpolator());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_choose_time_ok) {
            this.dismiss();
            if (listener != null) {
                String hour = null, minute = null;
                if (withTime) {
                    hour = hourContent[wv_hour.getCurrentItem()];
                    minute = minuteContent[wv_minute.getCurrentItem()];
                }

                listener.onTimeSelected(yearContent[wv_year.getCurrentItem()], monthContent[wv_month.getCurrentItem()], dayContent[wv_day.getCurrentItem()], hour, minute);
            }

        } else if (i == R.id.tv_choose_time_cancel) {
            this.dismiss();

        }
    }


    public interface OnTimeSelectedListener {
        void onTimeSelected(String year, String month, String day, String hour, String minute);
    }

    public static class TimeModel {
        private int year;
        private int month;
        private int day;
        private int hour;
        private int minute;

        public TimeModel() {
        }

        public TimeModel(int year, int month, int day, int hour, int minute) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        @Override
        public String toString() {
            return "TimeModel{" +
                    "year=" + year +
                    ", month=" + month +
                    ", day=" + day +
                    ", hour=" + hour +
                    ", minute=" + minute +
                    '}';
        }

        public static TimeModel getTimeModel(long timeStamp) {
            if (timeStamp == 0) {
                return null;
            }
            timeStamp = timeStamp * 1000;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒", Locale.CHINA);
                String time = sdf.format(timeStamp);
                String[] times = time.split("[年月日时分秒]");
                TimeModel timeMode = new TimeModel(Integer.parseInt(times[0]), Integer.parseInt(times[1]),
                        Integer.parseInt(times[2]), Integer.parseInt(times[3]), Integer.parseInt(times[4]));
                return timeMode;
            } catch (Exception e) {
            }
            return null;

        }
    }


}
