package com.android.settings.fuelgauge;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.BatteryInfo;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.HashMap;

public class TopLevelBatteryPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, BatteryPreferenceController {
    protected static HashMap<String, ComponentName> sReplacingActivityMap = new HashMap<>();
    private final BatteryBroadcastReceiver mBatteryBroadcastReceiver;
    private BatteryInfo mBatteryInfo;
    private BatterySettingsFeatureProvider mBatterySettingsFeatureProvider;
    private BatteryStatusFeatureProvider mBatteryStatusFeatureProvider;
    private String mBatteryStatusLabel;
    protected boolean mIsBatteryPresent = true;
    private Preference mPreference;

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

    public TopLevelBatteryPreferenceController(Context context, String str) {
        super(context, str);
        BatteryBroadcastReceiver batteryBroadcastReceiver = new BatteryBroadcastReceiver(this.mContext);
        this.mBatteryBroadcastReceiver = batteryBroadcastReceiver;
        batteryBroadcastReceiver.setBatteryChangedListener(new TopLevelBatteryPreferenceController$$ExternalSyntheticLambda0(this));
        this.mBatterySettingsFeatureProvider = FeatureFactory.getFactory(context).getBatterySettingsFeatureProvider(context);
        this.mBatteryStatusFeatureProvider = FeatureFactory.getFactory(context).getBatteryStatusFeatureProvider(context);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(int i) {
        if (i == 5) {
            this.mIsBatteryPresent = false;
        }
        BatteryInfo.getBatteryInfo(this.mContext, (BatteryInfo.Callback) new TopLevelBatteryPreferenceController$$ExternalSyntheticLambda1(this), true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(BatteryInfo batteryInfo) {
        this.mBatteryInfo = batteryInfo;
        updateState(this.mPreference);
    }

    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(C1980R$bool.config_show_top_level_battery) ? 0 : 3;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        ComponentName componentName;
        String fragment = preference.getFragment();
        if (fragment == null || fragment.isEmpty()) {
            return super.handlePreferenceTreeClick(preference);
        }
        ComponentName convertClassPathToComponentName = convertClassPathToComponentName(fragment);
        if (convertClassPathToComponentName == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        if (sReplacingActivityMap.containsKey(fragment)) {
            componentName = sReplacingActivityMap.get(fragment);
        } else {
            ComponentName replacingActivity = this.mBatterySettingsFeatureProvider.getReplacingActivity(convertClassPathToComponentName);
            sReplacingActivityMap.put(fragment, replacingActivity);
            componentName = replacingActivity;
        }
        if (componentName == null || convertClassPathToComponentName.compareTo(componentName) == 0) {
            return super.handlePreferenceTreeClick(preference);
        }
        Intent intent = new Intent();
        intent.setComponent(convertClassPathToComponentName);
        this.mContext.startActivity(intent);
        return true;
    }

    public void onStart() {
        this.mBatteryBroadcastReceiver.register();
    }

    public void onStop() {
        this.mBatteryBroadcastReceiver.unRegister();
    }

    public CharSequence getSummary() {
        return getSummary(true);
    }

    private CharSequence getSummary(boolean z) {
        if (!this.mIsBatteryPresent) {
            return this.mContext.getText(C1992R$string.battery_missing_message);
        }
        return getDashboardLabel(this.mContext, this.mBatteryInfo, z);
    }

    /* access modifiers changed from: protected */
    public CharSequence getDashboardLabel(Context context, BatteryInfo batteryInfo, boolean z) {
        if (batteryInfo == null || context == null) {
            return null;
        }
        if (z && !this.mBatteryStatusFeatureProvider.triggerBatteryStatusUpdate(this, batteryInfo)) {
            this.mBatteryStatusLabel = null;
        }
        String str = this.mBatteryStatusLabel;
        return str == null ? generateLabel(batteryInfo) : str;
    }

    private CharSequence generateLabel(BatteryInfo batteryInfo) {
        CharSequence charSequence;
        if (!batteryInfo.discharging && (charSequence = batteryInfo.chargeLabel) != null) {
            return charSequence;
        }
        CharSequence charSequence2 = batteryInfo.remainingLabel;
        if (charSequence2 == null) {
            return batteryInfo.batteryPercentString;
        }
        return this.mContext.getString(C1992R$string.power_remaining_settings_home_page, new Object[]{batteryInfo.batteryPercentString, charSequence2});
    }

    public void updateBatteryStatus(String str, BatteryInfo batteryInfo) {
        CharSequence summary;
        this.mBatteryStatusLabel = str;
        if (this.mPreference != null && (summary = getSummary(false)) != null) {
            this.mPreference.setSummary(summary);
        }
    }

    protected static ComponentName convertClassPathToComponentName(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        String[] split = str.split("\\.");
        int length = split.length - 1;
        if (length < 0) {
            return null;
        }
        int length2 = (str.length() - split[length].length()) - 1;
        return new ComponentName(length2 > 0 ? str.substring(0, length2) : "", split[length]);
    }
}
