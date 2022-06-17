package com.motorola.settings.display;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.android.settings.C1981R$color;
import com.android.settings.SettingsActivity;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class MotoDisplaySettingsActivity extends SettingsActivity {

    public static class DisplaySizeSettingsActivity extends MotoDisplaySettingsActivity {
    }

    public static class FontSizeSettingsActivity extends MotoDisplaySettingsActivity {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mAppBarLayout.setExpanded(false);
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        int i = C1981R$color.settings_action_bar_bg;
        collapsingToolbarLayout.setBackgroundColor(getColor(i));
        getActionBar().setBackgroundDrawable(new ColorDrawable(getColor(i)));
        getWindow().setStatusBarColor(getColor(C1981R$color.settings_status_bar_bg));
        getWindow().getDecorView().setBackgroundColor(getColor(C1981R$color.settings_display_window_bg));
    }

    public Intent getIntent() {
        Bundle bundle;
        Intent intent = new Intent(super.getIntent());
        Bundle bundleExtra = intent.getBundleExtra(":settings:show_fragment_args");
        if (bundleExtra != null) {
            bundle = new Bundle(bundleExtra);
        } else {
            bundle = new Bundle();
        }
        bundle.putBoolean("need_search_icon_in_action_bar", false);
        bundle.putInt("help_uri_resource", 0);
        intent.putExtra(":settings:show_fragment_args", bundle);
        return intent;
    }
}
