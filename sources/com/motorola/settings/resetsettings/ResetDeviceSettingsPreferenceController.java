package com.motorola.settings.resetsettings;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class ResetDeviceSettingsPreferenceController extends BasePreferenceController {
    private static final String RESET_DEVICE_SETTINGS_KEY = "reset_device_settings_key";

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ResetDeviceSettingsPreferenceController(Context context) {
        super(context, RESET_DEVICE_SETTINGS_KEY);
    }

    public int getAvailabilityStatus() {
        return !DeviceSettingsUtils.isVzwSIM(this.mContext) ? 2 : 0;
    }

    private Preference createResetDeviceSettingsPreference(int i) {
        Preference preference = new Preference(this.mContext);
        preference.setKey(this.mPreferenceKey);
        preference.setTitle(C1992R$string.reset_device_settings_title);
        preference.setEnabled(true);
        preference.setOrder(i);
        preference.setFragment(ResetDeviceSettings.class.getName());
        return preference;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        Preference createResetDeviceSettingsPreference;
        super.displayPreference(preferenceScreen);
        if (isAvailable() && preferenceScreen.findPreference(this.mPreferenceKey) == null && (createResetDeviceSettingsPreference = createResetDeviceSettingsPreference(preferenceScreen.findPreference("network_reset_pref").getOrder() + 1)) != null) {
            preferenceScreen.addPreference(createResetDeviceSettingsPreference);
        }
    }
}
