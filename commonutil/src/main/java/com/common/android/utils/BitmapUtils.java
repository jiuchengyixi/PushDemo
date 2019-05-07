package com.common.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Bitmap 工具类
 */
public final class BitmapUtils {

    /**
     * default bitmap width
     */
    public static int MAX_WIDTH = 480;

    /**
     * default bitmap height
     */
    public static int MAX_HEIGHT = 800;

    public static final Bitmap createBitmap(Bitmap bitmap, int target_width,
                                            int target_height) {
        return createBitmap(bitmap2Bytes(bitmap), target_width, target_height);
    }

    public static final Bitmap createBitmap(byte[] data, int target_width,
                                            int target_height) {
        try {
            int width = target_width <= 0 ? MAX_WIDTH : target_width;
            int height = target_height <= 0 ? MAX_HEIGHT : target_height;
            int minSideLength = 0;
            Options opts = new Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            // set parameter
            minSideLength = Math.min(width, height);
            opts.inSampleSize = computeSampleSize(opts, minSideLength, width
                    * height);
            opts.inJustDecodeBounds = false;
            opts.inInputShareable = true;
            opts.inPurgeable = true;
            opts.inPreferredConfig = Config.RGB_565;
            // decode bitmap
            return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        } catch (Exception e) {
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final Bitmap createBitmap(Resources res, int resId,
                                            int target_width, int target_height) {
        try {
            int width = target_width <= 0 ? MAX_WIDTH : target_width;
            int height = target_height <= 0 ? MAX_HEIGHT : target_height;
            int minSideLength = 0;
            Options opts = new Options();
            opts.inJustDecodeBounds = true;

            BitmapFactory.decodeResource(res, resId, opts);
            // set parameter
            minSideLength = Math.min(width, height);
            opts.inSampleSize = computeSampleSize(opts, minSideLength, width
                    * height);
            opts.inJustDecodeBounds = false;
            opts.inInputShareable = true;
            opts.inPurgeable = true;
            opts.inPreferredConfig = Config.RGB_565;
            // decode bitmap
            return BitmapFactory.decodeResource(res, resId, opts);
        } catch (Exception e) {
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final Bitmap createBitmap(String path, int target_width,
                                            int target_height) {
        return createBitmap(path, target_width, target_height, Config.RGB_565,
                true);
    }

    public static final Bitmap createBitmap(String path, int target_width,
                                            int target_height, Config config, boolean doDegree) {
        try {
            int width = target_width <= 0 ? MAX_WIDTH : target_width;
            int height = target_height <= 0 ? MAX_HEIGHT : target_height;
            int minSideLength = 0;
            Options opts = new Options();
            opts.inJustDecodeBounds = true;

            // bitmap degree
            int degree = 0;
            if (doDegree)
                degree = parserBitmapDegree(path);

            // decode bitmap
            BitmapFactory.decodeFile(path, opts);

            // set parameter
            minSideLength = Math.min(width, height);
            opts.inSampleSize = computeSampleSize(opts, minSideLength, width
                    * height);
            opts.inJustDecodeBounds = false;
            opts.inInputShareable = true;
            opts.inPurgeable = true;
            opts.inPreferredConfig = config != null ? config : Config.RGB_565;

            // decode bitmap
            Bitmap out = BitmapFactory.decodeFile(path, opts);

            // parser bitmap degree
            if (out != null && degree != 0) {
                return rotateBitmap(degree, out);
            }

            // decode bitmap
            return out;
        } catch (Exception e) {
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 分析当前图片的旋转角度
     */
    public static int parserBitmapDegree(String path) {

        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return degree;
    }

    /**
     * 旋转图片
     */
    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        // 是否需要旋转
        if (angle == 0) {
            return bitmap;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        Bitmap out = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);

        if (out != null && bitmap != out) {
            bitmap.recycle();
        }

        return out;
    }

    private final static int computeSampleSize(Options options,
                                               int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private final static int computeInitialSampleSize(
            Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 回收Bitmap资源
     */
    public static void recycleBitmap(Bitmap tempBitmap) {
        if (tempBitmap != null && !tempBitmap.isRecycled()) {
            tempBitmap.recycle();
            tempBitmap = null;
        }
    }

    /**
     * 回收ImageView中的bitmap资源
     */
    public static void recycleImageViewBitmap(ImageView imageView) {
        BitmapDrawable tempBitmapDrawable = (BitmapDrawable) imageView
                .getDrawable();
        if (tempBitmapDrawable != null) {
            recycleBitmap(tempBitmapDrawable.getBitmap());
        }
    }

    /**
     * Bitmap 转 byte[]
     */
    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 获得Bitmap占用内存大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (bitmap == null)
            return 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * 保存Bitmap到文件中
     *
     * @throws FileNotFoundException
     */
    public static boolean saveBitmap2file(Bitmap bmp, String path)
            throws FileNotFoundException {
        CompressFormat format = CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = new FileOutputStream(path);
        return bmp.compress(format, quality, stream);
    }

    /**
     * 把batmap 转file
     * @param bitmap
     * @param filepath
     */
    public static File saveBitmapFile(Bitmap bitmap, String filepath) {
        File file = new File(filepath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 加载本地图片资源，可缩放
     */
    public static Bitmap loadBitmapByResId(Context context, int resId,
                                           boolean needZoom, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;

        if (resId <= 0) {
            return bitmap;
        }

        if (needZoom) {
            Options opts = new Options();
            opts.inPurgeable = true;
            opts.inInputShareable = true;

            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(
                    context.getResources().openRawResource(resId), null, opts);

            int sampleSize = getSimpleSize(opts, maxWidth, maxHeight);

            opts.inSampleSize = sampleSize;
            opts.inJustDecodeBounds = false;
        }

        bitmap = BitmapFactory.decodeStream(context.getResources()
                .openRawResource(resId));
        return bitmap;
    }

    /**
     * 缩放图片
     */
    public static Bitmap getScaledBitmap(Bitmap bitmap, int maxWidth,
                                         int maxHeight) {
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scale = Math.min(((float) maxWidth) / width,
                    ((float) maxHeight) / height);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width * scale),
                    (int) (height * scale), false);
        } catch (Exception e) {
        }
        return bitmap;
    }

    /**
     * 缩放图片
     */
    public static byte[] getScaledBitmap2Byte(Bitmap bitmap, int maxSize)
            throws IOException {
        int quality = 100;
        byte[] b = imageTobyte(bitmap, quality);
        if (b.length > maxSize) {
            while (true) {
                quality = quality >> 1;
                b = imageTobyte(bitmap, quality);
                if (b.length < maxSize) {
                    return b;
                }
            }
        }
        return b;
    }

    /**
     * 缩放图片
     */
    public static Bitmap getScaledBitmap(Bitmap bitmap, int maxSize)
            throws IOException {
        return Bytes2Bimap(getScaledBitmap2Byte(bitmap, maxSize));
    }

    private static byte[] imageTobyte(Bitmap bitmap, int quality)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, quality, baos);
        byte[] b = baos.toByteArray();
        baos.close();
        return b;
    }

    /**
     * 获得合适的缩放比例
     */
    public static int getSimpleSize(double w, double h, int maxWidth,
                                    int maxHeight) {
        int initialSize = (int) Math.max(Math.ceil(w / maxWidth),
                Math.ceil(h / maxHeight));
        int simpleSize = 1;
        if (initialSize <= 8) {
            while (simpleSize < initialSize) {
                simpleSize <<= 1;
            }
        } else {
            simpleSize = (initialSize + 7) / 8 * 8;

        }
        return simpleSize;
    }

    /**
     * 获得合适的缩放比例
     */
    private static int getSimpleSize(Options opts, int maxWidth,
                                     int maxHeight) {
        double w = opts.outWidth;
        double h = opts.outHeight;
        int initialSize = (int) Math.max(Math.ceil(w / maxWidth),
                Math.ceil(h / maxHeight));
        int simpleSize = 1;
        if (initialSize <= 8) {
            while (simpleSize < initialSize) {
                simpleSize <<= 1;
            }
        } else {
            simpleSize = (initialSize + 7) / 8 * 8;

        }
        return simpleSize;
    }

    public static Bitmap createRotateReflectedMap(Bitmap originalBitmap) {
        float width = ((float) 200) / (originalBitmap.getWidth());
        float height = ((float) 200) / (originalBitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(width, height);
        originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                originalBitmap.getWidth(), originalBitmap.getHeight(), matrix,
                true);
        Bitmap bitmap = createReflectedImage(originalBitmap);
        bitmap = createRotateImage(bitmap);
        return bitmap;
    }

    public static Bitmap createRotateReflectedMap(Context ctx, Drawable resId) {

        Bitmap bitmap = ((BitmapDrawable) resId).getBitmap();
        if (bitmap != null) {
            return createRotateReflectedMap(bitmap);
        }
        return null;
    }

    public static Bitmap createRotateImage(Bitmap originalBitmap) {
        Camera camera = new Camera();
        camera.save();
        camera.rotateY(10f);
        Matrix mMatrix = new Matrix();
        camera.getMatrix(mMatrix);
        camera.restore();

        Bitmap bm = Bitmap.createBitmap(originalBitmap, 0, 0,
                originalBitmap.getWidth(), originalBitmap.getHeight(), mMatrix,
                true);
        // Bitmap bm = Bitmap.createBitmap(originalBitmap, 0,
        // 0,270,270,mMatrix,true);
        return bm;
    }

    public static Bitmap createReflectedImage(Bitmap originalBitmap) {
        final int reflectionGap = 4;

        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        Matrix matrix = new Matrix();

        matrix.preScale(1, -1);
        Bitmap reflectionBitmap = Bitmap.createBitmap(originalBitmap, 0,
                height / 2, width, height / 2, matrix, false);
        Bitmap withReflectionBitmap = Bitmap.createBitmap(width, (height
                + height / 2 + reflectionGap), Config.ARGB_8888);

        Canvas canvas = new Canvas(withReflectionBitmap);
        canvas.drawBitmap(originalBitmap, 0, 0, null);

        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);

        canvas.drawBitmap(reflectionBitmap, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,
                originalBitmap.getHeight(), 0,
                withReflectionBitmap.getHeight(), 0x70ffffff, 0x00ffffff,
                TileMode.MIRROR);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

        canvas.drawRect(0, height, width, withReflectionBitmap.getHeight(),
                paint);

        return withReflectionBitmap;
    }

    /**
     * 保存成PNG图片
     */
    public static void saveBitmapAsPNG(String savePath, Bitmap bitmap) {
        File f = new File(savePath);

        if (f.exists())
            f.delete();
        try {
            f.createNewFile();
        } catch (IOException e) {
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap.compress(CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 视频缩略图
     */
    public static Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(0L, 1);
        } catch (IllegalArgumentException ex) {
        } catch (RuntimeException ex) {
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }
        return bitmap;
    }

    public static Bitmap stringtoBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string, 0);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();
        return Base64.encodeToString(appicon, 0);
    }

    public static byte[] convertIconTobyte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static String encode2base64(Bitmap image) {
        return encode2base64(image, 50);
    }

    public static String encode2base64(Bitmap image, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, quality, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    /**
     * 矩形图片
     */
    public static Bitmap createRectBitmap(int x, int y, int alpha, int r,
                                          int g, int b) {
        Bitmap output = Bitmap.createBitmap(x, y, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        // 新建一个矩形
        RectF outerRect = new RectF(0, 0, x, y);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setARGB(alpha, r, g, b);
        canvas.drawRect(outerRect, paint);
        canvas.save();
        canvas.restore();
        return output;
    }

    /**
     * 圆形图片
     */
    public static Bitmap createCircleBitmap(float radius, int alpha, int r,
                                            int g, int b) {
        Bitmap output = Bitmap.createBitmap((int) radius * 2, (int) radius * 2,
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        // 新建一个矩形
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setARGB(alpha, r, g, b);
        canvas.drawCircle(radius, radius, radius, paint);
        canvas.save();
        canvas.restore();
        return output;
    }

    /**
     * 圆角矩形图片
     *
     * @param x              图像的宽度
     * @param y              图像的高度
     * @param image          源图片
     * @param outerRadiusRat 圆角的大小
     * @param color          Java中定义的Color值
     * @return 圆角图片
     */
    public static Bitmap createFramedPhoto(int x, int y, Bitmap image,
                                           float outerRadiusRat, int color) {
        // 根据源文件新建一个darwable对象
        Drawable imageDrawable = new BitmapDrawable(image);

        // 新建一个新的输出图片
        Bitmap output = Bitmap.createBitmap(x, y, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // 新建一个矩形
        RectF outerRect = new RectF(0, 0, x, y);

        // 产生一个红色的圆角矩形
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint);

        // 将源图片绘制到这个圆角矩形上
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        imageDrawable.setBounds(0, 0, x, y);
        canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
        imageDrawable.draw(canvas);
        canvas.save();
        canvas.restore();

        return output;
    }

    /**
     * 把一个View的对象转换成bitmap
     */
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        // 能画缓存就返回false
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    /**
     * 遮罩图
     */
    public static Bitmap getMaskBitmap(Bitmap original, Bitmap mask, int left,
                                       int top) {
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(),
                Config.ARGB_8888);
        // 将遮罩层的图片放到画布中
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 设置两张图片相交时的模式
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawBitmap(original, 0, 0, null);
        canvas.drawBitmap(mask, left, top, paint);
        paint.setXfermode(null);
        canvas.save();
        canvas.restore();
        try {
            original.recycle();
            mask.recycle();
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 抠图
     */
    public static Bitmap getNoMaskBitmap(Bitmap original, Bitmap mask,
                                         int left, int top) {
        Bitmap result = Bitmap.createBitmap(original.getWidth(),
                original.getHeight(), Config.ARGB_8888);
        // 将遮罩层的图片放到画布中
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 设置两张图片相交时的模式
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
        canvas.drawBitmap(original, 0, 0, null);
        canvas.drawBitmap(mask, left, top, paint);
        paint.setXfermode(null);
        canvas.save();
        canvas.restore();
        try {
            original.recycle();
            mask.recycle();
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 抠图
     */
    public static Bitmap getNoMaskBitmap(Bitmap original, int maskWidth,
                                         int maskHeight, int left, int top) {
        Bitmap mask = Bitmap.createBitmap(maskWidth, maskHeight,
                Config.ARGB_8888);
        return getNoMaskBitmap(original, mask, left, top);
    }

    /**
     * 放大缩小图片
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newbmp;
    }

    /**
     * 将Drawable转化为Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将Bitmap转化为Drawable
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * 获得带倒影的图片方法
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
                width, height / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * 图片灰化处理
     */
    public static Bitmap getGrayBitmap(Bitmap bitmap) {
        Bitmap mGrayBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mGrayBitmap);
        Paint mPaint = new Paint();

        // 创建颜色变换矩阵
        ColorMatrix mColorMatrix = new ColorMatrix();
        // 设置灰度影响范围
        mColorMatrix.setSaturation(0);
        // 创建颜色过滤矩阵
        ColorMatrixColorFilter mColorFilter = new ColorMatrixColorFilter(
                mColorMatrix);
        // 设置画笔的颜色过滤矩阵
        mPaint.setColorFilter(mColorFilter);
        // 使用处理后的画笔绘制图像
        mCanvas.drawBitmap(bitmap, 0, 0, mPaint);

        return mGrayBitmap;
    }

    /**
     * 图片圆角处理
     *
     * @param roundPx 圆角半径
     */
    public static Bitmap getRoundedBitmap(Bitmap bitmap, float roundPx) {
        // 创建新的位图
        Bitmap bgBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        // 把创建的位图作为画板
        Canvas mCanvas = new Canvas(bgBitmap);

        Paint mPaint = new Paint();
        Rect mRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF mRectF = new RectF(mRect);
        mPaint.setAntiAlias(true);
        // 先绘制圆角矩形
        mCanvas.drawRoundRect(mRectF, roundPx, roundPx, mPaint);

        // 设置图像的叠加模式
        mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        // 绘制图像
        mCanvas.drawBitmap(bitmap, mRect, mRect, mPaint);

        return bgBitmap;
    }

    /**
     * 图片旋转
     */
    public static Bitmap getRotatedBitmap(Bitmap bitmap, float degree) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preRotate(degree);
        Bitmap mRotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);

        return mRotateBitmap;
    }

    /**
     * 图片倾斜
     */
    public static Bitmap getScrewBitmap(Bitmap bitmap, float kx, float ky) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preSkew(kx, ky);
        Bitmap mScrewBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);

        return mScrewBitmap;
    }

    /**
     * 获取指定Activity的截屏，保存到png文件
     */
    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    /**
     * 任意圆角矩形
     */
    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels,
                                                int w, int h,
                                                boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR) {
        int time1 = (int) System.currentTimeMillis();

        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        //make sure that our rounded corner is scaled appropriately
        final float roundPx = pixels * densityMultiplier;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);


        //draw rectangles over the corners we want to be square
        if (squareTL) {
            canvas.drawRect(0, 0, w / 2, h / 2, paint);
        }
        if (squareTR) {
            canvas.drawRect(w / 2, 0, w, h / 2, paint);
        }
        if (squareBL) {
            canvas.drawRect(0, h / 2, w / 2, h, paint);
        }
        if (squareBR) {
            canvas.drawRect(w / 2, h / 2, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(input, 0, 0, paint);
        int time2 = (int) System.currentTimeMillis();
        return output;
    }


    public static Drawable zoomRoundedCornerDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        newbmp = getRoundedCornerBitmap(newbmp);
        if (!oldbmp.isRecycled()) {
            oldbmp.recycle();
        }
        return new BitmapDrawable(null, newbmp);
    }


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap roundBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(roundBitmap);
        int color = 0xff424242;
        Paint paint = new Paint();
        int radius;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            radius = bitmap.getHeight() / 2;
        } else {
            radius = bitmap.getWidth() / 2;
        }
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);


        int borderWith = 2;
        Paint mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(Color.WHITE);
        mBorderPaint.setStrokeWidth(borderWith);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getWidth() / 2, radius - borderWith, mBorderPaint);
        return roundBitmap;
    }

    public static Bitmap getBitmapFromLocalDrawable(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }
}
