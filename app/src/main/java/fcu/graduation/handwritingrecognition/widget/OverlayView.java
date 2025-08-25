package fcu.graduation.handwritingrecognition.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OverlayView extends View {
    private Paint paint;
    private RectF rect;

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFFFFFF00); // 黃色 (ARGB)
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f); // 線條粗細

        // 先預設一個矩形，等下會在 onDraw 根據螢幕大小調整
        rect = new RectF();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        // 畫一個中央矩形框 (寬度=螢幕寬 60%，高度=螢幕高 40%)
        float rectWidth = width * 0.8f;
        float rectHeight = height * 0.8f;
        float left = (width - rectWidth) / 2;
        float top = (height - rectHeight) / 2;
        float right = left + rectWidth;
        float bottom = top + rectHeight;

        rect.set(left, top, right, bottom);
        canvas.drawRect(rect, paint);
    }
}
