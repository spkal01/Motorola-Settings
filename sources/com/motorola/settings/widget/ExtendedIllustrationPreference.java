package com.motorola.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import androidx.preference.PreferenceViewHolder;
import com.airbnb.lottie.LottieAnimationView;
import com.android.internal.util.ArrayUtils;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1991R$raw;
import com.android.settings.R$styleable;
import com.android.settingslib.widget.IllustrationPreference;

public class ExtendedIllustrationPreference extends IllustrationPreference {
    private static final int[] EMPTY_ANIMATION_LIST = {C1983R$drawable.flip_camera_for_selfie, C1983R$drawable.nfc_detection_point, C1991R$raw.auto_awesome_battery_lottie, C1991R$raw.accessibility_timeout_banner, C1991R$raw.lottie_lift_to_check_phone, C1991R$raw.lottie_adaptive_brightness, C1991R$raw.lottie_tap_to_check_phone};
    private Drawable mImageDrawable;
    private int mImageFallbackResId;
    private int mImageResId;
    private Uri mImageUri;

    public ExtendedIllustrationPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public ExtendedIllustrationPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet);
    }

    public ExtendedIllustrationPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context, attributeSet);
    }

    public void setLottieAnimationResId(int i) {
        super.setLottieAnimationResId(i);
        setVisible(isImageResourceAvailable());
    }

    public void setImageUri(Uri uri) {
        super.setImageUri(uri);
        this.mImageUri = uri;
        setVisible(isImageResourceAvailable());
    }

    public void setFallbackResource(int i) {
        if (i != this.mImageFallbackResId) {
            this.mImageFallbackResId = i;
            notifyChanged();
        }
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        LottieAnimationView lottieAnimationView = (LottieAnimationView) preferenceViewHolder.findViewById(C1985R$id.lottie_view);
        if (lottieAnimationView != null) {
            lottieAnimationView.setFallbackResource(this.mImageFallbackResId);
        }
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.LottieAnimationView, 0, 0);
            this.mImageResId = obtainStyledAttributes.getResourceId(R$styleable.LottieAnimationView_lottie_rawRes, 0);
            this.mImageFallbackResId = obtainStyledAttributes.getResourceId(R$styleable.LottieAnimationView_lottie_fallbackRes, 0);
            obtainStyledAttributes.recycle();
            setVisible(isImageResourceAvailable());
        }
    }

    private boolean isImageResourceAvailable() {
        int i = this.mImageResId;
        if ((i == 0 || ArrayUtils.contains(EMPTY_ANIMATION_LIST, i)) && this.mImageFallbackResId == 0 && this.mImageDrawable == null && this.mImageUri == null) {
            return false;
        }
        return true;
    }
}
