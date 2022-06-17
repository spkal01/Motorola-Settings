package com.android.settings.notification;

import android.content.Intent;
import com.android.settings.C1992R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.notification.RedactionInterstitial;

public class RedactionSettingsStandalone extends SettingsActivity {
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", RedactionInterstitial.RedactionInterstitialFragment.class.getName()).putExtra("extra_prefs_show_button_bar", true).putExtra("extra_prefs_set_back_text", (String) null).putExtra("extra_prefs_set_next_text", getString(C1992R$string.app_notifications_dialog_done));
        return intent;
    }

    /* access modifiers changed from: protected */
    public boolean isValidFragment(String str) {
        return RedactionInterstitial.RedactionInterstitialFragment.class.getName().equals(str);
    }
}
