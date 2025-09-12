package com.xsz.IoT.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RoundGaugeView extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private Paint valuePaint;
    private Paint unitPaint;

    private float currentValue = 0;
    private float minValue = 0;
    private float maxValue = 100;
    private String unit = "";

    private static final int colorBackground = 0xff4d5a66;
    private static final int colorProgress = 0xff727cf5;
    private static final int colorUnit = 0xffaab8be;
    private static final int colorText = 0xffaab8be;
    private static final int colorValue = 0xff0acf97;

    private RectF gaugeRect;
    private float centerX, centerY, radius;

    public RoundGaugeView(Context context) {
        super(context);
        init();
    }

    public RoundGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(colorBackground);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(20);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(colorProgress);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(20);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(colorText);
        textPaint.setTextSize(20);
        textPaint.setTextAlign(Paint.Align.CENTER);

        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setColor(colorValue);
        valuePaint.setTextSize(40);
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setFakeBoldText(true);

        unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unitPaint.setColor(colorUnit);
        unitPaint.setTextSize(24);
        unitPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        centerX = w / 2f;
        centerY = h / 2f;
        radius = Math.min(w, h) / 2f - 40;

        gaugeRect = new RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (gaugeRect == null) return;

        // Draw background arc
        canvas.drawArc(gaugeRect, 135, 270, false, backgroundPaint);

        // Calculate progress angle
        float progress = (currentValue - minValue) / (maxValue - minValue);
        float sweepAngle = 270 * progress;

        // Draw progress arc
        canvas.drawArc(gaugeRect, 135, sweepAngle, false, progressPaint);


        // Draw current value
        canvas.drawText(String.format("%.1f", currentValue), centerX, centerY + 10, valuePaint);

        // Draw unit
        if (!unit.isEmpty()) {
            canvas.drawText(unit, centerX, centerY + 35, unitPaint);
        }

        // Draw min/max values
        //textPaint.setTextSize(16);
        canvas.drawText(String.format("%.0f", minValue), centerX - radius + 20, centerY + radius + 20, textPaint);
        canvas.drawText(String.format("%.0f", maxValue), centerX + radius - 20, centerY + radius + 20, textPaint);
        //textPaint.setTextSize(24);
    }

    public void setValue(float value) {
        this.currentValue = Math.max(minValue, Math.min(maxValue, value));
        invalidate();
    }

    public void setRange(float min, float max) {
        this.minValue = min;
        this.maxValue = max;
        invalidate();
    }

    public void setUnit(String unit) {
        this.unit = unit;
        invalidate();
    }

    public void setProgressColor(int color) {
        progressPaint.setColor(color);
        invalidate();
    }
}


