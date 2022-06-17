package com.motorola.settings.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.util.Log;
import com.android.wifitrackerlib.WifiEntry;
import com.motorola.internal.enterprise.MotoDevicePolicyManager;
import java.util.List;

public class MotoDevicePolicyUtils {
    private static final String TAG = "MotoDevicePolicyUtils";

    public static boolean applyAdminRestriction(WifiEntry wifiEntry, WifiConfiguration wifiConfiguration, Context context) {
        if (context == null) {
            if (Build.IS_DEBUGGABLE) {
                Log.d(TAG, "Can't check admin restriction Context is null...");
            }
            return false;
        }
        MotoDevicePolicyManager motoDevicePolicyManager = (MotoDevicePolicyManager) context.getSystemService("mot_device_policy");
        if (motoDevicePolicyManager != null) {
            if (motoDevicePolicyManager.hasUserRestriction("disallow_wifi_unsecure_networks")) {
                if (wifiEntry != null && wifiEntry.getSecurity() == 0) {
                    if (Build.IS_DEBUGGABLE) {
                        String str = TAG;
                        Log.d(str, wifiEntry.getSsid() + ": Can't connect to unsecure WiFis Networks");
                    }
                    return true;
                } else if (wifiConfiguration != null && wifiConfiguration.allowedKeyManagement.get(0) && !wifiConfiguration.allowedAuthAlgorithms.get(1)) {
                    if (Build.IS_DEBUGGABLE) {
                        String str2 = TAG;
                        Log.d(str2, wifiConfiguration.SSID + ": Can't connect to unsecure WiFis Networks");
                    }
                    return true;
                }
            }
            if (motoDevicePolicyManager.hasUserRestriction("no_wifi_connection_by_ssid")) {
                if (motoDevicePolicyManager.getWifiRestrictionState() == 2) {
                    List blockedSsids = motoDevicePolicyManager.getBlockedSsids();
                    if (blockedSsids == null || wifiEntry == null || !blockedSsids.contains(wifiEntry.getSsid())) {
                        String parsedWifiConfigSSID = getParsedWifiConfigSSID(wifiConfiguration);
                        if (!(blockedSsids == null || wifiConfiguration == null || !blockedSsids.contains(parsedWifiConfigSSID))) {
                            if (Build.IS_DEBUGGABLE) {
                                String str3 = TAG;
                                Log.d(str3, "DISALLOW_WIFI_CONNECTION_BY_SSID policy is active");
                                Log.d(str3, "Can't connect to wifi: " + parsedWifiConfigSSID + " - is in blocked list...");
                            }
                            return true;
                        }
                    } else {
                        if (Build.IS_DEBUGGABLE) {
                            String str4 = TAG;
                            Log.d(str4, "DISALLOW_WIFI_CONNECTION_BY_SSID policy is active");
                            Log.d(str4, "Can't connect to wifi: " + wifiEntry.getSsid() + " - is in blocked list...");
                        }
                        return true;
                    }
                }
                if (motoDevicePolicyManager.getWifiRestrictionState() == 1) {
                    List allowedSsids = motoDevicePolicyManager.getAllowedSsids();
                    if (allowedSsids == null || wifiEntry == null || allowedSsids.contains(wifiEntry.getSsid())) {
                        String parsedWifiConfigSSID2 = getParsedWifiConfigSSID(wifiConfiguration);
                        if (!(allowedSsids == null || wifiConfiguration == null || allowedSsids.contains(parsedWifiConfigSSID2))) {
                            if (Build.IS_DEBUGGABLE) {
                                String str5 = TAG;
                                Log.d(str5, "DISALLOW_WIFI_CONNECTION_BY_SSID policy is active");
                                Log.d(str5, "Can't connect to wifi: " + parsedWifiConfigSSID2 + " - is not in allow list...");
                            }
                            return true;
                        }
                    } else {
                        if (Build.IS_DEBUGGABLE) {
                            String str6 = TAG;
                            Log.d(str6, "DISALLOW_WIFI_CONNECTION_BY_SSID policy is active");
                            Log.d(str6, "Can't connect to wifi: " + wifiEntry.getSsid() + " - is not in allow list...");
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean canAddNetworkWithAdminRestrictions(Context context, WifiConfiguration wifiConfiguration) {
        if (context == null) {
            if (Build.IS_DEBUGGABLE) {
                Log.d(TAG, "Context is null can't check admin restrictions...");
            }
            return false;
        }
        MotoDevicePolicyManager motoDevicePolicyManager = (MotoDevicePolicyManager) context.getSystemService("mot_device_policy");
        if (motoDevicePolicyManager == null || !motoDevicePolicyManager.hasUserRestriction("disallow_wifi_unsecure_networks") || !wifiConfiguration.allowedKeyManagement.get(0) || wifiConfiguration.allowedAuthAlgorithms.get(1)) {
            if (motoDevicePolicyManager != null && motoDevicePolicyManager.hasUserRestriction("no_wifi_connection_by_ssid") && motoDevicePolicyManager.getWifiRestrictionState() == 2) {
                List blockedSsids = motoDevicePolicyManager.getBlockedSsids();
                String parsedWifiConfigSSID = getParsedWifiConfigSSID(wifiConfiguration);
                if (blockedSsids != null && blockedSsids.contains(parsedWifiConfigSSID)) {
                    if (Build.IS_DEBUGGABLE) {
                        String str = TAG;
                        Log.d(str, "The WifiConfiguration is BLOCKED and can't connect to blocked networks!");
                        Log.d(str, "The WifiConfiguration SSID: " + parsedWifiConfigSSID);
                    }
                    startAdminDialog(context, "no_wifi_connection_by_ssid");
                    return false;
                }
            }
            return true;
        }
        if (Build.IS_DEBUGGABLE) {
            String str2 = TAG;
            Log.d(str2, "The WifiConfiguration is Open and can't connect to open networks!");
            Log.d(str2, "The WifiConfiguration SSID: " + wifiConfiguration.SSID);
            Log.d(str2, "The WifiConfiguration is Open? " + wifiConfiguration.allowedKeyManagement.get(0));
        }
        startAdminDialog(context, "disallow_wifi_unsecure_networks");
        return false;
    }

    public static void startAdminDialog(Context context, String str) {
        Intent createMotoAdminSupportIntent;
        MotoDevicePolicyManager motoDevicePolicyManager = (MotoDevicePolicyManager) context.getSystemService("mot_device_policy");
        if (motoDevicePolicyManager != null && (createMotoAdminSupportIntent = motoDevicePolicyManager.createMotoAdminSupportIntent(str)) != null) {
            context.startActivity(createMotoAdminSupportIntent);
        }
    }

    private static String getParsedWifiConfigSSID(WifiConfiguration wifiConfiguration) {
        String str;
        if (wifiConfiguration == null || (str = wifiConfiguration.SSID) == null) {
            return "";
        }
        return str.replaceAll("^\"|\"$", "");
    }
}
