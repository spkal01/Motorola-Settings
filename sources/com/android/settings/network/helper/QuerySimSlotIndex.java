package com.android.settings.network.helper;

import android.telephony.TelephonyManager;
import android.telephony.UiccSlotInfo;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class QuerySimSlotIndex implements Callable<AtomicIntegerArray> {
    private boolean mDisabledSlotsIncluded;
    private boolean mOnlySlotWithSim;
    private TelephonyManager mTelephonyManager;

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$call$2(int i, int i2) {
        return i2 >= i;
    }

    public QuerySimSlotIndex(TelephonyManager telephonyManager, boolean z, boolean z2) {
        this.mTelephonyManager = telephonyManager;
        this.mDisabledSlotsIncluded = z;
        this.mOnlySlotWithSim = z2;
    }

    public AtomicIntegerArray call() {
        UiccSlotInfo[] uiccSlotsInfo = this.mTelephonyManager.getUiccSlotsInfo();
        int i = 0;
        if (uiccSlotsInfo == null) {
            return new AtomicIntegerArray(0);
        }
        if (!this.mOnlySlotWithSim) {
            i = -1;
        }
        return new AtomicIntegerArray(Arrays.stream(uiccSlotsInfo).filter(new QuerySimSlotIndex$$ExternalSyntheticLambda1(this)).mapToInt(new QuerySimSlotIndex$$ExternalSyntheticLambda2(this)).filter(new QuerySimSlotIndex$$ExternalSyntheticLambda0(i)).toArray());
    }

    /* access modifiers changed from: protected */
    /* renamed from: filterSlot */
    public boolean lambda$call$0(UiccSlotInfo uiccSlotInfo) {
        if (this.mDisabledSlotsIncluded) {
            return true;
        }
        if (uiccSlotInfo == null) {
            return false;
        }
        return uiccSlotInfo.getIsActive();
    }

    /* access modifiers changed from: protected */
    /* renamed from: mapToSlotIndex */
    public int lambda$call$1(UiccSlotInfo uiccSlotInfo) {
        if (uiccSlotInfo == null || uiccSlotInfo.getCardStateInfo() == 1) {
            return -1;
        }
        return uiccSlotInfo.getLogicalSlotIdx();
    }
}
