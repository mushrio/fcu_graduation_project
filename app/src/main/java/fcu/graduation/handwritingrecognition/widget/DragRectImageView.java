package fcu.graduation.handwritingrecognition.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

public class DragRectImageView extends AppCompatImageView {
    private float startX, startY, endX, endY;
    private boolean drawing = false;
    private RectF rect = new RectF();
    private Paint rectPaint;

    public DragRectImageView(Context context) {
        super(context);
        init();
    }

    public DragRectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragRectImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        rectPaint = new Paint();
        rectPaint.setColor(0x55FF0000); // 半透明紅色
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(5f);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                endX = startX;
                endY = startY;
                drawing = true;
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                endX = event.getX();
                endY = event.getY();
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
                endX = event.getX();
                endY = event.getY();
                drawing = false;
                invalidate();
                // 這裡可以加 callback 把座標傳出去
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (drawing || (startX != endX && startY != endY)) {
            rect.set(
                    Math.min(startX, endX),
                    Math.min(startY, endY),
                    Math.max(startX, endX),
                    Math.max(startY, endY)
            );
            canvas.drawRect(rect, rectPaint);
        }
    }

    public RectF getLastRect() {
        return new RectF(
                Math.min(startX, endX),
                Math.min(startY, endY),
                Math.max(startX, endX),
                Math.max(startY, endY)
        );
    }
}
