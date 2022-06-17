package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.MotoMobileNetworkUtils;
import com.motorola.settings.network.cache.MotoMnsCache;
import java.util.Iterator;

public class DisableSimFooterPreferenceController extends TelephonyBasePreferenceController {
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

    public DisableSimFooterPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void init(int i) {
        this.mSubId = i;
    }

    public int getAvailabilityStatus(int i) {
        if (i == -1) {
            return 2;
        }
        if (!MotoMobileNetworkUtils.isSimConfigurable(this.mContext, i)) {
            return 0;
        }
        SubscriptionManager subscriptionManager = (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);
        Iterator<SubscriptionInfo> it = MotoMnsCache.getIns(this.mContext).getAvailableSubInfos().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SubscriptionInfo next = it.next();
            if (next.getSubscriptionId() == i) {
                if (next.isEmbedded() || SubscriptionUtil.showToggleForPhysicalSim(subscriptionManager)) {
                    return 2;
                }
            }
        }
        return 0;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        boolean isAirplaneModeOn = MotoMobileNetworkUtils.isAirplaneModeOn(this.mContext);
        boolean z = !MotoMobileNetworkUtils.isSimInSlotUsable(this.mContext, this.mSubId);
        if (isAirplaneModeOn) {
            preference.setTitle(C1992R$string.mobile_network_settings_not_available_in_airplane_mode);
        } else if (z) {
            preference.setTitle(C1992R$string.mobile_network_disabled_sim);
        } else {
            preference.setTitle(C1992R$string.mobile_network_disable_sim_explanation);
        }
    }
}
