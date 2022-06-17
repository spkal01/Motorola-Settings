package com.motorola.settings.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;

public class ManageApplicationsHeaderView extends LinearLayout {
    private final ImageView mPreview;
    private final TextView mTitle;

    public ManageApplicationsHeaderView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ManageApplicationsHeaderView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ManageApplicationsHeaderView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setOrientation(1);
        LayoutInflater.from(context).inflate(C1987R$layout.extended_manage_applications_header, this, true);
        this.mTitle = (TextView) findViewById(C1985R$id.title);
        this.mPreview = (ImageView) findViewById(C1985R$id.preview);
    }

    public final void setHeaderTitle(int i) {
        TextView textView = this.mTitle;
        if (textView != null) {
            textView.setText(i);
        }
    }

    public final void setHeaderBackground(int i) {
        ImageView imageView = this.mPreview;
        if (imageView != null && i != 0) {
            imageView.setBackgroundResource(i);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Drawable background = this.mPreview.getBackground();
        if (background instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) background;
            for (int i = 0; i < layerDrawable.getNumberOfLayers(); i++) {
                Drawable drawable = layerDrawable.getDrawable(i);
                if (drawable instanceof AnimationDrawable) {
                    AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                    if (!animationDrawable.isRunning()) {
                        animationDrawable.start();
                    }
                }
            }
        }
    }
}
