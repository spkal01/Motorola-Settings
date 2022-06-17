package com.motorola.settings.gestures;

import android.graphics.drawable.Drawable;
import com.android.settingslib.widget.CandidateInfo;

public class ImageRadioCandidateInfo extends CandidateInfo {
    private final Drawable mIcon;
    private final String mKey;
    private final CharSequence mLabel;

    public ImageRadioCandidateInfo(CharSequence charSequence, Drawable drawable, String str, boolean z) {
        super(z);
        this.mLabel = charSequence;
        this.mIcon = drawable;
        this.mKey = str;
    }

    public CharSequence loadLabel() {
        return this.mLabel;
    }

    public Drawable loadIcon() {
        return this.mIcon;
    }

    public String getKey() {
        return this.mKey;
    }
}
