package com.motorola.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateXAnimation;
import android.widget.TextView;
import com.android.settings.C1982R$dimen;
import com.android.settings.R$styleable;
import com.android.settings.widget.ScrollToParentEditText;

public class PasswordView extends ScrollToParentEditText {
    private int mCircleColor;
    private float mCircleWidth;
    private Paint mPaint;
    private float mPasswordItemWidth;
    private int mPasswordNumber;
    private float mPasswordRadius;

    public PasswordView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initAttributeSet(context, attributeSet);
        initPaint();
        setInputType(128);
        setCursorVisible(false);
    }

    private void initAttributeSet(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.PasswordView);
        this.mPasswordNumber = obtainStyledAttributes.getInteger(R$styleable.PasswordView_passwordNumber, 6);
        this.mCircleColor = obtainStyledAttributes.getColor(R$styleable.PasswordView_passwordColor, 0);
        this.mPasswordRadius = (float) obtainStyledAttributes.getDimensionPixelSize(R$styleable.PasswordView_passwordRadius, 12);
        this.mCircleWidth = (float) context.getResources().getDimensionPixelSize(C1982R$dimen.password_view_circle_width);
        obtainStyledAttributes.recycle();
    }

    private void initPaint() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(this.mCircleColor);
        this.mPaint.setDither(true);
        this.mPaint.setAntiAlias(true);
    }

    public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
        PasswordView.super.setText(charSequence, bufferType);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.mPasswordItemWidth = ((float) getWidth()) / ((float) this.mPasswordNumber);
        drawPassword(canvas);
    }

    private void drawPassword(Canvas canvas) {
        int length = getText() == null ? 0 : getText().length();
        this.mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < length; i++) {
            float f = this.mPasswordItemWidth;
            canvas.drawCircle((((float) i) * f) + (f / 2.0f), ((float) getHeight()) / 2.0f, this.mPasswordRadius, this.mPaint);
        }
        int i2 = this.mPasswordNumber - length;
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(this.mCircleWidth);
        for (int i3 = 0; i3 < i2; i3++) {
            float f2 = this.mPasswordItemWidth;
            canvas.drawCircle((((float) length) * f2) + (((float) i3) * f2) + (f2 / 2.0f), ((float) getHeight()) / 2.0f, this.mPasswordRadius, this.mPaint);
        }
    }

    public void resetPasswordText() {
        TranslateXAnimation translateXAnimation = new TranslateXAnimation(-15.0f, 15.0f);
        translateXAnimation.setInterpolator(new LinearInterpolator());
        translateXAnimation.setDuration(60);
        translateXAnimation.setRepeatCount(4);
        translateXAnimation.setRepeatMode(2);
        startAnimation(translateXAnimation);
        setText("");
    }
}
