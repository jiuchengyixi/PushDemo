package com.wind.canada.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.android.utils.AndroidUtils;
import com.common.android.utils.CheckUtil;
import com.common.android.utils.CommonUtils;
import com.common.android.widget.LoadingDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.wind.canada.R;
import com.wind.canada.config.Config;
import com.wind.canada.db.DBManager;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoaderInterface;


/**
 * Created by yuanht on 16/6/24.
 */
public class BaseActivity extends AppCompatActivity {
    protected Activity mContext;
    protected DBManager mDBManager;

    private LoadingDialog mLoadingDialog;
    private Toast mToast;

    protected ImageView mBackButton;
    protected ImageView mRightImageView;
    protected TextView mTitleView;
    protected TextView mRightTextView;
    protected View mTitleLineView;

    private TextView mEmptyTextView;
    private ImageView mEmptyImageView;
    private View mEmptyView;
    protected TextView mEmptyTextViewReconnect;

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 0x11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mDBManager = DBManager.getInstance(getApplicationContext());
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        mBackButton = findViewById(R.id.btn_title_back);
        if (mBackButton != null) {
            mBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        View titleView = findViewById(R.id.tv_title_title);
        if (titleView != null && titleView instanceof TextView) {
            mTitleView = (TextView) titleView;
        }

        View rightView = findViewById(R.id.tv_title_right);
        if (rightView != null && rightView instanceof TextView) {
            mRightTextView = (TextView) rightView;
        }
        View rightImageView = findViewById(R.id.img_title_right);
        if (rightImageView != null && rightImageView instanceof ImageView) {
            mRightImageView = (ImageView) rightImageView;
        }
        mTitleLineView = findViewById(R.id.view_title_line);

        //recyclerview 空数据
        mEmptyImageView = findViewById(R.id.img_none);
        mEmptyTextView = findViewById(R.id.tv_none);
        mEmptyTextViewReconnect = findViewById(R.id.tv_reconnect);
        mEmptyView = findViewById(R.id.layout_data_none);
    }

    protected void setBackgroud(int resId) {
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(resId));
    }

    protected void showToast(int resId) {
        showToast(getString(resId));
    }

    protected void showToast(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                }
