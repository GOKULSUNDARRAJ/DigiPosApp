package com.app.digiposfinalapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {
    private Paint paint = new Paint();
    private Paint textPaint = new Paint();
    private float[] values = {28, 18, 4, 10}; // Pie chart data
    private String[] labels = {"", "", "", ""}; // Labels for each section
    private int[] colors = {Color.MAGENTA, Color.RED, Color.GRAY, Color.BLUE}; // Section colors

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPieChart(canvas);
        drawCenterText(canvas);
    }

    private void drawPieChart(Canvas canvas) {
        float total = 0;
        for (float value : values) {
            total += value;
        }

        float startAngle = 0;
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float radius = Math.min(centerX, centerY) * 0.8f; // Use 80% of the smaller dimension
        float innerRadius = radius * 0.75f; // Create inner radius for doughnut chart

        RectF outerRect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        RectF innerRect = new RectF(centerX - innerRadius, centerY - innerRadius, centerX + innerRadius, centerY + innerRadius);

        for (int i = 0; i < values.length; i++) {
            float sweepAngle = (values[i] / total) * 360;
            paint.setColor(colors[i]);

            // Draw the doughnut (outer arc)
            canvas.drawArc(outerRect, startAngle, sweepAngle, true, paint);

            // Cut out the center (inner arc) for doughnut effect
            paint.setColor(Color.WHITE);
            canvas.drawArc(innerRect, startAngle, sweepAngle, true, paint);

            // Add label near the middle of each section
            float angle = startAngle + sweepAngle / 2;
            float labelX = centerX + (float) (Math.cos(Math.toRadians(angle)) * radius * 0.7);
            float labelY = centerY + (float) (Math.sin(Math.toRadians(angle)) * radius * 0.7);
            canvas.drawText(labels[i], labelX, labelY, textPaint);

            startAngle += sweepAngle;
        }
    }

    private void drawCenterText(Canvas canvas) {
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;

        // Draw "Sales in million" text at the center of the chart
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60);
        canvas.drawText("", centerX, centerY, textPaint);
    }
}
