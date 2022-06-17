package com.motorola.settings.wifi.tether;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.MacAddress;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WifiTetherAllowedDevices extends RestrictedDashboardFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static SharedPreferences mSharedPrefs;
    private WifiTetherAllowAllDevicesController mAllowAllDevicesController;
    private WifiTetherWhitelistController mWhitelistController;
    private WifiManager mWifiManager;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "WifiTetherAllowedDevices";
    }

    public int getMetricsCategory() {
        return 1014;
    }

    public WifiTetherAllowedDevices() {
        super("no_config_tethering");
    }

    public void onStart() {
        super.onStart();
        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
        this.mAllowAllDevicesController.updateDisplay();
        this.mWhitelistController.updateDisplay();
    }

    public void onStop() {
        super.onStop();
        mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.wifi_tether_allowed_devices;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        mSharedPrefs = context.getSharedPreferences("WIFI_TETHER_ALLOWED_DEVICE_SHARED_PREF", 0);
        updateSharedPref();
        ArrayList arrayList = new ArrayList();
        this.mWhitelistController = new WifiTetherWhitelistController(context, mSharedPrefs);
        this.mAllowAllDevicesController = new WifiTetherAllowAllDevicesController(context, mSharedPrefs);
        arrayList.add(this.mWhitelistController);
        arrayList.add(this.mAllowAllDevicesController);
        return arrayList;
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        boolean onPreferenceTreeClick = super.onPreferenceTreeClick(preference);
        if (preference.getKey().equals(this.mAllowAllDevicesController.getPreferenceKey())) {
            this.mWhitelistController.updateDisplay();
        } else {
            this.mWhitelistController.preferenceClicked(preference.getKey());
        }
        return onPreferenceTreeClick;
    }

    private void updateSharedPref() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.putBoolean("clientControlByUser", softApConfiguration.isClientControlByUserEnabled());
        Map<String, ?> all = mSharedPrefs.getAll();
        for (MacAddress macAddress : softApConfiguration.getAllowedClientList()) {
            String upperCase = macAddress.toString().toUpperCase();
            if (!all.containsKey(upperCase)) {
                edit.putString(upperCase, "");
            }
        }
        edit.apply();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        Resources resources = getContext().getResources();
        List blockedClientList = softApConfiguration.getBlockedClientList();
        ArrayList arrayList = new ArrayList();
        Map<String, ?> all = mSharedPrefs.getAll();
        all.remove("clientControlByUser");
        if (!all.isEmpty()) {
            for (Map.Entry next : all.entrySet()) {
                MacAddress fromString = MacAddress.fromString((String) next.getKey());
                arrayList.add(fromString);
                blockedClientList.remove(fromString);
                if (((String) next.getValue()).length() == 0) {
                    all.put((String) next.getKey(), resources.getString(C1992R$string.wifi_tether_unknown_device));
                }
            }
        }
        this.mWifiManager.setSoftApConfiguration(new SoftApConfiguration.Builder(softApConfiguration).setClientControlByUserEnabled(mSharedPrefs.getBoolean("clientControlByUser", false)).setBlockedClientList(blockedClientList).setAllowedClientList(arrayList).build());
        this.mAllowAllDevicesController.updateDisplay();
        this.mWhitelistController.updateDisplay();
    }
}
