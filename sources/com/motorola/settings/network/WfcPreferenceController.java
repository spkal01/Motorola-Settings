package com.motorola.settings.network;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.widget.GenericSwitchController;
import com.android.settings.widget.PrimarySwitchPreference;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.motorola.settings.network.att.WfcSwitchManager;
import com.motorola.settings.wifi.calling.WifiCallingUtils;

public class WfcPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnResume, OnPause, OnStart, OnStop, SwitchWidgetController.OnSwitchChangeListener, WfcSwitchManager.WfcChangeListener, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private SubscriptionsChangeListener mChangeListener;
    private boolean mIsDualSimDevice;
    private Uri mOnStopNotifyUri;
    private PreferenceScreen mPrefScreen;
    private boolean mShouldNotifyOnStop;
    private Uri mSummaryUri;
    private SwitchWidgetController mSwitchController;
    private WfcSwitchManager mWfcManager;
    private PrimarySwitchPreference mWfcPreference;

    public String getPreferenceKey() {
        return "toggle_wifi_calling";
    }

    public void onAirplaneModeChanged(boolean z) {
    }

    public WfcPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mIsDualSimDevice = WifiCallingUtils.isDualSimDevice(context, SubscriptionManager.getDefaultVoiceSubscriptionId());
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPrefScreen = preferenceScreen;
        if (isAvailable()) {
            this.mWfcPreference = (PrimarySwitchPreference) preferenceScreen.findPreference("toggle_wifi_calling");
            prepareUi();
            this.mWfcManager = new WfcSwitchManager(this.mContext, this, this.mSummaryUri, this.mOnStopNotifyUri, SubscriptionManager.getDefaultVoiceSubscriptionId());
        }
    }

    public boolean isAvailable() {
        if (!this.mIsDualSimDevice) {
            MotoMnsLog.logd("WfcPreferenceController", "Single sim device");
            return canShowPreference();
        }
        MotoMnsLog.logd("WfcPreferenceController", " Dual sim device");
        return WifiCallingUtils.getUsableSimCount(this.mContext) < 2 && canShowPreference();
    }

    public void onResume() {
        this.mChangeListener.start();
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
        this.mChangeListener.stop();
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
            this.mContext.startActivity(new Intent("com.motorola.wfc.TOGGLE_WFC"));
            return false;
        } catch (ActivityNotFoundException unused) {
            return false;
        }
    }

    private boolean isVisible() {
        return this.mWfcPreference != null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0044 A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:11:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean canShowPreference() {
        /*
            r3 = this;
            boolean r0 = r3.mIsDualSimDevice
            r1 = 1
            if (r0 == 0) goto L_0x0028
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = " Sim slot id: "
            r0.append(r2)
            int r2 = r3.getActiveSimSlotId()
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "WfcPreferenceController"
            com.motorola.settings.network.MotoMnsLog.logd(r2, r0)
            int r0 = r3.getActiveSimSlotId()
            if (r0 != r1) goto L_0x0028
            java.lang.String r0 = "com.motorola.wfc.TOGGLE_WFC_SLOT1"
            goto L_0x002a
        L_0x0028:
            java.lang.String r0 = "com.motorola.wfc.TOGGLE_WFC"
        L_0x002a:
            android.content.Intent r2 = new android.content.Intent
            r2.<init>(r0)
            android.content.Context r3 = r3.mContext
            android.content.pm.PackageManager r3 = r3.getPackageManager()
            r0 = 1048576(0x100000, float:1.469368E-39)
            java.util.List r3 = r3.queryIntentActivities(r2, r0)
            if (r3 == 0) goto L_0x0044
            int r3 = r3.size()
            if (r3 <= 0) goto L_0x0044
            goto L_0x0045
        L_0x0044:
            r1 = 0
        L_0x0045:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.settings.network.WfcPreferenceController.canShowPreference():boolean");
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
            Intent intent = new Intent((!this.mIsDualSimDevice || getActiveSimSlotId() != 1) ? "com.motorola.settings.OVERRIDE_AOSP_WIFI_CALLING_SETTINGS" : "com.motorola.settings.OVERRIDE_AOSP_WIFI_CALLING_SETTINGS_SLOT1");
            PackageManager packageManager = this.mContext.getPackageManager();
            for (ResolveInfo next : packageManager.queryIntentActivities(intent, 128)) {
                if (next.system) {
                    ActivityInfo activityInfo = next.activityInfo;
                    try {
                        Resources resourcesForApplication = packageManager.getResourcesForApplication(activityInfo.packageName);
                        Bundle bundle = activityInfo.metaData;
                        int i = 0;
                        if (bundle.containsKey("com.android.settings.icon")) {
                            i = bundle.getInt("com.android.settings.icon");
                        }
                        if (i != 0) {
                            this.mWfcPreference.setIcon(Icon.createWithResource(activityInfo.packageName, i).loadDrawable(this.mContext));
                        }
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
                        String string = bundle.containsKey("com.motorola.settings.dashboard.tile_summary_provider_uri") ? bundle.getString("com.motorola.settings.dashboard.tile_summary_provider_uri") : null;
                        this.mSummaryUri = !TextUtils.isEmpty(string) ? Uri.parse(string) : null;
                        if (bundle.containsKey("com.motorola.settings.dashboard.on_stop_notify_uri")) {
                            string = bundle.getString("com.motorola.settings.dashboard.on_stop_notify_uri");
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
        if (this.mPrefScreen != null) {
            if ((WifiCallingUtils.getUsableSimCount(this.mContext) > 1 || !canShowPreference()) && isVisible()) {
                this.mPrefScreen.removePreference(this.mWfcPreference);
            }
        }
    }

    public void onSubscriptionsChanged() {
        update();
    }

    private int getActiveSimSlotId() {
        int defaultVoiceSubscriptionId = SubscriptionManager.getDefaultVoiceSubscriptionId();
        MotoMnsLog.logd("WfcPreferenceController", " Default voice subscription id = " + defaultVoiceSubscriptionId);
        if (defaultVoiceSubscriptionId != -1) {
            return SubscriptionManager.getSlotIndex(defaultVoiceSubscriptionId);
        }
        return 0;
    }
}
