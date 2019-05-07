package com.common.android.utils.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.common.android.utils.AndroidUtils;
import com.common.android.utils.BitmapUtils;
import com.common.android.utils.CommonUtils;
import com.common.android.utils.Logger;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

/**
 * Created by yuanht on 2017/7/18.
 */

public class FrescoUtils {
    /**
     * 常用属性设置
     * <p>
     * <pre>
     *
     * <com.facebook.drawee.view.SimpleDraweeView
     * android:layout_width="20dp"
     * android:layout_height="20dp"
     * fresco:fadeDuration="300" // 淡出时间，毫秒。
     * fresco:actualImageScaleType="focusCrop" // 等同于android:scaleType。
     * fresco:placeholderImage="@color/wait_color" // 加载中…时显示的图。
     * fresco:placeholderImageScaleType="fitCenter" // 加载中…显示图的缩放模式。
     * fresco:failureImage="@drawable/error" // 加载失败时显示的图。
     * fresco:failureImageScaleType="centerInside" // 加载失败时显示图的缩放模式。
     * fresco:retryImage="@drawable/retrying" // 重试时显示图。
     * fresco:retryImageScaleType="centerCrop" // 重试时显示图的缩放模式。
     * fresco:progressBarImage="@drawable/progress_bar" // 进度条显示图。
     * fresco:progressBarImageScaleType="centerInside" // 进度条时显示图的缩放模式。
     * fresco:progressBarAutoRotateInterval="1000" // 进度条旋转时间间隔。
     * fresco:backgroundImage="@color/blue" // 背景图，不会被View遮挡。
     *
     * fresco:roundAsCircle="false" // 是否是圆形图片。
     * fresco:roundedCornerRadius="1dp" // 四角圆角度数，如果是圆形图片，这个属性被忽略。
     * fresco:roundTopLeft="true" // 左上角是否圆角。
     * fresco:roundTopRight="false" // 右上角是否圆角。
     * fresco:roundBottomLeft="false" // 左下角是否圆角。
     * fresco:roundBottomRight="true" // 左下角是否圆角。
     * fresco:roundingBorderWidth="2dp" // 描边的宽度。
     * fresco:roundingBorderColor="@color/border_color" 描边的颜色。
     * />
     * </>
     */
    private static int screenWidth = 720;
    private static int screenHeight = 1280;
    private static int smallWidth = 200;

//    private static GenericDraweeHierarchy hierarchy;

    /**
     * 初始化
     */
    public static void init(Context context, File imageCacheDir) {
        init(context, imageCacheDir, false);
    }

    /**
     * 初始化
     *
     * @param openLog 打开日志
     */
    public static void init(Context context, File imageCacheDir, boolean openLog) {
        Fresco.initialize(context, ImagePipelineConfigFactory.getImagePipelineConfig(context, imageCacheDir, openLog));
        screenHeight = AndroidUtils.getScreenHeight(context);
        screenWidth = AndroidUtils.getScreenWidth(context);
    }

    /**
     * 初始化，使用OKHttp加载图片
     *
     * @param openLog 打开日志
     */
    public static void initOkHttp(Context context, File imageCacheDir, boolean openLog) {
        Fresco.initialize(context, ImagePipelineConfigFactory.getOkHttpImagePipelineConfig(context, imageCacheDir, openLog));
        screenHeight = AndroidUtils.getScreenHeight(context);
        screenWidth = AndroidUtils.getScreenWidth(context);
    }

//    public static void displayImage(SimpleDraweeView simpleDraweeView, String path) {
//        displayImage(simpleDraweeView, path, R.mipmap.default_pic_16x9, R.mipmap.default_pic_16x9, null, false);
//    }

    public static void displayImage(SimpleDraweeView simpleDraweeView, String path, int resDefault) {
        displayImage(simpleDraweeView, path, resDefault, resDefault, ScalingUtils.ScaleType.CENTER_CROP, false);
    }

    public static void displayImageSmall(SimpleDraweeView simpleDraweeView, String path, int resDefault) {
        displayImage(simpleDraweeView, path, resDefault, resDefault, null, true);
    }

    public static void displayImage(SimpleDraweeView simpleDraweeView, String path, int resLoading, int resFailure, ScalingUtils.ScaleType scaleType, boolean small) {
        if (CommonUtils.isEmpty(path)) {
            showRes(simpleDraweeView, resLoading);
            return;
        }
        try {
            setHierarchy(simpleDraweeView, resLoading, resFailure, null, scaleType);
            setController(simpleDraweeView, path, small);
        } catch (OutOfMemoryError e) {
            Logger.e(e);
        }

        //simpleDraweeView.setImageURI(path);
    }

//    /**
//     * 圆形图片
//     *
//     * @param path 图片路径
//     */
//    public static void displayCircleImage(SimpleDraweeView simpleDraweeView, String path) {
//        displayCircleImage(simpleDraweeView, path, 0, 0, R.mipmap.default_pic_1x1);
//    }


