package com.motorola.settings.network.emara.jio;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.telephony.SubscriptionManager;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import com.android.settings.C1978R$array;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import com.motorola.settings.network.ExtendedEnabledNetworkModePreferenceController;
import com.motorola.settings.network.MotoMnsLog;

public class JioEnabledNetworkModePreferenceController extends ExtendedEnabledNetworkModePreferenceController {
    private final int JIO_ID = 2018;
    private SubscriptionManager mSubscriptionManager;

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

    public JioEnabledNetworkModePreferenceController(Context context, String str) {
        super(context, str);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
    }

    public void init(Lifecycle lifecycle, FragmentManager fragmentManager, int i) {
        super.init(lifecycle, fragmentManager, i);
        if (JioFeatureLockUtils.isSubsidyLocked(this.mContext) && this.mSubscriptionManager.getActiveSubscriptionInfo(i).getCarrierId() != 2018) {
            this.mBuilder = new JioExtendedPreferenceEntriesBuilder(this.mContext, i);
        }
    }

    protected class JioExtendedPreferenceEntriesBuilder extends ExtendedEnabledNetworkModePreferenceController.ExtendedPreferenceEntriesBuilder {
        private MotoExtTelephonyManager motTm = new MotoExtTelephonyManager(this.mContext, this.mSubId);

        JioExtendedPreferenceEntriesBuilder(Context context, int i) {
            super(context, i);
        }

        /* access modifiers changed from: protected */
        public void setPreferenceEntries() {
            MotoMnsLog.logd("EnabledNetworkMode", "addCustomJioEntries");
            clearAllEntries();
            Resources resourcesForSubId = SubscriptionManager.getResourcesForSubId(this.mContext, this.mSubId);
            for (String add : resourcesForSubId.getStringArray(C1978R$array.preferred_network_mode_choices_jio)) {
                this.mEntries.add(add);
            }
            for (int valueOf : resourcesForSubId.getIntArray(C1978R$array.preferred_network_mode_values_jio)) {
                this.mEntriesValue.add(Integer.valueOf(valueOf));
            }
        }
    }
}
