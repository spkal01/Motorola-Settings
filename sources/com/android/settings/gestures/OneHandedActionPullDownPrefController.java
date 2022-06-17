package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.gestures.OneHandedSettingsUtils;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.widget.RadioButtonPreference;

public class OneHandedActionPullDownPrefController extends BasePreferenceController implements OneHandedSettingsUtils.TogglesCallback, LifecycleObserver, OnStart, OnStop {
    private Preference mPreference;
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

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OneHandedActionPullDownPrefController(Context context, String str) {
        super(context, str);
        this.mUtils = new OneHandedSettingsUtils(context);
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference instanceof RadioButtonPreference) {
            ((RadioButtonPreference) preference).setChecked(!OneHandedSettingsUtils.isSwipeDownNotificationEnabled(this.mContext));
        }
    }

    public int getAvailabilityStatus() {
        return (!OneHandedSettingsUtils.isSupportOneHandedMode() || !OneHandedSettingsUtils.canEnableController(this.mContext)) ? 5 : 0;
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!getPreferenceKey().equals(preference.getKey())) {
            return false;
        }
        OneHandedSettingsUtils.setSwipeDownNotificationEnabled(this.mContext, false);
        if (preference instanceof RadioButtonPreference) {
            ((RadioButtonPreference) preference).setChecked(true);
        }
        return true;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    public void onStart() {
        this.mUtils.registerToggleAwareObserver(this);
    }

    public void onStop() {
        this.mUtils.unregisterToggleAwareObserver();
    }

    public void onChange(Uri uri) {
        if (this.mPreference != null) {
            if (uri.equals(OneHandedSettingsUtils.ONE_HANDED_MODE_ENABLED_URI) || uri.equals(OneHandedSettingsUtils.SHORTCUT_ENABLED_URI)) {
                this.mPreference.setEnabled(OneHandedSettingsUtils.canEnableController(this.mContext));
            } else if (uri.equals(OneHandedSettingsUtils.SHOW_NOTIFICATION_ENABLED_URI)) {
                updateState(this.mPreference);
            }
        }
    }
}
