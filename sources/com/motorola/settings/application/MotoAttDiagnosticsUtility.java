package com.motorola.settings.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.C1992R$string;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class MotoAttDiagnosticsUtility implements LifecycleObserver, OnStart, OnStop {
    public static final String TAG = "com.motorola.settings.application.MotoAttDiagnosticsUtility";
    private Context mContext;
    private boolean mDisableInProgress = false;
    /* access modifiers changed from: private */
    public boolean mForceStopInProgress = false;
    /* access modifiers changed from: private */
    public Listener mListener;
    private BroadcastReceiver mRegisteredReceiver = null;

    public interface Listener {
        void forceStop(String str);
    }

    public MotoAttDiagnosticsUtility(Context context, Listener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    private void registerForIqiBroadcast(Context context) {
        this.mRegisteredReceiver = new IQIReceiver();
        context.registerReceiver(this.mRegisteredReceiver, new IntentFilter("com.motorola.intent.action.IQI_FS_HANDLE_COMPLETE"), "com.att.iqi.permission.RECEIVE_UPLOAD_NOTIFICATIONS", (Handler) null);
    }

    private class IQIReceiver extends BroadcastReceiver {
        private IQIReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            MotoAttDiagnosticsUtility.this.mListener.forceStop("com.att.iqi");
            boolean unused = MotoAttDiagnosticsUtility.this.mForceStopInProgress = false;
        }
    }

    public void onStop() {
        try {
            BroadcastReceiver broadcastReceiver = this.mRegisteredReceiver;
            if (broadcastReceiver != null) {
                this.mContext.unregisterReceiver(broadcastReceiver);
                this.mRegisteredReceiver = null;
            }
        } catch (Exception e) {
            String str = TAG;
            Log.d(str, "Exception while unregistering receiver: " + e);
        }
    }

    public void onStart() {
        if (this.mForceStopInProgress) {
            registerForIqiBroadcast(this.mContext);
        }
    }

    public static boolean isIqiApp(String str) {
        return TextUtils.equals(str, "com.att.iqi");
    }

    public void handleDisablePackage() {
        if (!this.mDisableInProgress) {
            sendBroadcastHelper(1);
            this.mDisableInProgress = true;
            return;
        }
        Toast.makeText(this.mContext, C1992R$string.disable_app_in_progress, 0).show();
    }

    public void handleForceStopPackage() {
        if (!this.mForceStopInProgress) {
            sendBroadcastHelper(2);
            this.mForceStopInProgress = true;
            registerForIqiBroadcast(this.mContext);
            return;
        }
        Toast.makeText(this.mContext, C1992R$string.force_stop_in_progress, 0).show();
    }

    private void sendBroadcastHelper(int i) {
        Intent intent = new Intent("com.motorola.intent.action.IQI_HANDLE_FS_DISABLE");
        intent.putExtra("iqi_action_type", i);
        intent.setClassName("com.motorola.iqimotmetrics", "com.motorola.iqimotmetrics.receivers.IqiSettingsReceiver");
        intent.addFlags(16777216);
        this.mContext.sendBroadcast(intent);
    }

    public void setDisableInProgress(boolean z) {
        this.mDisableInProgress = z;
    }
}
