package com.example.wzx.demo1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wzx on 2017/2/6.
 */

public class VerticalTextView extends View {
    private TextPaint mTextPaint;
    private String mText;
    Rect text_bounds = new Rect();
    final static int DEFAULT_TEXT_SIZE = 15;
    final static int DEFAULT_TEXT_COLOR = 0xFF000000;
    private int direction;
    private String[] split;
    private int textHeight;

    public VerticalTextView(Context context) {
        super(context);
        initTextPaint();
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTextPaint();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.verticaltextview);
        CharSequence s = a.getString(R.styleable.verticaltextview_text);
        if (s != null)
            mText = s.toString();
        split = mText.split("\n");

        int textSize = a.getDimensionPixelOffset(R.styleable.verticaltextview_textSize, DEFAULT_TEXT_SIZE);
        if (textSize > 0)
            mTextPaint.setTextSize(textSize);

        mTextPaint.setColor(a.getColor(R.styleable.verticaltextview_textColor, DEFAULT_TEXT_COLOR));
        direction = a.getInt(R.styleable.verticaltextview_direction, 0);
        a.recycle();

        requestLayout();
        invalidate();
    }

    private final void initTextPaint() {
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(DEFAULT_TEXT_SIZE);
        mTextPaint.setColor(DEFAULT_TEXT_COLOR);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setText(String text) {
        mText = text;
        split = text.split("\n");
        requestLayout();
        invalidate();
    }

    public void setText(int resId) {
        if (resId > 0) {
            mText = getResources().getString(resId);
            split = mText.split("\n");
            requestLayout();
            invalidate();
        }
    }

    public void setTextSize(int size) {
        mTextPaint.setTextSize(size);
        requestLayout();
        invalidate();
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTextPaint.getTextBounds(mText, 0, mText.length(), text_bounds);
        setMeasuredDimension(
                measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
//            result = text_bounds.height() + getPaddingLeft() + getPaddingRight();
//            textHeight = text_bounds.height();
//            result = text_bounds.height();
            textHeight = (int) (mTextPaint.getFontMetrics().descent - mTextPaint.getFontMetrics().ascent);
            result = textHeight * split.length;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
//            result = text_bounds.width() + getPaddingTop() + getPaddingBottom();
//            result = text_bounds.width();
            float[] arr = new float[split.length];
            for (int i = 0; i < split.length; i++) {
                arr[i] = mTextPaint.measureText(split[i]);
            }
            result = getMaxValue(arr);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int getMaxValue(float[] arr) {
        float max = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return (int) max;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < split.length; i++) {
            int startX = 0;
            int startY = 0;
            int stopY = getHeight();
            Path path = new Path();
            if (direction == 0) {
                startX = (getWidth() / split.length >> 1) - (text_bounds.height() >> 1) + i * textHeight;
                path.moveTo(startX, startY);
                path.lineTo(startX, stopY);
            } else {
                startX = (getWidth() / split.length >> 1) + (text_bounds.height() >> 1) + i * textHeight;
                path.moveTo(startX, stopY);
                path.lineTo(startX, startY);
            }
            //从左到右绘制
            canvas.drawTextOnPath(split[split.length - 1 - i], path, 0, 0, mTextPaint);
        }
    }
}
