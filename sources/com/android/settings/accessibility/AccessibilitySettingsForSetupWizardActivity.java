package com.android.settings.accessibility;

import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.C1985R$id;
import com.android.settings.C1993R$style;
import com.android.settings.SettingsActivity;
import com.android.settings.SetupWizardUtils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.display.FontSizePreferenceFragmentForSetupWizard;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.util.ThemeHelper;

public class AccessibilitySettingsForSetupWizardActivity extends SettingsActivity {
    static final String CLASS_NAME_FONT_SIZE_SETTINGS_FOR_SUW = "com.android.settings.FontSizeSettingsForSetupWizardActivity";

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putCharSequence("activity_title", getTitle());
        super.onSaveInstanceState(bundle);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        setTitle(bundle.getCharSequence("activity_title"));
    }

    public boolean onNavigateUp() {
        onBackPressed();
        getWindow().getDecorView().sendAccessibilityEvent(32);
        return true;
    }

    public boolean onPreferenceStartFragment(PreferenceFragmentCompat preferenceFragmentCompat, Preference preference) {
        Bundle extras = preference.getExtras();
        if (extras == null) {
            extras = new Bundle();
        }
        int i = 0;
        extras.putInt("help_uri_resource", 0);
        extras.putBoolean("need_search_icon_in_action_bar", false);
        SubSettingLauncher arguments = new SubSettingLauncher(this).setDestination(preference.getFragment()).setArguments(extras);
        if (preferenceFragmentCompat instanceof Instrumentable) {
            i = ((Instrumentable) preferenceFragmentCompat).getMetricsCategory();
        }
        arguments.setSourceMetricsCategory(i).setExtras(SetupWizardUtils.copyLifecycleExtra(getIntent().getExtras(), new Bundle())).setTransitionType(2).launch();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        applyTheme();
        tryLaunchFontSizeSettings();
        findViewById(C1985R$id.content_parent).setFitsSystemWindows(false);
    }

    private void applyTheme() {
        int i;
        if (ThemeHelper.trySetDynamicColor(this)) {
            if (ThemeHelper.isSetupWizardDayNightEnabled(this)) {
                i = C1993R$style.SudDynamicColorThemeSettings_SetupWizard_DayNight;
            } else {
                i = C1993R$style.SudDynamicColorThemeSettings_SetupWizard;
            }
            setTheme(i);
            return;
        }
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
    }

    /* access modifiers changed from: package-private */
    public void tryLaunchFontSizeSettings() {
        if (WizardManagerHelper.isAnySetupWizard(getIntent()) && new ComponentName(getPackageName(), CLASS_NAME_FONT_SIZE_SETTINGS_FOR_SUW).equals(getIntent().getComponent())) {
            Bundle bundle = new Bundle();
            bundle.putInt("help_uri_resource", 0);
            bundle.putBoolean("need_search_icon_in_action_bar", false);
            SubSettingLauncher transitionType = new SubSettingLauncher(this).setDestination(FontSizePreferenceFragmentForSetupWizard.class.getName()).setArguments(bundle).setSourceMetricsCategory(0).setExtras(SetupWizardUtils.copyLifecycleExtra(getIntent().getExtras(), new Bundle())).setTransitionType(2);
            Log.d("A11ySettingsForSUW", "Launch font size settings");
            transitionType.launch();
            finish();
        }
    }
}
