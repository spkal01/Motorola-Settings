package com.motorola.settings.display.colors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class ColorTemperatureSeekBar extends SeekBar {
    public ColorTemperatureSeekBar(Context context) {
        this(context, (AttributeSet) null);
    }

    public ColorTemperatureSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ColorTemperatureSeekBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ColorTemperatureSeekBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void drawTickMarks(Canvas canvas) {
        Drawable tickMark = getTickMark();
        if (tickMark != null) {
            int max = getMax() - getMin();
            int i = 1;
            if (max > 1) {
                int intrinsicWidth = tickMark.getIntrinsicWidth();
                int intrinsicHeight = tickMark.getIntrinsicHeight();
                int i2 = intrinsicWidth >= 0 ? intrinsicWidth / 2 : 1;
                if (intrinsicHeight >= 0) {
                    i = intrinsicHeight / 2;
                }
                int i3 = -i2;
                int i4 = -i;
                tickMark.setBounds(i3, i4, i2, i);
                float width = ((float) ((getWidth() - this.mPaddingLeft) - this.mPaddingRight)) / ((float) max);
                int save = canvas.save();
                canvas.translate((float) this.mPaddingLeft, (float) (getHeight() / 2));
                for (int i5 = 0; i5 <= max; i5++) {
                    if (i5 == 0) {
                        tickMark.setBounds(0, i4, intrinsicWidth, i);
                    }
                    if (i5 == max) {
                        tickMark.setBounds(i3, i4, i3, i);
                    }
                    tickMark.draw(canvas);
                    canvas.translate(width, 0.0f);
                }
                canvas.restoreToCount(save);
            }
        }
    }
}
