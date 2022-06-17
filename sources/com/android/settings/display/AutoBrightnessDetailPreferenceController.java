package com.android.settings.display;

import android.content.Context;
import android.content.IntentFilter;
import android.widget.Switch;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1980R$bool;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.SettingsMainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

public class AutoBrightnessDetailPreferenceController extends AutoBrightnessPreferenceController implements OnMainSwitchChangeListener {
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

    public boolean isPublicSlice() {
        return true;
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AutoBrightnessDetailPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus() {
        return (this.mContext.getResources().getBoolean(C1980R$bool.config_adaptive_brightness_customizable) || !this.mContext.getResources().getBoolean(17891387)) ? 3 : 0;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SettingsMainSwitchPreference settingsMainSwitchPreference = (SettingsMainSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        settingsMainSwitchPreference.addOnSwitchChangeListener(this);
        settingsMainSwitchPreference.setChecked(isChecked());
    }

    public void onSwitchChanged(Switch switchR, boolean z) {
        if (z != isChecked()) {
            setChecked(z);
        }
    }
}
