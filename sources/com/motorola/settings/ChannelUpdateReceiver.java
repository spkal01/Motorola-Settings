package com.motorola.settings;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ChannelUpdateReceiver extends BroadcastReceiver {
    private static final boolean DEBUG = (!"user".equals(Build.TYPE));

    public void onReceive(Context context, Intent intent) {
        if (intent != null && "com.motorola.cce.Actions.CCE_CHANNEL_ID_UPDATED".equals(intent.getAction())) {
            if (DEBUG) {
                Log.d("SChannelUpdateReceiver", "Received Broadcast:" + intent.getAction());
            }
            CarrierMediaUpdateJobService.scheduleCarrierMediaUpdateOperation(context);
            CarrierPropUpdateJobService.scheduleCarrierPropUpdateOperation(context);
            disableReceiver(context);
        }
    }

    private void disableReceiver(Context context) {
        if (DEBUG) {
            Log.d("SChannelUpdateReceiver", "disable ChannelUpdateReceiver");
        }
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, ChannelUpdateReceiver.class), 2, 1);
    }
}
