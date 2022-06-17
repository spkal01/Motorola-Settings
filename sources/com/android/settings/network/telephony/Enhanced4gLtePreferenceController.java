package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.telephony.SubscriptionManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C1992R$string;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.MotoMnsLog;

public class Enhanced4gLtePreferenceController extends Enhanced4gBasePreferenceController {
    private static final String TAG = "Enhanced 4G LTE";

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    /* access modifiers changed from: protected */
    public int getMode() {
        return 0;
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

    public Enhanced4gLtePreferenceController(Context context, String str) {
        super(context, str);
    }

    /* access modifiers changed from: protected */
    public void updateTitleAndSummary(Preference preference) {
        if (preference != null) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            switchPreference.setTitle((CharSequence) getResourceFromSubId(this.mSubId).getString(C1992R$string.enhanced_4g_lte_mode_title));
            switchPreference.setSummary((CharSequence) getResourceFromSubId(this.mSubId).getString(C1992R$string.enhanced_4g_lte_mode_summary));
            MotoMnsLog.logd(TAG, "updateTitleAndSummary");
        }
    }

    private Resources getResourceFromSubId(int i) {
        return SubscriptionManager.getResourcesForSubId(this.mContext, i);
    }
}
