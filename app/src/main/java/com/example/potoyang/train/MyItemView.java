package com.example.potoyang.train;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 71579 on 2017/3/29.
 */

public class MyItemView extends View {


    private int headColor = 0;
    private int tailColor = 0;
    private List<Integer> boxColor = new ArrayList<>();

    public MyItemView(Context context) {
        super(context);
    }

    public MyItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawHead(canvas, headColor);

        drawTail(canvas, tailColor);

        drawBox(canvas, boxColor);
    }

    public void setHeadColor(int headColor) {
        this.headColor = headColor;
    }

    public void setTailColor(int tailColor) {
        this.tailColor = tailColor;
    }

    public void setBoxColor(List<Integer> boxColor) {
        this.boxColor = boxColor;
    }


    /**
     * 绘制车厢头部
     *
     * @param canvas
     * @param color
     */
    private void drawHead(Canvas canvas, int color) {
        RectF head = new RectF(50, 10, 140, 130);

        Paint paint = new Paint();
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);

        canvas.drawArc(head, 180, 90, true, paint);
    }

    /**
     * 绘制车厢尾部
     *
     * @param canvas
     * @param color
     */
    private void drawTail(Canvas canvas, int color) {
        RectF tail = new RectF(430, 10, 520, 130);

        Paint paint = new Paint();
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);

        canvas.drawArc(tail, 0, -90, true, paint);
    }

    private void drawBox(Canvas canvas, List<Integer> boxColor) {
        int left = 120;
        Paint paint = new Paint();
        for (int i = 0; i < 6; i++) {
            paint.setStrokeWidth(8);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(boxColor.get(i));
            Rect box = new Rect(left, 10, left + 40, 70);
            canvas.drawRect(box, paint);
            left = left + 60;
        }
    }
}
