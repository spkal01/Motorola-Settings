package com.android.settings.network.ims;

import android.content.Context;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsException;
import android.util.Log;

public class VoNRQueryImsState extends ImsQueryController {
    private Context mContext;
    private int mSubId;

    public VoNRQueryImsState(Context context, int i) {
        super(1, 3, 1);
        this.mContext = context;
        this.mSubId = i;
    }

    public boolean isReadyToVoNR() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return false;
        }
        try {
            return isServiceStateReady(this.mSubId);
        } catch (ImsException | IllegalArgumentException | InterruptedException e) {
            Log.w("VoNRQueryImsState", "fail to get VoNR service status. subId=" + this.mSubId, e);
            return false;
        }
    }

    public boolean isAllowUserControl() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return false;
        }
        if (!isTtyEnabled(this.mContext) || isTtyOnVolteEnabled(this.mSubId)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isTtyEnabled(Context context) {
        return ((TelecomManager) context.getSystemService(TelecomManager.class)).getCurrentTtyMode() != 0;
    }
}
