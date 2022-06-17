package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;
import android.view.CrossWindowBlurListeners;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public final class EnableBlursPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private final boolean mBlurSupported;

    public String getPreferenceKey() {
        return "enable_blurs_on_windows";
    }

    public EnableBlursPreferenceController(Context context) {
        this(context, CrossWindowBlurListeners.CROSS_WINDOW_BLUR_SUPPORTED);
    }

    public EnableBlursPreferenceController(Context context, boolean z) {
        super(context);
        this.mBlurSupported = z;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "disable_window_blurs", ((Boolean) obj).booleanValue() ^ true ? 1 : 0);
        return true;
    }

    public boolean isAvailable() {
        return this.mBlurSupported;
    }

    public void updateState(Preference preference) {
        boolean z = false;
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "disable_window_blurs", 0) == 0) {
            z = true;
        }
        ((SwitchPreference) this.mPreference).setChecked(z);
    }

    /* access modifiers changed from: protected */
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.Global.putInt(this.mContext.getContentResolver(), "disable_window_blurs", 0);
        updateState((Preference) null);
    }
}
