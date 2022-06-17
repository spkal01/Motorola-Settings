package com.motorola.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import androidx.fragment.app.FragmentManager;
import com.android.settings.network.telephony.TelephonyBasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public abstract class MotoTelephonyBasePreferenceController extends TelephonyBasePreferenceController implements MotoTelephonyInit {
    protected FragmentManager mFragmentManager;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public abstract /* synthetic */ int getAvailabilityStatus(int i);

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

    public MotoTelephonyBasePreferenceController(Context context, String str) {
        super(context, str);
    }

    public void init(int i, FragmentManager fragmentManager) {
        this.mSubId = i;
        this.mFragmentManager = fragmentManager;
    }
}
