package com.common.android.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.io.File;

/**
 * 图片选择裁剪
 */
public class PicPickUtils {
    private final Activity activity;
    private final Fragment fragmentContext;
    private final OnPickedlistener onPicPickedlistener;
    // 是否裁剪
    private boolean doCrop;
    // 宽
    private int cropWidth = 200;
    // 高
    private int cropHeight = 200;
    // 保存图片本地路径
    private final String saveDir;

    private String IMAGE_FILE_NAME = "";
    private String TMP_IMAGE_FILE_NAME = "";
    // 常量定义
    /**
     * 相机拍照
     */
    private static final int TAKE_A_PICTURE = 0x901;
    private static final int SELECT_A_PICTURE = 0x902;
    private static final int SET_PICTURE = 0x903;
    private static final int SET_ALBUM_PICTURE_KITKAT = 0x904;
    private static final int SELECET_A_PICTURE_AFTER_KIKAT = 0x905;
    //private String mAlbumPicturePath = null;

    // 版本比较：是否是4.4及以上版本
    private final boolean mIsKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    public PicPickUtils(Activity context, Fragment fragmentContext, String saveDir, OnPickedlistener onPicPickedlistener) {
        this.activity = context;
        this.fragmentContext = fragmentContext;
        this.onPicPickedlistener = onPicPickedlistener;
        this.saveDir = saveDir;
    }

    /**
     * 设置是否裁剪
     *
     * @param doCrop true: 是 false:否
     */
    public void setDoCrop(boolean doCrop) {
        this.doCrop = doCrop;
    }

    /**
     * 初始化
     *
     * @param outWith   宽度
     * @param outHeight 高度
     */
    public void init(final int outWith, final int outHeight) {
        this.cropWidth = outWith;
        this.cropHeight = outHeight;

        File imagepath = new File(saveDir);
        if (!imagepath.exists()) {
            imagepath.mkdirs();
        }
        long currentTimeMillis = System.currentTimeMillis();
        IMAGE_FILE_NAME = currentTimeMillis + ".jpeg";
        TMP_IMAGE_FILE_NAME = "tmp_" + currentTimeMillis + ".jpeg";
    }

