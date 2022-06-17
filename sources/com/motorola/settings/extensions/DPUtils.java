package com.motorola.settings.extensions;

import android.content.Intent;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.DisplaySettings;
import com.android.settings.LegalSettings;
import com.android.settings.TestingSettings;
import com.android.settings.TetherSettings;
import com.android.settings.accessibility.AccessibilitySettings;
import com.android.settings.accounts.AccountDashboardFragment;
import com.android.settings.applications.AppDashboardFragment;
import com.android.settings.applications.specialaccess.SpecialAccessSettings;
import com.android.settings.connecteddevice.AdvancedConnectedDeviceDashboardFragment;
import com.android.settings.connecteddevice.ConnectedDeviceDashboardFragment;
import com.android.settings.datausage.DataUsageSummary;
import com.android.settings.development.DevelopmentSettingsDashboardFragment;
import com.android.settings.deviceinfo.PublicVolumeSettings;
import com.android.settings.deviceinfo.StorageDashboardFragment;
import com.android.settings.deviceinfo.aboutphone.MyDeviceInfoFragment;
import com.android.settings.fuelgauge.PowerUsageSummary;
import com.android.settings.language.LanguageAndInputSettings;
import com.android.settings.location.LocationSettings;
import com.android.settings.network.NetworkDashboardFragment;
import com.android.settings.notification.ConfigureNotificationSettings;
import com.android.settings.notification.SoundSettings;
import com.android.settings.security.EncryptionAndCredential;
import com.android.settings.security.SecuritySettings;
import com.android.settings.security.screenlock.ScreenLockSettings;
import com.android.settings.system.ResetDashboardFragment;
import com.android.settings.system.SystemDashboardFragment;
import com.android.settings.wifi.ConfigureWifiSettings;
import com.android.settings.wifi.tether.WifiTetherSettings;
import com.motorola.extensions.DynamicPreferences;
import java.util.ArrayList;
import java.util.HashMap;

public class DPUtils implements DPIntents {
    static HashMap<Class<?>, Intent> sDynamicMenusIntentMap = new HashMap<>();
    static HashMap<Class<?>, Intent> sDynamicPrefOnCreateIntentMap = new HashMap<>();
    static HashMap<Class<?>, Intent> sDynamicPrefOnCustomLoadIntentMap = new HashMap<>();

    static {
        sDynamicPrefOnCreateIntentMap.put(TetherSettings.class, DPIntents.DYNAMIC_TETHER_SETTINGS_INTENT);
        sDynamicPrefOnCreateIntentMap.put(AccessibilitySettings.class, DPIntents.DYNAMIC_ACCESSIBILITY_SETTINGS_INTENT);
        sDynamicPrefOnCreateIntentMap.put(TestingSettings.class, DPIntents.DYNAMIC_TESTING_SETTINGS_INTENT);
        sDynamicPrefOnCreateIntentMap.put(LegalSettings.class, DPIntents.DYNAMIC_LEGAL_INFO_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(NetworkDashboardFragment.class, DPIntents.DYNAMIC_NETWORK_DASHBOARD_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(ConnectedDeviceDashboardFragment.class, DPIntents.DYNAMIC_CONNECTED_DEVICES_DASHBOARD_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(AppDashboardFragment.class, DPIntents.DYNAMIC_APPS_DASHBOARD_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(ConfigureNotificationSettings.class, DPIntents.DYNAMIC_NOTIFICATIONS_DASHBOARD_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(PowerUsageSummary.class, DPIntents.DYNAMIC_BATTERY_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(DisplaySettings.class, DPIntents.DYNAMIC_DISPLAY_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(SoundSettings.class, DPIntents.DYNAMIC_SOUND_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(StorageDashboardFragment.class, DPIntents.DYNAMIC_STORAGE_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(SecuritySettings.class, DPIntents.DYNAMIC_SECURITY_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(AccountDashboardFragment.class, DPIntents.DYNAMIC_ACCOUNTS_DASHBOARD_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(SystemDashboardFragment.class, DPIntents.DYNAMIC_SYSTEM_DASHBOARD_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(ResetDashboardFragment.class, DPIntents.DYNAMIC_RESET_DASHBOARD_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(ConfigureWifiSettings.class, DPIntents.DYNAMIC_CONFIGURE_WIFI_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(DataUsageSummary.class, DPIntents.DYNAMIC_DATA_USAGE_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(WifiTetherSettings.class, DPIntents.DYNAMIC_MOBILE_HOTSPOT_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(AdvancedConnectedDeviceDashboardFragment.class, DPIntents.DYNAMIC_CONNECTED_DEVICES_CONNECTION_PREFERENCES_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(ConfigureNotificationSettings.class, DPIntents.DYNAMIC_CONFIGURE_NOTIFICATIONS_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(SpecialAccessSettings.class, DPIntents.DYNAMIC_SPECIAL_APP_ACCESS_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(PublicVolumeSettings.class, DPIntents.DYNAMIC_PUBLIC_STORAGE_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(ScreenLockSettings.class, DPIntents.DYNAMIC_SECURITY_LOCKSCREEN_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(LocationSettings.class, DPIntents.DYNAMIC_LOCATION_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(EncryptionAndCredential.class, DPIntents.DYNAMIC_ENCRYPTION_AND_CREDENTIAL_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(MyDeviceInfoFragment.class, DPIntents.DYNAMIC_DEVICE_INFO_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(LanguageAndInputSettings.class, DPIntents.DYNAMIC_LANGUAGE_SETTINGS_INTENT);
        sDynamicPrefOnCustomLoadIntentMap.put(DevelopmentSettingsDashboardFragment.class, DPIntents.DYNAMIC_DEVELOPMENT_SETTINGS_INTENT);
    }

    public static boolean isOkToAddOrOverridePrefsPostCreate(Object obj) {
        return sDynamicPrefOnCreateIntentMap.containsKey(obj.getClass());
    }

    public static boolean isOkToAddOrOverridePrefsOnCustomLoad(Object obj) {
        return sDynamicPrefOnCustomLoadIntentMap.containsKey(obj.getClass());
    }

    private static Intent getDynamicPrefsIntent(Object obj) {
        if (sDynamicPrefOnCreateIntentMap.containsKey(obj.getClass())) {
            return sDynamicPrefOnCreateIntentMap.get(obj.getClass());
        }
        if (sDynamicPrefOnCustomLoadIntentMap.containsKey(obj.getClass())) {
            return sDynamicPrefOnCustomLoadIntentMap.get(obj.getClass());
        }
        return null;
    }

    public static void addOrOverridePreferences(PreferenceFragmentCompat preferenceFragmentCompat, ArrayList<Preference> arrayList, ArrayList<Preference> arrayList2) {
        if (preferenceFragmentCompat != null) {
            PreferenceScreen preferenceScreen = preferenceFragmentCompat.getPreferenceScreen();
            Intent dynamicPrefsIntent = getDynamicPrefsIntent(preferenceFragmentCompat);
            if (preferenceScreen != null && dynamicPrefsIntent != null) {
                DynamicPreferences.addOrOverridePreferences(preferenceScreen, arrayList, arrayList2, dynamicPrefsIntent);
            }
        }
    }
}