//                mToast = CommonUtils.showCustomToast(mToast, mContext, -1, txt);
                mToast = Toast.makeText(mContext, " " + txt + " ", Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.show();
            }
        });
    }

    public void setTitle(int resId) {
        setTitle(getString(resId));
    }

    public void setTitle(String title) {
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
    }

    protected void showBusy() {
        showToast(R.string.network_busy_or_data_error);
    }

    protected void showOnworking() {
        showToast(R.string.function_on_working);
    }

    protected void showLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog == null) {
                    mLoadingDialog = new LoadingDialog(mContext);
                    mLoadingDialog.setDefaultView().setCanceledOnTouchOutside(true);
                }
                if (!mLoadingDialog.isShowing()) {
                    if (isFinishing()) {
                        return;
                    }
                    mLoadingDialog.show();
                }
            }
        });

    }

    protected void dismissLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
    }

    @Override
    public void startActivity(Intent intent) {
        startActivityWithAlpha(intent);
    }

    public void startActivityWithAlpha(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    public void startActivityWithPushUpIn(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    public void startActivityWithSlide(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    public void startActivityWithoutAnim(Intent intent) {
        super.startActivity(intent);
    }

    @Override
    public void finish() {
        finishWithAlpha();
    }

    public void finishWithAlpha() {
        super.finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    public void finishWithSlide() {
        super.finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    public void finishWithNoAnim() {
        super.finish();
    }

    protected void showNetworkError() {
        showToast(getString(R.string.network_not_available));
    }

    /**
     * 判断空
     */
    protected boolean checkNull(TextView tv, String errorMessage) {
        if (CommonUtils.isEmpty(tv.getText().toString().trim())) {
            tv.requestFocus();
            showToast(errorMessage);
            return true;
        }
        return false;
    }


    protected void hideSoftKeyboard(View view, boolean hidden) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (hidden) {
            view.requestFocus();
            // 隐藏软键盘
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            // 强制隐藏软键盘
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
            // 强制显示软键盘
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    public void setEditTextFocus(EditText editText, boolean isFocused) {
        editText.setCursorVisible(isFocused);
        editText.setFocusable(isFocused);
        editText.setFocusableInTouchMode(isFocused);
        if (isFocused) {
            editText.requestFocus();
        }
    }

    protected boolean isLogin() {
        return mDBManager.isLogin();
    }

    protected void setText(TextView textView, String text) {
        if (text == null) {
            text = "";
        }
        textView.setText(text);
    }

    protected <T> T parseResult(Object response, boolean showError) {
//        BaseResultModel<T> resultModel = (BaseResultModel) response;
//        if (resultModel.getCode() == Result.Success.getCode()) {
//            if (resultModel.getData() != null) {
//                return resultModel.getData();
//            }
//        } else {
//            if (showError) {
//                showToast(resultModel.getError());
//            }
//        }
        return null;
    }

    protected boolean parseResultSuccess(Object response, boolean showError) {
//        BaseResultModel resultModel = (BaseResultModel) response;
//        if (resultModel.getCode() == Result.Success.getCode()) {
//            return true;
//        } else {
//            if (showError) {
//                showToast(resultModel.getError());
//            }
//        }
        return false;
    }

    public void initBannerView(Banner bannerView, ImageLoaderInterface imageLoader, boolean resetLayout, int padding) {
        //设置banner样式
        bannerView.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        bannerView.setImageLoader(imageLoader);
        //设置图片集合
        //bannerView.setImages(images);
        //设置banner动画效果
//        bannerView.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
        //bannerView.setBannerTitles(titles);
        //设置自动轮播，默认为true
        bannerView.isAutoPlay(true);
        //设置轮播时间
        bannerView.setDelayTime(Config.PICTURE_SCROLL_INTERVAL);
        //设置指示器位置（当banner模式中有指示器时）
        bannerView.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        //bannerView.start();

        if (resetLayout) {
            int width = AndroidUtils.getScreenWidth(mContext) - padding * 2;
            int imageHeight = width * 9 / 16;
            if (bannerView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) bannerView.getLayoutParams();
                layoutParams.height = imageHeight;
                bannerView.setLayoutParams(layoutParams);
            } else if (bannerView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) bannerView.getLayoutParams();
                layoutParams.height = imageHeight;
                bannerView.setLayoutParams(layoutParams);
            }
        }
    }

    //自定义高度的banner
    public void initBannerView(Banner bannerView, ImageLoaderInterface imageLoader, boolean resetLayout, int padding, int imageHeight) {
        //设置banner样式
        bannerView.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        bannerView.setImageLoader(imageLoader);
        //设置图片集合
        //bannerView.setImages(images);
        //设置banner动画效果
//        bannerView.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
        //bannerView.setBannerTitles(titles);
        //设置自动轮播，默认为true
        bannerView.isAutoPlay(true);
        //设置轮播时间
        bannerView.setDelayTime(Config.PICTURE_SCROLL_INTERVAL);
        //设置指示器位置（当banner模式中有指示器时）
        bannerView.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        //bannerView.start();

        if (resetLayout) {
//            int width = AndroidUtils.getScreenWidth(mContext) - padding * 2;
//            int imageHeight = width * 9 / 16;
            int height = AndroidUtils.dp2px(mContext, imageHeight);
            if (bannerView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) bannerView.getLayoutParams();
                layoutParams.height = height;
                bannerView.setLayoutParams(layoutParams);
            } else if (bannerView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) bannerView.getLayoutParams();
                layoutParams.height = height;
                bannerView.setLayoutParams(layoutParams);
            }
        }
    }

    /**
     * 判断手机号码是否正确
     */
    protected boolean isValidPhoneNumber(EditText et) {
        if (checkNull(et, "请输入手机号码")) {
            et.requestFocus();
            return false;
        }
        String phone = et.getText().toString();
        if (!CheckUtil.isPhoneNumber(phone)) {
            et.requestFocus();
            showToast("手机号码格式不正确！");
            return false;
        }
        return true;
    }

    /**
     * 获取焦点
     */
    public void requestFocus(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    protected boolean checkLoginAndStartActivity(boolean showToast) {
        if (mDBManager.isLogin()) {
            return true;
        }

//        if (showToast) {
//            showToast(R.string.request_login);
//        }
//        startActivity(new Intent(mContext, LoginActivity.class));
        return false;
    }

    protected void saveUserModel() {

    }

    protected void initLocation() {
        /*BaiduMapUtils.getLocation(new BaiduMapUtils.LocationListener() {
            @Override
            public void onReceiveLocation(double lat, double lon, String country, String province, String city, String district, String address) {
                if (CommonUtils.checkLatLon(lat, lon)) {
                    MyApplication.locationModel.setLatitude(lat);
                    MyApplication.locationModel.setLongitude(lon);
                    MyApplication.locationModel.setProvince(province);
                    MyApplication.locationModel.setCity(city);
                    MyApplication.locationModel.setDistrict(district);
                    MyApplication.locationModel.setCountry(country);
                    MyApplication.locationModel.setAddress(address);
                    onLocationReceived(true);
                } else {
//                    MyApplication.locationModel.setProvince(Config.DEFAULT_PROVINCE);
//                    MyApplication.locationModel.setCity(Config.DEFAULT_CITY);
//                    MyApplication.locationModel.setDistrict(Config.DEFAULT_AREA);
//                    onLocationReceived(false);
                }

            }
        });*/
    }

    protected void onLocationReceived(boolean success) {
    }


}
