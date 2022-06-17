package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1985R$id;
import com.android.settingslib.utils.ColorUtil;

public class MutableGearPreference extends GearPreference {
    private Context mContext;
    private int mDisabledAlphaValue;
    private ImageView mGear;

    public MutableGearPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        this.mDisabledAlphaValue = (int) (ColorUtil.getDisabledAlpha(context) * 255.0f);
    }

    public void setGearEnabled(boolean z) {
        ImageView imageView = this.mGear;
        if (imageView != null) {
            imageView.setEnabled(z);
            this.mGear.setImageAlpha(z ? 255 : this.mDisabledAlphaValue);
        }
        this.mGearState = z;
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mGear = (ImageView) preferenceViewHolder.findViewById(C1985R$id.settings_button);
        setGearEnabled(this.mGearState);
    }
}
