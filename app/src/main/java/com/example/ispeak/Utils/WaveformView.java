/*
 * Thomas M.
 */

package com.example.ispeak.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WaveformView extends View {
    private Paint paint;
    private byte[] waveform;

    public WaveformView(Context context) {
        super(context);
        init();
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
    }

    public void clearWaveform() {
        waveform = null;
        invalidate();
    }

    public void setWaveform(byte[] waveform) {
        this.waveform = waveform;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (waveform != null) {

            for (int i = 0; i < waveform.length - 1; i++) {
                float startX = i * getWidth() / waveform.length;
                float startY = (getHeight() / 2) + (waveform[i] * getHeight() / 2 / 128);
                float stopX = (i + 1) * getWidth() / waveform.length;
                float stopY = (getHeight() / 2) + (waveform[i + 1] * getHeight() / 2 / 128);
                canvas.drawLine(startX, startY, stopX, stopY, paint);
            }

        }
    }
}

