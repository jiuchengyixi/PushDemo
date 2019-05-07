package com.common.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yuanht on 2017/10/8.
 */
@SuppressLint("AppCompatCustomView")
public class RotateTextView extends TextView {
    private int maxWidth;
    private float defaultTextSize = 0.0f;

    public RotateTextView(Context context) {
        super(context);
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //文字大小自适应
        TextPaint paint = getPaint();
        if (defaultTextSize == 0.0f) {
            defaultTextSize = getTextSize();
        }
        float textSize = defaultTextSize;
        paint.setTextSize(textSize);
        if (maxWidth == 0)
            maxWidth = getWidth();
        float textViewWidth = maxWidth - getPaddingLeft() - getPaddingRight();//不包含左右padding的空间宽度
        float textViewWidth1 = textViewWidth - paint.getFontSpacing() * 2;//不包含左右字体空间
        String text = getText().toString();
        float textWidth = paint.measureText(text);
        while (textWidth > textViewWidth1) {
            textSize--;
            paint.setTextSize(textSize);
            textWidth = paint.measureText(text);
            textViewWidth1 = textViewWidth - paint.getFontSpacing() * 2;
        }

        //倾斜度45,上下左右居中
        canvas.rotate(-36, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        super.onDraw(canvas);
    }
}
