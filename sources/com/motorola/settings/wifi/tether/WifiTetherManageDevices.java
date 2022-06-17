package com.motorola.settings.wifi.tether;

import android.content.Context;
import android.net.TetheredClient;
import android.net.TetheringManager;
import android.net.wifi.SoftApCapability;
import android.net.wifi.WifiClient;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import androidx.preference.Preference;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WifiTetherManageDevices extends RestrictedDashboardFragment implements TetheringManager.TetheringEventCallback {
    /* access modifiers changed from: private */
    public WifiTetherConnectedDevicesController mController;
    private Handler mHandler;
    private WifiManager.SoftApCallback mSoftApCallback;
    private TetheringManager mTetheringManager;
    private WifiManager mWifiManager;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "WifiTetherManageDevices";
    }

    public int getMetricsCategory() {
        return 1014;
    }

    public WifiTetherManageDevices() {
        super("no_config_tethering");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mWifiManager = (WifiManager) getContext().getSystemService("wifi");
        this.mTetheringManager = (TetheringManager) getContext().getSystemService("tethering");
        initWifiTetherSoftApCallback();
        this.mController = new WifiTetherConnectedDevicesController(getContext(), (WifiProgressCategory) findPreference("wifi_tether_connected_devices"));
    }

    public void onStart() {
        super.onStart();
        WifiManager wifiManager = this.mWifiManager;
        if (wifiManager != null) {
            wifiManager.registerSoftApCallback(new HandlerExecutor(this.mHandler), this.mSoftApCallback);
        }
        TetheringManager tetheringManager = this.mTetheringManager;
        if (tetheringManager != null) {
            tetheringManager.registerTetheringEventCallback(new HandlerExecutor(this.mHandler), this);
        }
    }

    public void onStop() {
        super.onStop();
        WifiManager wifiManager = this.mWifiManager;
        if (wifiManager != null) {
            wifiManager.unregisterSoftApCallback(this.mSoftApCallback);
        }
        TetheringManager tetheringManager = this.mTetheringManager;
        if (tetheringManager != null) {
            tetheringManager.unregisterTetheringEventCallback(this);
        }
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.wifi_tether_manage_devices;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WifiTetherDeviceLimitController(context, (WifiTetherBasePreferenceController.OnTetherConfigUpdateListener) null));
        arrayList.add(new WifiTetherAllowedDevicesController(context, "wifi_tether_allowed_devices"));
        return arrayList;
    }

    public void onClientsChanged(Collection<TetheredClient> collection) {
        for (TetheredClient addOrUpdateDevice : collection) {
            this.mController.addOrUpdateDevice(addOrUpdateDevice);
        }
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        this.mController.preferenceClicked(preference.getKey());
        return super.onPreferenceTreeClick(preference);
    }

    private void initWifiTetherSoftApCallback() {
        this.mSoftApCallback = new WifiManager.SoftApCallback() {
            public void onStateChanged(int i, int i2) {
            }

            public void onConnectedClientsChanged(List<WifiClient> list) {
                WifiTetherManageDevices.this.mController.addOrUpdateDevices(list);
            }

            public void onCapabilityChanged(SoftApCapability softApCapability) {
                ((WifiTetherDeviceLimitController) WifiTetherManageDevices.this.use(WifiTetherDeviceLimitController.class)).setSoftApCapability(softApCapability);
            }
        };
    }
}
