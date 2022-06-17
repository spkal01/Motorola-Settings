package com.android.settings.wallpaper;

import androidx.constraintlayout.widget.R$styleable;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;

public class WallpaperTypeSettings extends DashboardFragment {
    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "WallpaperTypeSettings";
    }

    public int getMetricsCategory() {
        return R$styleable.Constraint_layout_goneMarginRight;
    }

    public int getHelpResource() {
        return C1992R$string.help_uri_wallpaper;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.wallpaper_settings;
    }
}
