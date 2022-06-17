package com.google.android.setupdesign.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import com.google.android.setupdesign.R$color;

public class IconUniformityAppImageView extends ImageView {
    private static final Float APPS_ICON_RADIUS_MULTIPLIER = Float.valueOf(0.2f);
    private static final Float LEGACY_SIZE_SCALE_FACTOR;
    private static final Float LEGACY_SIZE_SCALE_MARGIN_FACTOR;
    private static final boolean ON_L_PLUS = (Build.VERSION.SDK_INT >= 21);
    private int backdropColorResId = 0;
    private final GradientDrawable backdropDrawable = new GradientDrawable();

    static {
        Float valueOf = Float.valueOf(0.75f);
        LEGACY_SIZE_SCALE_FACTOR = valueOf;
        LEGACY_SIZE_SCALE_MARGIN_FACTOR = Float.valueOf((1.0f - valueOf.floatValue()) / 2.0f);
    }

    public IconUniformityAppImageView(Context context) {
        super(context);
    }

    public IconUniformityAppImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public IconUniformityAppImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @TargetApi(23)
    public IconUniformityAppImageView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.backdropColorResId = R$color.sud_uniformity_backdrop_color;
        this.backdropDrawable.setColor(ContextCompat.getColor(getContext(), this.backdropColorResId));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean z = ON_L_PLUS;
        super.onDraw(canvas);
    }
}
