package com.xsz.IoT.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.xsz.IoT.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrendChartView extends View {
    private Paint linePaint;
    private Paint gridPaint;
    private Paint textPaint;
    private Paint valuePaint;
    private Paint unitPaint;
    
    private List<Float> dataPoints;
    private float minValue = 0;
    private float maxValue = 100;
    private String unit = "";
    private static final int colorBackground = 0xff4d5a66;
    private static final int colorProgress = 0xff727cf5;
    private static final int colorUnit = 0xffaab8be;
    private static final int colorText = 0xffaab8be;
    private static final int colorValue = 0xff0acf97;

    private int MAX_DATA_POINTS = 200;

    // Scrolling behavior variables
    private boolean isScrollingMode = false;  // Whether we're in scrolling mode (phase 2/3)
    private final List<Float> leftHalfData = new ArrayList<>();  // Data points for left half of screen
    private final List<Float> rightHalfData = new ArrayList<>(); // Data points for right half of screen
    
    public TrendChartView(Context context) {
        super(context);
        init();
    }
    
    public TrendChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public TrendChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        dataPoints = new ArrayList<>();
        
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(colorProgress);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);
        
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(colorBackground);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);
        
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        float width = getWidth();
        float height = getHeight();
        float padding = 40;
        
        // Always draw grid lines and axis labels regardless of data state
        // Draw grid lines
        for (int i = 0; i <= 4; i++) {
            float y = padding + (height - 2 * padding) * i / 4;
            canvas.drawLine(padding, y, width - padding, y, gridPaint);
        }
        
        // Draw min/max values
        textPaint.setTextSize(12);
        canvas.drawText(String.format("%.0f", minValue), padding - 20, height - padding, textPaint);
        canvas.drawText(String.format("%.0f", maxValue), padding - 20, padding + 10, textPaint);
        textPaint.setTextSize(16);
        
        // Check if we have any data to draw
        boolean hasData = (!isScrollingMode && !dataPoints.isEmpty()) || 
                         (isScrollingMode && (!leftHalfData.isEmpty() || !rightHalfData.isEmpty()));
        
        if (!hasData) return;
        
        // Draw trend line
        if (getDataPointsCount() > 1) {
            Path path = getPath(width, padding, height);
            canvas.drawPath(path, linePaint);
        }
        
        // Draw current value
        float currentValue = getLastValue();
        if (!Float.isNaN(currentValue)) {
            String valueText = String.format("%.1f", currentValue);
            
            // Calculate text widths for proper positioning
            float valueWidth = valuePaint.measureText(valueText);
            float unitWidth = !unit.isEmpty() ? unitPaint.measureText(unit) : 0;
            
            // Calculate starting position to center both texts together
            float totalWidth = valueWidth + (unitWidth > 0 ? unitWidth + 8 : 0); // 8px spacing
            float startX = (width - totalWidth) / 2f;
            
            // Draw value text
            valuePaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(valueText, startX, height - 8, valuePaint);
            
            // Draw unit text to the right of value
            if (!unit.isEmpty()) {
                unitPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(unit, startX + valueWidth + 8, height - 8, unitPaint);
            }
            
            // Reset text alignment for other uses
            valuePaint.setTextAlign(Paint.Align.CENTER);
            unitPaint.setTextAlign(Paint.Align.CENTER);
        }
    }

    @NonNull
    private Path getPath(float width, float padding, float height) {
        Path path = new Path();
        float chartWidth = width - 2 * padding;
        float halfWidth = chartWidth / 2f;
        
        if (dataPoints.isEmpty() && leftHalfData.isEmpty() && rightHalfData.isEmpty()) {
            return path;
        }
        
        if (!isScrollingMode) {
            // Phase 1: Normal left-to-right plotting (0 to maxDataPoints)
            float xStep = chartWidth / (MAX_DATA_POINTS - 1);
            for (int i = 0; i < dataPoints.size(); i++) {
                float x = padding + i * xStep;
                float normalizedValue = (dataPoints.get(i) - minValue) / (maxValue - minValue);
                float y = height - padding - normalizedValue * (height - 2 * padding);
                
                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
        } else {
            // Phase 2/3: Oscilloscope scrolling mode
            boolean hasLeftData = !leftHalfData.isEmpty();
            boolean hasRightData = !rightHalfData.isEmpty();
            
            // Draw left half data (shifted old data)
            if (hasLeftData) {
                float leftXStep = halfWidth / (MAX_DATA_POINTS / 2 - 1);
                for (int i = 0; i < leftHalfData.size(); i++) {
                    float x = padding + i * leftXStep;
                    float normalizedValue = (leftHalfData.get(i) - minValue) / (maxValue - minValue);
                    float y = height - padding - normalizedValue * (height - 2 * padding);
                    
                    if (i == 0 && !hasRightData) {
                        path.moveTo(x, y);
                    } else if (i == 0) {
                        path.moveTo(x, y);
                    } else {
                        path.lineTo(x, y);
                    }
                }
            }
            
            // Draw right half data (new data from center)
            if (hasRightData) {
                float centerX = padding + halfWidth;
                float rightXStep = halfWidth / ((float) MAX_DATA_POINTS / 2 - 1);
                
                for (int i = 0; i < rightHalfData.size(); i++) {
                    float x = centerX + i * rightXStep;
                    float normalizedValue = (rightHalfData.get(i) - minValue) / (maxValue - minValue);
                    float y = height - padding - normalizedValue * (height - 2 * padding);
                    
                    if (i == 0 && hasLeftData && !leftHalfData.isEmpty()) {
                        // Connect from last point of left half to first point of right half
                        path.lineTo(x, y);
                    } else if (i == 0) {
                        path.moveTo(x, y);
                    } else {
                        path.lineTo(x, y);
                    }
                }
            }
        }
        
        return path;
    }

    public void addDataPoint(float value) {
        if (!isScrollingMode) {
            // Phase 1: Normal left-to-right filling
            dataPoints.add(value);
            
            if (dataPoints.size() >= MAX_DATA_POINTS) {
                // Switch to scrolling mode: move last half of data to left half
                isScrollingMode = true;
                
                // Take the last 50 points and move them to leftHalfData
                int halfPoint = MAX_DATA_POINTS / 2;
                leftHalfData.clear();
                for (int i = halfPoint; i < dataPoints.size(); i++) {
                    leftHalfData.add(dataPoints.get(i));
                }
                
                // Clear dataPoints and start rightHalfData with current value
                dataPoints.clear();
                rightHalfData.clear();
                rightHalfData.add(value); // Add the current value as first point in right half
                
                // Debug log to verify transition
                android.util.Log.d("TrendChart", getContext().getString(R.string.debug_switched_to_scrolling, leftHalfData.size(), rightHalfData.size()));
            }
        } else {
            // Phase 2/3: Oscilloscope scrolling mode
            rightHalfData.add(value);
            
            // Check if right half is full (reached right edge)
            if (rightHalfData.size() >= MAX_DATA_POINTS / 2) {
                // Shift: move current rightHalfData to leftHalfData
                leftHalfData.clear();
                leftHalfData.addAll(rightHalfData);
                
                // Start new rightHalfData from center with current value
                rightHalfData.clear();
                rightHalfData.add(value);
                
                // Debug log to verify oscilloscope shift
                android.util.Log.d("TrendChart", getContext().getString(R.string.debug_oscilloscope_shift, leftHalfData.size(), rightHalfData.size()));
            }
        }
        
        invalidate();
    }
    
    public void setCurrentValue(float value) {
        // Only add if this is a new value (not the same as the last one)
        float lastValue = getLastValue();
        // Use a smaller threshold relative to the range, or always add if range is small
        float threshold = Math.max(0.01f, (maxValue - minValue) * 0.001f);
        if (Float.isNaN(lastValue) || Math.abs(lastValue - value) > threshold) {
            addDataPoint(value);
        }
    }
    
    public void forceAddDataPoint(float value) {
        // Force add data point without filtering - useful for testing or continuous updates
        addDataPoint(value);
    }
    
    private float getLastValue() {
        if (!isScrollingMode && !dataPoints.isEmpty()) {
            return dataPoints.get(dataPoints.size() - 1);
        } else if (isScrollingMode && !rightHalfData.isEmpty()) {
            return rightHalfData.get(rightHalfData.size() - 1);
        } else if (isScrollingMode && !leftHalfData.isEmpty()) {
            return leftHalfData.get(leftHalfData.size() - 1);
        }
        return Float.NaN;
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

    public void setLineColor(int color) {
        linePaint.setColor(color);
        invalidate();
    }
    
    public void clearData() {
        dataPoints.clear();
        leftHalfData.clear();
        rightHalfData.clear();
        isScrollingMode = false;
        invalidate();
    }
    
    public int getDataPointsCount() {
        if (!isScrollingMode) {
            return dataPoints.size();
        } else {
            return leftHalfData.size() + rightHalfData.size();
        }
    }
}

