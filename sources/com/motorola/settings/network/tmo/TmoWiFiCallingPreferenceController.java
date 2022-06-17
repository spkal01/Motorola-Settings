package com.motorola.settings.network.tmo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.motorola.settings.network.MotoTelephonyBasePreferenceController;
import com.motorola.settings.network.MotoTelephonyFeature;

public class TmoWiFiCallingPreferenceController extends MotoTelephonyBasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private WfcStatusObserver mObserver;

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

    public TmoWiFiCallingPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        return MotoTelephonyFeature.isFtr6252Enabled(this.mContext, i) ? 0 : 2;
    }

    public void onStart() {
        if (MotoTelephonyFeature.isFtr6252Enabled(this.mContext, this.mSubId)) {
            WfcStatusObserver wfcStatusObserver = new WfcStatusObserver(this.mContext, this.mSubId);
            this.mObserver = wfcStatusObserver;
            wfcStatusObserver.register();
        }
    }

    public void onStop() {
        WfcStatusObserver wfcStatusObserver = this.mObserver;
        if (wfcStatusObserver != null) {
            wfcStatusObserver.unregister();
            this.mObserver = null;
        }
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        WfcStatusObserver wfcStatusObserver = this.mObserver;
        if (wfcStatusObserver != null) {
            wfcStatusObserver.refreshPreference(preference);
        }
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        try {
            this.mContext.startActivity(getCustomWfcIntent());
            return false;
        } catch (ActivityNotFoundException unused) {
            return false;
        }
    }

    private Intent getCustomWfcIntent() {
        Intent intent = new Intent(SubscriptionManager.getSlotIndex(this.mSubId) == 1 ? "com.motorola.carriersettingsext.WFC_SLOT1" : "com.motorola.carriersettingsext.WFC");
        intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", this.mSubId);
        intent.setFlags(268435456);
        return intent;
    }
}
