package com.motorola.settings.security.screenlock;

import android.content.res.ColorStateList;
import com.android.settings.SettingsActivity;
import com.android.settingslib.Utils;

public class SettingsSetUpActivity extends SettingsActivity {
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().setStatusBarColor(getBackgroundColor());
    }

    /* access modifiers changed from: protected */
    public int getBackgroundColor() {
        ColorStateList colorAttr = Utils.getColorAttr(this, 16842836);
        if (colorAttr != null) {
            return colorAttr.getDefaultColor();
        }
        return 0;
    }
}
