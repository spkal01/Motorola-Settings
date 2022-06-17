package com.google.android.setupdesign.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.util.BuildCompatUtils;
import com.google.android.setupdesign.R$styleable;

public class IntrinsicSizeFrameLayout extends FrameLayout {
    private int intrinsicHeight = 0;
    private int intrinsicWidth = 0;

    public IntrinsicSizeFrameLayout(Context context) {
        super(context);
        init(context, (AttributeSet) null, 0);
    }

    public IntrinsicSizeFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet, 0);
    }

    @TargetApi(11)
    public IntrinsicSizeFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet, i);
    }

    private void init(Context context, AttributeSet attributeSet, int i) {
        if (!isInEditMode()) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.SudIntrinsicSizeFrameLayout, i, 0);
            this.intrinsicHeight = obtainStyledAttributes.getDimensionPixelSize(R$styleable.SudIntrinsicSizeFrameLayout_android_height, 0);
            this.intrinsicWidth = obtainStyledAttributes.getDimensionPixelSize(R$styleable.SudIntrinsicSizeFrameLayout_android_width, 0);
            obtainStyledAttributes.recycle();
            if (BuildCompatUtils.isAtLeastS()) {
                PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(context);
                PartnerConfig partnerConfig = PartnerConfig.CONFIG_CARD_VIEW_INTRINSIC_HEIGHT;
                if (partnerConfigHelper.isPartnerConfigAvailable(partnerConfig)) {
                    this.intrinsicHeight = (int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig);
                }
                PartnerConfigHelper partnerConfigHelper2 = PartnerConfigHelper.get(context);
                PartnerConfig partnerConfig2 = PartnerConfig.CONFIG_CARD_VIEW_INTRINSIC_WIDTH;
                if (partnerConfigHelper2.isPartnerConfigAvailable(partnerConfig2)) {
                    this.intrinsicWidth = (int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig2);
                }
            }
        }
    }

    public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (BuildCompatUtils.isAtLeastS() && this.intrinsicHeight == 0 && this.intrinsicWidth == 0) {
            layoutParams.width = -1;
            layoutParams.height = -1;
        }
        super.setLayoutParams(layoutParams);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(getIntrinsicMeasureSpec(i, this.intrinsicWidth), getIntrinsicMeasureSpec(i2, this.intrinsicHeight));
    }

    private int getIntrinsicMeasureSpec(int i, int i2) {
        if (i2 <= 0) {
            return i;
        }
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        if (mode == 0) {
            return View.MeasureSpec.makeMeasureSpec(this.intrinsicHeight, 1073741824);
        }
        return mode == Integer.MIN_VALUE ? View.MeasureSpec.makeMeasureSpec(Math.min(size, this.intrinsicHeight), 1073741824) : i;
    }
}
