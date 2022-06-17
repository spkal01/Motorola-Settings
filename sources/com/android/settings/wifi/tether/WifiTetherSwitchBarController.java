package com.android.settings.wifi.tether;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.motorola.settings.tether.TetherStartupDialogUtils;
import com.motorola.settings.tether.WarningDialogCallback;

public class WifiTetherSwitchBarController implements LifecycleObserver, OnStart, OnStop, DataSaverBackend.Listener, View.OnClickListener {
    private static final IntentFilter WIFI_INTENT_FILTER = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    final DataSaverBackend mDataSaverBackend;
    /* access modifiers changed from: private */
    public boolean mIsTetherTurningOn = false;
    final ConnectivityManager.OnStartTetheringCallback mOnStartTetheringCallback = new ConnectivityManager.OnStartTetheringCallback() {
        public void onTetheringFailed() {
            WifiTetherSwitchBarController.super.onTetheringFailed();
            WifiTetherSwitchBarController.this.mSwitchBar.setChecked(false);
            WifiTetherSwitchBarController.this.updateWifiSwitch();
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("wifi_state", 14);
                if (!isInitialStickyBroadcast() || intExtra == WifiTetherSwitchBarController.this.mWifiManager.getWifiApState()) {
                    WifiTetherSwitchBarController.this.handleWifiApStateChanged(intExtra);
                }
            }
        }
    };
    private final Switch mSwitch;
    /* access modifiers changed from: private */
    public final SettingsMainSwitchBar mSwitchBar;
    /* access modifiers changed from: private */
    public final WifiManager mWifiManager;

    public void onAllowlistStatusChanged(int i, boolean z) {
    }

    public void onDenylistStatusChanged(int i, boolean z) {
    }

    WifiTetherSwitchBarController(Context context, SettingsMainSwitchBar settingsMainSwitchBar) {
        boolean z = false;
        this.mContext = context;
        this.mSwitchBar = settingsMainSwitchBar;
        this.mSwitch = settingsMainSwitchBar.getSwitch();
        this.mDataSaverBackend = new DataSaverBackend(context);
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        this.mWifiManager = wifiManager;
        settingsMainSwitchBar.setChecked(wifiManager.getWifiApState() == 13 ? true : z);
        updateWifiSwitch();
    }

    public void onStart() {
        this.mDataSaverBackend.addListener(this);
        this.mSwitch.setOnClickListener(this);
        this.mContext.registerReceiver(this.mReceiver, WIFI_INTENT_FILTER);
    }

    public void onStop() {
        this.mDataSaverBackend.remListener(this);
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    public void onClick(View view) {
        if (!((Switch) view).isChecked()) {
            stopTether();
        } else if (!this.mWifiManager.isWifiApEnabled() && !this.mIsTetherTurningOn) {
            this.mSwitchBar.setChecked(false);
            updateWifiSwitch();
            Context context = this.mContext;
            TetherStartupDialogUtils.displayDialogsOrEnableWifiTether(context, ((Activity) context).getFragmentManager(), getWarningDialogCallback());
        }
    }

    private WarningDialogCallback getWarningDialogCallback() {
        return new WarningDialogCallback() {
            public void onPositive() {
                if (!WifiTetherSwitchBarController.this.mWifiManager.isWifiApEnabled()) {
                    boolean unused = WifiTetherSwitchBarController.this.mIsTetherTurningOn = true;
                    WifiTetherSwitchBarController.this.startTether();
                }
            }

            public void onNegative() {
                if (!WifiTetherSwitchBarController.this.mWifiManager.isWifiApEnabled()) {
                    WifiTetherSwitchBarController.this.mSwitchBar.setChecked(false);
                }
            }
        };
    }

    /* access modifiers changed from: package-private */
    public void stopTether() {
        this.mSwitchBar.setEnabled(false);
        this.mConnectivityManager.stopTethering(0);
    }

    /* access modifiers changed from: package-private */
    public void startTether() {
        this.mSwitchBar.setEnabled(false);
        this.mConnectivityManager.startTethering(0, true, this.mOnStartTetheringCallback, new Handler(Looper.getMainLooper()));
    }

    /* access modifiers changed from: private */
    public void handleWifiApStateChanged(int i) {
        switch (i) {
            case 10:
                if (this.mSwitch.isChecked()) {
                    this.mSwitch.setChecked(false);
                }
                this.mSwitchBar.setEnabled(false);
                return;
            case 11:
                this.mSwitch.setChecked(false);
                updateWifiSwitch();
                return;
            case 12:
                this.mSwitchBar.setEnabled(false);
                return;
            case 13:
                if (!this.mSwitch.isChecked()) {
                    this.mSwitch.setChecked(true);
                }
                updateWifiSwitch();
                return;
            default:
                this.mSwitch.setChecked(false);
                updateWifiSwitch();
                return;
        }
    }

    /* access modifiers changed from: private */
    public void updateWifiSwitch() {
        this.mIsTetherTurningOn = false;
        this.mSwitchBar.setEnabled(!this.mDataSaverBackend.isDataSaverEnabled());
    }

    public void onDataSaverChanged(boolean z) {
        updateWifiSwitch();
    }
}
