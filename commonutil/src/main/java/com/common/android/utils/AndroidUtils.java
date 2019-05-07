package com.common.android.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.android.R;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Created by yuanht on 2018/7/21.
 * App 或 Android 常用工具类型
 */
public class AndroidUtils {
    /**
     * 判断SDCard是否可用
     */
    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static void setContentViewBackground(Activity activity, int drawableId) {
        Drawable drawable = activity.getResources().getDrawable(drawableId);
        activity.getWindow().setBackgroundDrawable(drawable);
    }

    /**
     * 更新媒体
     *
     * @param imageUri 文件路径
     */
    public static void updateMediaContent(Context context, Uri imageUri) {
        // 该广播即使多发（即选取照片成功时也发送）也没有关系，只是唤醒系统刷新媒体文件
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageUri);
        context.sendBroadcast(intent);
    }

    /**
     * 更新媒体
     *
     * @param filename 文件全名，包括后缀
     */
    public static void updateMediaContentProvider(Context context, String filename) {
        updateMediaContentProvider(context, new String[]{filename});
    }

    /**
     * 更新媒体
     *
     * @param filenames 文件全名，包括后缀
     */
    public static void updateMediaContentProvider(Context context, String[] filenames) {
        MediaScannerConnection.scanFile(context, filenames, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
            }
        });
    }

    /**
     * 获得当前软件版本名词
     */
    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获得当前软件版本号
     */
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 启动浏览器
     */
    public static Intent getBrowserIntent(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Uri url = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        return intent;
    }

    public static File initCacheDir(Context context, String dirPath) {
        File cacheDir;
        if (!isSDCardAvailable()) {
            cacheDir = context.getDir(dirPath, Context.MODE_PRIVATE);
        } else {
            if (context.getExternalCacheDir() == null) {
                cacheDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + context.getPackageName(), dirPath);
            } else {
                cacheDir = new File(context.getExternalCacheDir(), dirPath);
            }
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
        }
        return cacheDir;
    }


    /**
     * 缓存根目录
     *
     * @return
     */
    public static String getCacheRootPath(Context context) {
        if (isSDCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            return context.getExternalCacheDir().getPath();
        } else {
            // /data/data/<application package>/cache
            return context.getCacheDir().getPath();
        }
    }

    /**
     * 缓存根目录
     *
     * @return
     */
    public static String getInternalCacheRootPath(Context context) {
        return context.getCacheDir().getPath();
    }

    /**
     * 获得媒体文件本地路径
     */
    public static String getMediaFilePath(Context context, Uri data) {
        String path = data.getPath();

        // For gallery application
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(data, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return path;
    }

    /**
     * 调用Camera拍照
     *
     * @param photoFile 保存拍照文件的路径
     *                  <p>
     *                  <pre>在onActivityResult()中resultCode == RESULT_OK时,可以直接从photoFile中获得文件</pre>
     */
    public static Intent getCaptureImageIntent(File photoFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        return intent;
    }

    /**
     * 从图库选择图片，并进行裁剪
     */
    public static Intent getPhotoPickIntent(int width, int height) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "false");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", true);
        return intent;
    }

    public static void openBrowser(Activity activity, String strUrl) {
        Uri url = Uri.parse(strUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        activity.startActivity(intent);
    }


    /**
     * 取得国际移动设备省份吗
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        try {
            TelephonyManager telemamanger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = telemamanger.getSubscriberId();
            String imei = telemamanger.getDeviceId();
            return imei + imsi;
        } catch (Exception ex) {
            Logger.i("TelephonyManager android id have other error");
        }

        return "";
    }

    public static boolean isServiceRunning(Context context, String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        //这个value取任意大于1的值，但返回的列表大小可能比这个值小。
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检查权限，如果权限不允许，弹出授权窗口
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static boolean checkPermission(final Activity context, final String permission, final int requestCode) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(context,
//                    permission)) {
//                return false;
//            }
            ActivityCompat.requestPermissions(context,
                    new String[]{permission},
                    requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkPermission(Context context, String permission, String errorMessage) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            if (!CommonUtils.isEmpty(errorMessage)) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    public static boolean installNormal(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (!file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }

        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }

    /**
     * 根据包名打开应用
     */
    public static void doStartApplicationWithPackageName(Activity context, String packageName) {
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveInfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveInfoList.iterator().next();
        if (resolveinfo != null) {
            // packageName = packageName
            packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    public static boolean getTopAcitivity(Context mContext, String lockActivityName) {
        String topActivityName = "";
        ActivityManager am = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = am
                .getRunningTasks(1);
        if (runningTasks != null && !runningTasks.isEmpty()) {
            ActivityManager.RunningTaskInfo taskInfo = runningTasks.get(0);
            topActivityName = taskInfo.topActivity.getClassName();
        }
        return lockActivityName.equals(topActivityName);
    }

    public static void callPhone(Activity activity, String phoneNumber) {
        //直接拨打电话
//        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        //复制号码到拨号盘
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        if (checkPermission(activity, Manifest.permission.CALL_PHONE, activity.getString(R.string.string_need_phone_permission))) {
            activity.startActivity(intent);
        }
    }

    /**
     * 光标移动到最后
     */
    public static void moveFocusEnd(EditText et) {
        et.setSelection(et.getText().length());
    }

    public static void showStatusBar(Activity context) {
        WindowManager.LayoutParams attrs = context.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        context.getWindow().setAttributes(attrs);
    }

    public static void hideStatusBar(Activity context) {
        WindowManager.LayoutParams attrs = context.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        context.getWindow().setAttributes(attrs);
    }

    public static Toast showCustomToast(Toast mToast, Context activity, int drawableId, String txt) {
        if (mToast != null) {
            mToast.cancel();
        }
        LayoutInflater inflater = LayoutInflater.from(activity);
        View layout = inflater.inflate(R.layout.layout_custom_toast, null);

        ImageView info_img = (ImageView) layout.findViewById(R.id.id_info_img);
        TextView title = (TextView) layout.findViewById(R.id.id_info_txt);
        if (drawableId > 0) {
            info_img.setImageResource(drawableId);
        } else {
            info_img.setVisibility(View.GONE);
        }

        title.setText(txt);
        mToast = new Toast(activity);
        mToast.setGravity(Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(layout);
        mToast.show();
        return mToast;
    }


    /**
     * 获得屏幕高度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        return width;
    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int height = outMetrics.heightPixels;
        return height;
    }

    /**
     * 取得屏幕尺寸
     *
     * @return int[0] width, int[1] height
     */
    public static int[] getResolution(Context context) {
        int[] result = new int[2];
        result[0] = context.getResources().getDisplayMetrics().widthPixels;
        result[1] = context.getResources().getDisplayMetrics().heightPixels;
        return result;
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpVal) {
        int result = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources()
                        .getDisplayMetrics());
        return result;
    }

    /**
     * 设置密码是否可见
     */
    public static void setPwdsetVisibility(EditText et, boolean visible) {
        if (visible) {
            //设置EditText文本为可见的
            et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            //设置EditText文本为隐藏的
            et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        et.postInvalidate();
        //切换后将EditText光标置于末尾
        CharSequence charSequence = et.getText();
        if (charSequence != null) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
    }

    /**
     * 文本复制
     */
    public static boolean copyText(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            return true;
        }
        return false;
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }


}


