package com.motorola.settings.network.vzw;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.ExtendedCdmaSystemSelectPreferenceController;
import com.motorola.settings.network.MotoTelephonyFeature;

public class VzwCdmaSystemSelectPreferenceController extends ExtendedCdmaSystemSelectPreferenceController {
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

    public VzwCdmaSystemSelectPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        if (MotoTelephonyFeature.isCdmaLessDevice(this.mContext, i)) {
            return 2;
        }
        return super.getAvailabilityStatus(i);
    }

    /* access modifiers changed from: protected */
    public void updateStateBasedOnNwMode(Preference preference, int i) {
        if (!MotoTelephonyFeature.isVzwWorldPhoneEnabled(this.mContext, this.mSubId)) {
            super.updateStateBasedOnNwMode(preference, i);
        } else if (i == 4 || i == 5 || i == 6 || i == 11 || i == 12) {
            this.mPreference.setEnabled(true);
        } else {
            this.mPreference.setEnabled(false);
        }
    }
}
