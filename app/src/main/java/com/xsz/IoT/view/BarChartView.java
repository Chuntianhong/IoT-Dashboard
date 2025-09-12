package com.xsz.IoT.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class BarChartView extends View {
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

    private RectF barRect;
    private float barWidth, barHeight;
    
    public BarChartView(Context context) {
        super(context);
        init();
    }
    
    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(colorBackground);
        backgroundPaint.setStyle(Paint.Style.FILL);
        
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(colorProgress);
        progressPaint.setStyle(Paint.Style.FILL);
        
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
        
        barWidth = w * 0.25f;
        barHeight = h * 0.75f;
        
        float left = (w - barWidth) / 2f;
        float top = (h - barHeight) / 2f;
        
        barRect = new RectF(left, top, left + barWidth, top + barHeight);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (barRect == null) return;
        
        // Draw background bar
        canvas.drawRoundRect(barRect, 8, 8, backgroundPaint);
        
        // Calculate progress height
        float progress = (currentValue - minValue) / (maxValue - minValue);
        float progressHeight = barHeight * progress;
        
        // Draw progress bar
        RectF progressRect = new RectF(
            barRect.left,
            barRect.bottom - progressHeight,
            barRect.right,
            barRect.bottom
        );
        canvas.drawRoundRect(progressRect, 8, 8, progressPaint);
        
        // Title removed - already shown in card header
        
        // Draw current value
        canvas.drawText(String.format("%.1f", currentValue), getWidth() / 2f, barRect.centerY() + 10, valuePaint);
        
        // Draw unit
        if (!unit.isEmpty()) {
            canvas.drawText(unit, getWidth() / 2f, barRect.centerY() + 35, unitPaint);
        }
        
        // Draw min/max values
        //textPaint.setTextSize(14);
        canvas.drawText(String.format("%.0f", minValue), barRect.left - 30, barRect.bottom + 20, textPaint);
        canvas.drawText(String.format("%.0f", maxValue), barRect.right + 30, barRect.bottom + 20, textPaint);
        //textPaint.setTextSize(20);
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


