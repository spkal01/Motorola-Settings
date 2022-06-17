package com.android.settings.sim.smartForwarding;

import android.telephony.CallForwardingInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class DisableSmartForwardingTask implements Runnable {
    private final CallForwardingInfo[] callForwardingInfo;
    private final boolean[] callWaitingStatus;

    /* renamed from: tm */
    private final TelephonyManager f132tm;

    public DisableSmartForwardingTask(TelephonyManager telephonyManager, boolean[] zArr, CallForwardingInfo[] callForwardingInfoArr) {
        this.f132tm = telephonyManager;
        this.callWaitingStatus = zArr;
        this.callForwardingInfo = callForwardingInfoArr;
    }

    public void run() {
        for (int i = 0; i < this.f132tm.getActiveModemCount(); i++) {
            if (this.callWaitingStatus != null) {
                Log.d("SmartForwarding", "Restore call waiting to " + this.callWaitingStatus[i]);
                this.f132tm.setCallWaitingEnabled(this.callWaitingStatus[i], (Executor) null, (Consumer) null);
            }
            CallForwardingInfo[] callForwardingInfoArr = this.callForwardingInfo;
            if (!(callForwardingInfoArr == null || callForwardingInfoArr[i] == null || callForwardingInfoArr[i].getTimeoutSeconds() <= 0)) {
                Log.d("SmartForwarding", "Restore call waiting to " + this.callForwardingInfo);
                this.f132tm.setCallForwarding(this.callForwardingInfo[i], (Executor) null, (Consumer) null);
            }
        }
    }
}