    /**
     * 圆形图片
     *
     * @param path       图片路径
     * @param resDefault 默认图片
     */
    public static void displayCircleImage(SimpleDraweeView simpleDraweeView, String path, int borderWidth, int borderColor, int resDefault) {
        displayCircleImage(simpleDraweeView, path, borderWidth, borderColor, resDefault, resDefault, null, true);
    }

    /**
     * 圆形图片
     */
    public static void displayCircleImage(SimpleDraweeView simpleDraweeView, String path, int borderWidth, int borderColor, int resLoading, int resFailure, ScalingUtils.ScaleType scaleType, boolean small) {
        RoundingParams roundingParams = new RoundingParams();
        roundingParams.setRoundAsCircle(true);
        if (borderWidth > 0) {
            roundingParams.setBorder(borderColor, borderWidth);
        }
        try {
            setHierarchy(simpleDraweeView, resLoading, resFailure, roundingParams, scaleType);
            setController(simpleDraweeView, path, small);
        } catch (OutOfMemoryError e) {
            Logger.e(e);
        }
        //simpleDraweeView.setImageURI(path);
    }

    /**
     * 圆角图片
     */
    public static void displayCornersImage(SimpleDraweeView simpleDraweeView, String path, int radius, int resLoading, boolean small) {
        displayCornersImage(simpleDraweeView, path, radius, resLoading, resLoading, ScalingUtils.ScaleType.FIT_XY, small);
    }

    /**
     * 圆角图片
     */
    public static void displayCornersImage(SimpleDraweeView simpleDraweeView, String path, int radius, int resLoading, int resFailure, ScalingUtils.ScaleType scaleType, boolean small) {
        RoundingParams roundingParams = new RoundingParams();
        roundingParams.setCornersRadius(radius);
        try {
            setHierarchy(simpleDraweeView, resLoading, resFailure, roundingParams, scaleType);
            setController(simpleDraweeView, path, small);
        } catch (OutOfMemoryError e) {
            Logger.e(e);
        }
        //simpleDraweeView.setImageURI(path);
    }

