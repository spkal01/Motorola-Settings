package com.motorola.settings.network.tmo;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import androidx.lifecycle.Lifecycle;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoMobileNetworkUtils;
import com.motorola.settings.network.NrPreferenceController;

public class Tmo5GToggleEnablerController extends NrPreferenceController {
    private static final String CONFIG_ENABLE_URI = "content://com.motorola.carrierconfig.provider.Toggle5GConfigProvider/5g_enabled";
    private static final String CONFIG_VISIBILITY_URI = "content://com.motorola.carrierconfig.provider.Toggle5GConfigProvider/5g_toggle_enabled";
    private static final String LOG_TAG = "Tmo5GToggleEnablerController";
    private TelephonyManager mTelephonyManager;

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

    public Tmo5GToggleEnablerController(Context context, String str) {
        super(context, str);
    }

    public void init(Lifecycle lifecycle, int i) {
        super.init(lifecycle, i);
        this.mSubId = i;
        this.mTelephonyManager = TelephonyManager.from(this.mContext).createForSubscriptionId(this.mSubId);
    }

    public int getAvailabilityStatus(int i) {
        boolean modemSupports5g = MotoMobileNetworkUtils.modemSupports5g(this.mContext, i);
        String str = LOG_TAG;
        MotoMnsLog.logd(str, "has5GSupport: " + modemSupports5g);
        if (modemSupports5g) {
            return getIsToogle5GVisible(i);
        }
        return 2;
    }

    private int getIsToogle5GVisible(int i) {
        return (!TmoConfigUtils.getConfigFromTMOConfig(this.mContext, CONFIG_VISIBILITY_URI, i) || !TmoConfigUtils.getConfigFromTMOConfig(this.mContext, CONFIG_ENABLE_URI, i)) ? 2 : 0;
    }

    public boolean isChecked() {
        return super.isChecked() && TmoConfigUtils.getConfigFromTMOConfig(this.mContext, CONFIG_ENABLE_URI, this.mSubId);
    }

    public boolean setChecked(boolean z) {
        long allowedNetworkTypesForReason = this.mTelephonyManager.getAllowedNetworkTypesForReason(2);
        long j = z ? allowedNetworkTypesForReason | 524288 : allowedNetworkTypesForReason & -524289;
        this.mTelephonyManager.setAllowedNetworkTypesForReason(2, j);
        int pnt = MotoMobileNetworkUtils.getPnt(this.mContext, this.mSubId);
        String str = LOG_TAG;
        MotoMnsLog.logd(str, "currentPnt:" + pnt + ", allowRaf:" + j + ", isChecked:" + z);
        return true;
    }
}