    public boolean pickResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_A_PICTURE) {
            if (resultCode == activity.RESULT_OK && null != data) {
                String picturePath;
                if (doCrop) {
                    picturePath = new File(saveDir, TMP_IMAGE_FILE_NAME).getPath();
                } else {
                    picturePath = getPath(activity.getApplicationContext(), data.getData());
                }
                if (onPicPickedlistener != null) {
                    onPicPickedlistener.onPicPicked(picturePath);
                }
            } else if (resultCode == activity.RESULT_CANCELED) {

            }
            return true;
        } else if (requestCode == SELECET_A_PICTURE_AFTER_KIKAT) {
            if (resultCode == activity.RESULT_OK && null != data) {
                String mAlbumPicturePath = getPath(activity.getApplicationContext(), data.getData());
                if (doCrop) {
                    cropImageUriAfterKikat(Uri.fromFile(new File(mAlbumPicturePath)));
                } else {
                    if (onPicPickedlistener != null) {
                        onPicPickedlistener.onPicPicked(mAlbumPicturePath);
                    }
                }
            } else if (resultCode == activity.RESULT_CANCELED) {
            }
            return true;
        } else if (requestCode == SET_ALBUM_PICTURE_KITKAT) {
            if (resultCode == activity.RESULT_OK && onPicPickedlistener != null) {
                onPicPickedlistener.onPicPicked(new File(saveDir, TMP_IMAGE_FILE_NAME).getPath());
            }
            return true;
        } else if (requestCode == TAKE_A_PICTURE) {
            if (resultCode == activity.RESULT_OK) {
                if (doCrop) {
                    cameraCropImageUri(Uri.fromFile(new File(saveDir, IMAGE_FILE_NAME)));
                } else {
                    if (onPicPickedlistener != null) {
                        onPicPickedlistener.onPicPicked(new File(saveDir, IMAGE_FILE_NAME).getPath());
                    }
                }
            } else {
            }
            return true;
        } else if (requestCode == SET_PICTURE) {
            // 拍照的设置头像 不考虑版本
            if (resultCode == activity.RESULT_OK && null != data) {
                if (onPicPickedlistener != null) {
                    onPicPickedlistener.onPicPicked(new File(saveDir, IMAGE_FILE_NAME).getPath());
                }
            } else if (resultCode == activity.RESULT_CANCELED) {
            }
            return true;
        }
        return false;
    }

    /**
     * 调用拍照
     */
    public void doTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(saveDir, IMAGE_FILE_NAME)));
        if (fragmentContext != null) {
            fragmentContext.startActivityForResult(intent, TAKE_A_PICTURE);
        } else {
            activity.startActivityForResult(intent, TAKE_A_PICTURE);
        }
    }

    /**
     * 拍照图片的保存路径
     */
    public String getCaptureImagePath() {
        return new File(saveDir, IMAGE_FILE_NAME).getPath();
    }

    /**
     * 调用相册
     */
    public void doPickPhotoFromGallery() {
        if (mIsKitKat) {
            selectImageUriAfterKikat();
        } else {
            if (doCrop) {
                cropImageUri();
            } else {
                cropImageUriNo();
            }
        }
    }

    /**
     * <br>
     * 功能简述:不裁剪图片方法实现---------------------- 相册 <br>
     * 功能详细描述: <br>
     * 注意:
     */
    private void cropImageUriNo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        if (fragmentContext != null) {
            fragmentContext.startActivityForResult(intent, SELECT_A_PICTURE);
        } else {
            activity.startActivityForResult(intent, SELECT_A_PICTURE);
        }
    }

    /**
     * <br>
     * 功能简述:裁剪图片方法实现---------------------- 相册 <br>
     * 功能详细描述: <br>
     * 注意:
     */
    private void cropImageUri() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", cropWidth);
        intent.putExtra("aspectY", cropHeight);
        intent.putExtra("outputX", cropWidth);
        intent.putExtra("outputY", cropHeight);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(saveDir, TMP_IMAGE_FILE_NAME)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        if (fragmentContext != null) {
            fragmentContext.startActivityForResult(intent, SELECT_A_PICTURE);
        } else {
            activity.startActivityForResult(intent, SELECT_A_PICTURE);
        }
    }

    /**
     * <br>
     * 功能简述:4.4以上裁剪图片方法实现---------------------- 相册 <br>
     * 功能详细描述: <br>
     * 注意:
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void selectImageUriAfterKikat() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (fragmentContext != null) {
            fragmentContext.startActivityForResult(intent, SELECET_A_PICTURE_AFTER_KIKAT);
        } else {
            activity.startActivityForResult(intent, SELECET_A_PICTURE_AFTER_KIKAT);
        }
    }

    /**
     * <br>
     * 功能简述:裁剪图片方法实现----------------------相机 <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @param uri
     */
    private void cameraCropImageUri(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/jpeg");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", cropWidth);
        intent.putExtra("aspectY", cropHeight);
        intent.putExtra("outputX", cropWidth);
        intent.putExtra("outputY", cropHeight);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        if (fragmentContext != null) {
            fragmentContext.startActivityForResult(intent, SET_PICTURE);
        } else {
            activity.startActivityForResult(intent, SET_PICTURE);
        }
    }

    /**
     * <br>
     * 功能简述: 4.4及以上改动版裁剪图片方法实现 --------------------相机 <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @param uri
     */
    private void cropImageUriAfterKikat(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/jpeg");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", cropWidth);
        intent.putExtra("aspectY", cropHeight);
        intent.putExtra("outputX", cropWidth);
        intent.putExtra("outputY", cropHeight);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(saveDir, TMP_IMAGE_FILE_NAME)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        if (fragmentContext != null) {
            fragmentContext.startActivityForResult(intent, SET_ALBUM_PICTURE_KITKAT);
        } else {
            activity.startActivityForResult(intent, SET_ALBUM_PICTURE_KITKAT);
        }
    }

    /**
     * <br>
     * 功能简述:4.4及以上获取图片的方法 <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @param context
     * @param uri
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public interface OnPickedlistener {
        void onPicPicked(String path);
    }

    public boolean checkCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检查权限，如果权限不允许，弹出授权窗口
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public boolean checkCameraPermission(final Activity context, final int requestCode) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.CAMERA},
                    requestCode);
            return false;
        } else {
            return true;
        }
    }

    public boolean onRequestPermissionsResult(String[] permissions, int[] grantResults) {
        return grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
