package com.zzs.networkcalculatorclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * View to display expression.
 */
public class ExpressionView extends View {
    private static final int DIVISION_TOP = 5;
    private String mExpression;
    private Paint mPaint;
    private int mDivisionWidth;
    private int mTextHeight;
    private boolean mHasDenominator;
    private int mHalfHeight;
    private static final int DRAW_START = 0;
    private int mNumeratorDrawStart = 0;
    private int mDenominatorDrawStart = 0;
    private int mHalfCharHeight;
    private String mNumerator, mDenominator;

    public ExpressionView(Context context) {
        super(context);
        init(context);
    }

    public ExpressionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpressionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ExpressionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.TextSize1));

        mTextHeight = (int) (mPaint.getFontMetrics().descent - mPaint.getFontMetrics().ascent);
        mHalfCharHeight = mTextHeight / 2;
    }

    public void setExpression(String expression) {
        mExpression = expression;
        if (null == expression) {
            return;
        }

        if (expression.contains("/")) {
            mHasDenominator = true;
            int index = expression.indexOf("/");
            mNumerator = expression.substring(0, index);
            mDenominator = expression.substring(index+1, expression.length());
            //calculate division sign width.
            int numeratorWidth = getWidthWithoutPowerSymbol(mNumerator);
            int denominatorWidth = getWidthWithoutPowerSymbol(mDenominator);
            mDivisionWidth = (denominatorWidth > numeratorWidth) ? denominatorWidth : numeratorWidth;

            mNumeratorDrawStart = (mDivisionWidth - numeratorWidth) / 2;
            mDenominatorDrawStart = (mDivisionWidth - denominatorWidth) / 2;

        } else {
            mHasDenominator = false;
            mNumerator = expression;
            int numeratorWidth = getWidthWithoutPowerSymbol(mNumerator);
            mDenominator = "";
        }
    }
    private int getWidthWithoutPowerSymbol(String str) {
        String numeratorWithoutPowerSymbol = str.replaceAll("^", "");
        return (int) mPaint.measureText(numeratorWithoutPowerSymbol);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mExpression != null) {
            // Draw numerator
            drawExpression(canvas, mNumerator, mNumeratorDrawStart, mHalfHeight - DIVISION_TOP);
            if (mHasDenominator) {
                // Draw division
                canvas.drawLine(DRAW_START, mHalfHeight + DIVISION_TOP, mDivisionWidth,
                        mHalfHeight + DIVISION_TOP, mPaint);
                // Draw denominator
                int denominatorHeight = mHalfHeight + mTextHeight + DIVISION_TOP;
                drawExpression(canvas, mDenominator, mDenominatorDrawStart, denominatorHeight);
            }
        }

    }

    private void drawExpression(Canvas canvas, String expression, int drawStartX, int drawStartY) {
        int size = expression.length();
        boolean powerStart = false;
        float charX = drawStartX;
        for (int i = 0; i < size; i++) {
            char ch = expression.charAt(i);
            String charString = String.valueOf(ch);
            if ('^' == ch) {
                powerStart = true;
                continue;
            }
            if (powerStart && ch >= '0' && ch <= '9') {
                canvas.drawText(charString, DRAW_START + charX, drawStartY - mHalfCharHeight,
                        mPaint);
            } else {
                powerStart = false;
                canvas.drawText(charString, charX, drawStartY, mPaint);
            }
            float charWidth = mPaint.measureText(charString);
            charX += charWidth;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mHalfHeight = h / 2;
    }
}