    public static void displayGif(SimpleDraweeView simpleDraweeView, String path) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(path)
                .setAutoPlayAnimations(true)
                .build();
        simpleDraweeView.setController(controller);
    }

    public static void setHierarchy(SimpleDraweeView simpleDraweeView, int resLoading, int resFailure, RoundingParams roundingParams, ScalingUtils.ScaleType scaleType) {
        GenericDraweeHierarchy hierarchy = simpleDraweeView.getHierarchy();
        hierarchy.setPlaceholderImage(resLoading);
        hierarchy.setFailureImage(resFailure);
        hierarchy.setFadeDuration(300);
        if (roundingParams != null) {
            hierarchy.setRoundingParams(roundingParams);
        }
        if (scaleType != null) {
            hierarchy.setActualImageScaleType(scaleType);
        }
    }

    public static void setController(SimpleDraweeView simpleDraweeView, String path, boolean small) {
        if (path == null) {
            path = "";
        }

        PipelineDraweeController controller =
                (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setImageRequest(getImageRequest(path, small))
                        .setOldController(simpleDraweeView.getController())
                        .setAutoPlayAnimations(true) //自动播放gif动画
                        .build();
        simpleDraweeView.setController(controller);
    }


    private static ImageRequest getImageRequest(String path, boolean small) {
        int width = small ? smallWidth : screenWidth;

        return ImageRequestBuilder.newBuilderWithSource(Uri.parse(path))
                .setResizeOptions(new ResizeOptions(width, width))
                //缩放,在解码前修改内存中的图片大小, 配合Downsampling可以处理所有图片,否则只能处理jpg,
                // 开启Downsampling:在初始化时设置.setDownsampleEnabled(true)
                //部分图片本身格式异常，以下方法会导致图片第一次显示失败
                //.setProgressiveRenderingEnabled(true)//支持图片渐进式加载
                //.setAutoRotateEnabled(true) //如果图片是侧着,可以自动旋转
                .build();
    }

    /**
     * 设置view大小。
     *
     * @param view  View。
     * @param width 指定宽。
     * @param width 指定高。
     */
    public static void requestLayout(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(width, height);
            view.setLayoutParams(layoutParams);
        } else {
            view.getLayoutParams().width = width;
            view.getLayoutParams().height = height;
            view.requestLayout();
        }
    }

    /**
     * 根据设计图宽高，计算出View在该屏幕上的实际宽高。
     *
     * @param width  设计图中View宽。
     * @param height 设计图中View高。
     */
    public static void calcRealSizeByDesign(View view, int width, int height, int designWidth) {
        int realWidth, realHeight;
        DisplayMetrics screenSize = getScreenSize(view.getContext());
        realWidth = screenSize.widthPixels * width / designWidth;
        realHeight = screenSize.widthPixels * height / width;
        requestLayout(view, realWidth, realHeight);
    }

    public static DisplayMetrics getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }

    /**
     * 显示http或者https远程图片。
     *
     * @param draweeView imageView。
     * @param url        连接地址。
     */
    public static void showUrl(SimpleDraweeView draweeView, String url) {
        try {
            draweeView.setImageURI(Uri.parse(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示一个本地图片。
     *
     * @param draweeView imageView。
     * @param path       路径。
     * @param width      实际宽。
     * @param height     实际高度。
     */
    public static void showFile(SimpleDraweeView draweeView, String path, int width, int height) {
        try {
            Uri uri = Uri.parse("file://" + path);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(width, height))
                    .build();
            AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(draweeView.getController())
                    .setImageRequest(request)
                    .build();
            draweeView.setController(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示本地图片。
     *
     * @param draweeView imageView。
     * @param path       路径。
     */
    public static void showFile(SimpleDraweeView draweeView, String path) {
        try {
            Uri uri = Uri.parse("file://" + path);
            draweeView.setImageURI(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示一个Res中的图片。
     *
     * @param draweeView ImageView。
     * @param resId      资源ID。
     */
    public static void showRes(SimpleDraweeView draweeView, @DrawableRes int resId) {
        showRes(draweeView, resId, null);
    }

    /**
     * 显示一个Res中的图片。
     *
     * @param draweeView ImageView。
     * @param resId      资源ID。
     */
    public static void showRes(SimpleDraweeView draweeView, @DrawableRes int resId, ScalingUtils.ScaleType scaleType) {
        try {
            // 你没看错，这里是三个///。
            draweeView.setImageURI(Uri.parse("res:///" + resId));
            if (scaleType != null) {
                draweeView.getHierarchy().setActualImageScaleType(scaleType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示content provider图片。
     *
     * @param draweeView image view。
     * @param path       路径。
     */
    public static void showContentProvider(SimpleDraweeView draweeView, String path) {
        try {
            draweeView.setImageURI(Uri.parse("content://" + path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示Assets中的图片。
     *
     * @param draweeView ImageView.
     * @param path       路径。
     */
    public static void showAsset(SimpleDraweeView draweeView, String path) {
        try {
            draweeView.setImageURI(Uri.parse("asset://" + path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 以高斯模糊显示。
     *
     * @param draweeView View。
     * @param url        url.
     * @param iterations 迭代次数，越大越魔化。
     * @param blurRadius 模糊图半径，必须大于0，越大越模糊。
     */
    public static void showUrlBlur(SimpleDraweeView draweeView, String url, int iterations, int blurRadius) {
        try {
            Uri uri = Uri.parse(url);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setPostprocessor(new IterativeBoxBlurPostProcessor(iterations, blurRadius))
                    .build();
            AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(draweeView.getController())
                    .setImageRequest(request)
                    .build();
            draweeView.setController(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearMemoryCaches() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();

// combines above two lines
        //imagePipeline.clearCaches();
    }

    public static void clearDiskCaches() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearDiskCaches();
    }

    public static void showInImageView(String imagePath, final String savePath, boolean small, final ImageView imageView) {
        ImageRequest imageRequest = getImageRequest(imagePath, small);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, null);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable final Bitmap bitmap) {
                //bitmap即为下载所得图片
                new android.os.Handler(imageView.getContext().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //BitmapUtils.saveBitmap2file(BitmapUtils.createBitmap(bitmap, 500, 500), savePath);
                            BitmapUtils.saveBitmap2file(bitmap, savePath);
                            imageView.setImageURI(Uri.fromFile(new File(savePath)));
                        } catch (Exception e) {
                            Logger.d(e);
                        } catch (OutOfMemoryError outOfMemoryError) {
                            Logger.d(outOfMemoryError);
                        }

                    }
                });
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {

            }
        }, CallerThreadExecutor.getInstance());
    }


}
