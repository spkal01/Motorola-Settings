package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import androidx.preference.PreferenceScreen;
import com.android.settings.gestures.OneHandedSettingsUtils;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.SettingsMainSwitchPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.widget.MainSwitchPreference;

public class OneHandedMainSwitchPreferenceController extends SettingsMainSwitchPreferenceController implements OneHandedSettingsUtils.TogglesCallback, LifecycleObserver, OnStart, OnStop {
    private MainSwitchPreference mPreference;
    private final OneHandedSettingsUtils mUtils;

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

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OneHandedMainSwitchPreferenceController(Context context, String str) {
        super(context, str);
        this.mUtils = new OneHandedSettingsUtils(context);
    }

    public int getAvailabilityStatus() {
        return (!OneHandedSettingsUtils.isSupportOneHandedMode() || OneHandedSettingsUtils.getNavigationBarMode(this.mContext) == 0) ? 5 : 0;
    }

    public boolean isChecked() {
        return OneHandedSettingsUtils.isOneHandedModeEnabled(this.mContext);
    }

    public boolean setChecked(boolean z) {
        if (z) {
            OneHandedSettingsUtils.setTapsAppToExitEnabled(this.mContext, true);
            OneHandedSettingsUtils.setTimeoutValue(this.mContext, OneHandedSettingsUtils.OneHandedTimeout.MEDIUM.getValue());
        }
        OneHandedSettingsUtils.setOneHandedModeEnabled(this.mContext, z);
        return true;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (MainSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public void onStart() {
        this.mUtils.registerToggleAwareObserver(this);
    }

    public void onStop() {
        this.mUtils.unregisterToggleAwareObserver();
    }

    public void onChange(Uri uri) {
        if (this.mPreference != null && uri.equals(OneHandedSettingsUtils.ONE_HANDED_MODE_ENABLED_URI)) {
            this.mPreference.setChecked(isChecked());
        }
    }
}
