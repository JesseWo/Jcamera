package com.example.wzx.demo1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wzx on 2017/2/8.
 */

public class CustomView extends View {

    private TextPaint mTextPaint;
    private final String TEXT = "你好啊";
    private Rect rect;
    private Paint paint;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(80);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        rect = new Rect();

        paint = new Paint();
        paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTextPaint.getTextBounds(TEXT, 0, TEXT.length(), rect);
        canvas.drawText(TEXT, rect.width() / 2, rect.height(), mTextPaint);

        int startX = 0;
        int startY = 0;
        int stopY = getHeight();
        int stopX = getWidth();
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(stopX, startY);
        canvas.drawTextOnPath(TEXT, path, 0, 0, mTextPaint);

    }
}
