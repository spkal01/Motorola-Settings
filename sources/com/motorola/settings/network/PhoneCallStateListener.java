package com.motorola.settings.network;

import android.content.Context;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;

public class PhoneCallStateListener extends TelephonyCallback implements TelephonyCallback.CallStateListener {
    private Client mClient;
    private Context mContext;
    private TelephonyManager mTelephonyManager;

    public interface Client {
        void onCallStateChanged(int i);
    }

    public PhoneCallStateListener(Context context, Client client) {
        this.mClient = client;
        this.mContext = context;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
    }

    public void onCallStateChanged(int i) {
        Client client = this.mClient;
        if (client != null) {
            client.onCallStateChanged(i);
        }
    }

    public void register(int i) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(i);
        }
        this.mTelephonyManager.registerTelephonyCallback(this.mContext.getMainExecutor(), this);
    }

    public void unregister() {
        this.mTelephonyManager.unregisterTelephonyCallback(this);
    }
}
