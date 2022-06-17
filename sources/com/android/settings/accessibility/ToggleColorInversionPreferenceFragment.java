package com.android.settings.accessibility;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.settings.C1991R$raw;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.widget.SettingsMainSwitchPreference;
import java.util.ArrayList;

public class ToggleColorInversionPreferenceFragment extends ToggleFeaturePreferenceFragment {
    private final Handler mHandler = new Handler();
    private SettingsContentObserver mSettingsContentObserver;

    public int getMetricsCategory() {
        return 1817;
    }

    /* access modifiers changed from: protected */
    public void onPreferenceToggled(String str, boolean z) {
        AccessibilityStatsLogUtils.logAccessibilityServiceEnabled(this.mComponentName, z);
        Settings.Secure.putInt(getContentResolver(), "accessibility_display_inversion_enabled", z ? 1 : 0);
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.accessibility_color_inversion_settings;
    }

    /* access modifiers changed from: protected */
    public void onRemoveSwitchPreferenceToggleSwitch() {
        super.onRemoveSwitchPreferenceToggleSwitch();
        this.mToggleServiceSwitchPreference.setOnPreferenceClickListener((Preference.OnPreferenceClickListener) null);
    }

    /* access modifiers changed from: protected */
    public void updateToggleServiceTitle(SettingsMainSwitchPreference settingsMainSwitchPreference) {
        settingsMainSwitchPreference.setTitle(C1992R$string.accessibility_display_inversion_switch_title);
    }

    /* access modifiers changed from: protected */
    public void updateShortcutTitle(ShortcutPreference shortcutPreference) {
        shortcutPreference.setTitle(C1992R$string.accessibility_display_inversion_shortcut_title);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mComponentName = AccessibilityShortcutController.COLOR_INVERSION_COMPONENT_NAME;
        this.mPackageName = getText(C1992R$string.accessibility_display_inversion_preference_title);
        this.mHtmlDescription = getText(C1992R$string.accessibility_display_inversion_preference_subtitle);
        this.mImageUri = new Uri.Builder().scheme("android.resource").authority(getPrefContext().getPackageName()).appendPath(String.valueOf(C1991R$raw.accessibility_color_inversion_banner)).build();
        ArrayList arrayList = new ArrayList(1);
        arrayList.add("accessibility_display_inversion_enabled");
        this.mSettingsContentObserver = new SettingsContentObserver(this.mHandler, arrayList) {
            public void onChange(boolean z, Uri uri) {
                ToggleColorInversionPreferenceFragment.this.updateSwitchBarToggleSwitch();
            }
        };
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        updateFooterPreference();
        return onCreateView;
    }

    private void updateFooterPreference() {
        String string = getPrefContext().getString(C1992R$string.accessibility_color_inversion_about_title);
        String string2 = getPrefContext().getString(C1992R$string.f54xb513e1d2);
        this.mFooterPreferenceController.setIntroductionTitle(string);
        this.mFooterPreferenceController.setupHelpLink(getHelpResource(), string2);
        this.mFooterPreferenceController.displayPreference(getPreferenceScreen());
    }

    public void onResume() {
        super.onResume();
        updateSwitchBarToggleSwitch();
        this.mSettingsContentObserver.register(getContentResolver());
    }

    public void onPause() {
        this.mSettingsContentObserver.unregister(getContentResolver());
        super.onPause();
    }

    public int getHelpResource() {
        return C1992R$string.help_url_color_inversion;
    }

    /* access modifiers changed from: package-private */
    public int getUserShortcutTypes() {
        return AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName);
    }

    /* access modifiers changed from: protected */
    public void updateSwitchBarToggleSwitch() {
        boolean z = false;
        if (Settings.Secure.getInt(getContentResolver(), "accessibility_display_inversion_enabled", 0) == 1) {
            z = true;
        }
        if (this.mToggleServiceSwitchPreference.isChecked() != z) {
            this.mToggleServiceSwitchPreference.setChecked(z);
        }
    }
}
