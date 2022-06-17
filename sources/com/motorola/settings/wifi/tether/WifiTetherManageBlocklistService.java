package com.motorola.settings.wifi.tether;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.MacAddress;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import java.util.List;
import java.util.Random;

public class WifiTetherManageBlocklistService extends Service {
    private SoftApConfiguration mConfig;
    private WifiManager mWifiManager;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        WifiManager wifiManager = (WifiManager) getSystemService("wifi");
        this.mWifiManager = wifiManager;
        this.mConfig = wifiManager.getSoftApConfiguration();
        if (intent.hasExtra("blocklistMac")) {
            String stringExtra = intent.getStringExtra("blocklistMac");
            disconnectionStart(stringExtra);
            Intent intent2 = new Intent(this, WifiTetherManageBlocklistService.class);
            intent2.putExtra("blocklistMacDone", stringExtra);
            ((AlarmManager) getSystemService("alarm")).set(0, System.currentTimeMillis() + 300000, PendingIntent.getService(this, new Random().nextInt(), intent2, 67108864));
            return 2;
        }
        if (intent.hasExtra("blocklistMacDone")) {
            disconnectionDone(intent.getStringExtra("blocklistMacDone"));
        }
        return 2;
    }

    private void disconnectionStart(String str) {
        List blockedClientList = this.mConfig.getBlockedClientList();
        List allowedClientList = this.mConfig.getAllowedClientList();
        MacAddress fromString = MacAddress.fromString(str);
        allowedClientList.remove(fromString);
        blockedClientList.add(fromString);
        this.mWifiManager.setSoftApConfiguration(new SoftApConfiguration.Builder(this.mConfig).setAllowedClientList(allowedClientList).setBlockedClientList(blockedClientList).build());
        getSharedPreferences("WIFI_TETHER_ALLOWED_DEVICE_SHARED_PREF", 0).edit().remove(str.toUpperCase()).apply();
    }

    private void disconnectionDone(String str) {
        List blockedClientList = this.mConfig.getBlockedClientList();
        blockedClientList.remove(MacAddress.fromString(str));
        this.mWifiManager.setSoftApConfiguration(new SoftApConfiguration.Builder(this.mConfig).setBlockedClientList(blockedClientList).build());
    }
}
