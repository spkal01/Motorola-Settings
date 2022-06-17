package com.motorola.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CarrierPropInitOnBoot extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Log.d("CarrierPropInitOnBoot", "CarrierPropInitOnBoot. Received Broadcast:" + intent.getAction());
            if ("android.intent.action.LOCKED_BOOT_COMPLETED".equals(intent.getAction())) {
                CarrierPropUpdateJobService.scheduleCarrierPropUpdateOperation(context);
            }
        }
    }
}
