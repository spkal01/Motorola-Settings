package com.motorola.settings.network.att;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.GenericSwitchController;
import com.android.settings.widget.PrimarySwitchPreference;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoTelephonyBasePreferenceController;
import com.motorola.settings.network.att.WfcSwitchManager;
import com.motorola.settings.wifi.calling.WifiCallingUtils;
import java.util.List;

public class AttWfcPreferenceController extends MotoTelephonyBasePreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnResume, OnPause, OnStart, OnStop, SwitchWidgetController.OnSwitchChangeListener, WfcSwitchManager.WfcChangeListener, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private static final String KEY_PREF = "toggle_wifi_calling";
    private static final String META_KEY_ON_STOP_NOTIFY_URI = "com.motorola.settings.dashboard.on_stop_notify_uri";
    private static final String META_KEY_SUMMARY_PROVIDER_URI = "com.motorola.settings.dashboard.tile_summary_provider_uri";
    private static final String TAG = "AttWfcPreferenceController";
    private static final String TOGGLE_WFC_ACTION = "com.motorola.wfc.TOGGLE_WFC";
    private static final String TOGGLE_WFC_ACTION_SLOT1 = "com.motorola.wfc.TOGGLE_WFC_SLOT1";
    private SubscriptionsChangeListener mChangeListener;
    private boolean mIsDualSimDevice;
    private Uri mOnStopNotifyUri;
    private PreferenceScreen mPrefScreen;
    private boolean mShouldNotifyOnStop;
    private Uri mSummaryUri;
    private SwitchWidgetController mSwitchController;
    private WfcSwitchManager mWfcManager;
    private PrimarySwitchPreference mWfcPreference;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public String getPreferenceKey() {
        return KEY_PREF;
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

    public void onAirplaneModeChanged(boolean z) {
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AttWfcPreferenceController(Context context, String str) {
        super(context, str);
        boolean isDualSimDevice = WifiCallingUtils.isDualSimDevice(context, this.mSubId);
        this.mIsDualSimDevice = isDualSimDevice;
        if (isDualSimDevice) {
            this.mChangeListener = new SubscriptionsChangeListener(context, this);
        }
    }

    public int getAvailabilityStatus(int i) {
        MotoMnsLog.logd(TAG, " Is dual sim: " + this.mIsDualSimDevice + " Usable count = " + WifiCallingUtils.getUsableSimCount(this.mContext) + " Subid: " + i);
        return (!this.mIsDualSimDevice || WifiCallingUtils.getUsableSimCount(this.mContext) <= 1 || !canShowPreference(i)) ? 2 : 0;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (getAvailabilityStatus(this.mSubId) == 0) {
            this.mWfcPreference = (PrimarySwitchPreference) preferenceScreen.findPreference(KEY_PREF);
            prepareUi();
            this.mWfcManager = new WfcSwitchManager(this.mContext, this, this.mSummaryUri, this.mOnStopNotifyUri, this.mSubId);
        }
    }

    public void onResume() {
        if (this.mIsDualSimDevice) {
            this.mChangeListener.start();
        }
        if (isVisible()) {
            this.mWfcManager.register();
        }
    }

    private void refreshUi() {
        if (isVisible()) {
            this.mWfcPreference.setChecked(this.mWfcManager.isWfcStateON());
            if (this.mWfcManager.isWfcStateEnabling()) {
                this.mWfcPreference.setSummary((CharSequence) this.mWfcManager.getSummary());
            } else {
                this.mWfcPreference.setSummary((CharSequence) null);
            }
            updateSwitchState();
        }
    }

    public void onPause() {
        if (this.mIsDualSimDevice) {
            this.mChangeListener.stop();
        }
        if (isVisible()) {
            this.mWfcManager.unregister();
        }
    }

    public void onStart() {
        if (isVisible()) {
            GenericSwitchController genericSwitchController = new GenericSwitchController(this.mWfcPreference);
            this.mSwitchController = genericSwitchController;
            genericSwitchController.setListener(this);
            this.mSwitchController.startListening();
        }
    }

    public void onStop() {
        if (this.mSwitchController != null) {
            if (this.mShouldNotifyOnStop) {
                this.mWfcManager.notifyOnStop();
            }
            this.mSwitchController.stopListening();
            this.mSwitchController.teardownView();
        }
    }

    public boolean onSwitchToggled(boolean z) {
        this.mShouldNotifyOnStop = true;
        try {
            this.mContext.startActivity(getCustomWfcIntent());
            return false;
        } catch (ActivityNotFoundException unused) {
            return false;
        }
    }

    private Intent getCustomWfcIntent() {
        Intent intent = new Intent(SubscriptionManager.getSlotIndex(this.mSubId) == 1 ? TOGGLE_WFC_ACTION_SLOT1 : TOGGLE_WFC_ACTION);
        intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", this.mSubId);
        intent.setFlags(268435456);
        return intent;
    }

    private boolean isVisible() {
        return this.mWfcPreference != null;
    }

    private boolean canShowPreference(int i) {
        List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(new Intent(SubscriptionManager.getSlotIndex(i) == 1 ? TOGGLE_WFC_ACTION_SLOT1 : TOGGLE_WFC_ACTION), 1048576);
        if (queryIntentActivities == null || queryIntentActivities.size() <= 0) {
            return false;
        }
        return true;
    }

    public void toggleStateChanged() {
        refreshUi();
    }

    private void updateSwitchState() {
        if (isVisible()) {
            boolean z = true;
            this.mWfcPreference.setEnabled(!this.mWfcManager.disableUi());
            if (this.mWfcManager.isWfcStateEnabling() || this.mWfcManager.inWifiCall() || this.mWfcManager.disableUi()) {
                z = false;
            }
            this.mWfcPreference.setSwitchEnabled(z);
        }
    }

    private void prepareUi() {
        String str;
        if (this.mWfcPreference != null) {
            Intent intent = new Intent(SubscriptionManager.getSlotIndex(this.mSubId) == 1 ? "com.motorola.settings.OVERRIDE_AOSP_WIFI_CALLING_SETTINGS_SLOT1" : "com.motorola.settings.OVERRIDE_AOSP_WIFI_CALLING_SETTINGS");
            PackageManager packageManager = this.mContext.getPackageManager();
            for (ResolveInfo next : packageManager.queryIntentActivities(intent, 128)) {
                if (next.system) {
                    ActivityInfo activityInfo = next.activityInfo;
                    try {
                        Resources resourcesForApplication = packageManager.getResourcesForApplication(activityInfo.packageName);
                        Bundle bundle = activityInfo.metaData;
                        Uri uri = null;
                        if (bundle.containsKey("com.android.settings.title")) {
                            str = bundle.get("com.android.settings.title") instanceof Integer ? resourcesForApplication.getString(bundle.getInt("com.android.settings.title")) : bundle.getString("com.android.settings.title");
                        } else {
                            str = null;
                        }
                        intent.setPackage(activityInfo.packageName);
                        this.mWfcPreference.setIntent(intent);
                        if (str != null) {
                            this.mWfcPreference.setTitle((CharSequence) str);
                        }
                        String string = bundle.containsKey(META_KEY_SUMMARY_PROVIDER_URI) ? bundle.getString(META_KEY_SUMMARY_PROVIDER_URI) : null;
                        this.mSummaryUri = !TextUtils.isEmpty(string) ? Uri.parse(string) : null;
                        if (bundle.containsKey(META_KEY_ON_STOP_NOTIFY_URI)) {
                            string = bundle.getString(META_KEY_ON_STOP_NOTIFY_URI);
                        }
                        if (!TextUtils.isEmpty(string)) {
                            uri = Uri.parse(string);
                        }
                        this.mOnStopNotifyUri = uri;
                        return;
                    } catch (PackageManager.NameNotFoundException unused) {
                        return;
                    }
                }
            }
        }
    }

    private void update() {
        if (this.mPrefScreen != null && WifiCallingUtils.getUsableSimCount(this.mContext) > 1 && isVisible()) {
            this.mPrefScreen.removePreference(this.mWfcPreference);
        }
    }

    public void onSubscriptionsChanged() {
        update();
    }
}
