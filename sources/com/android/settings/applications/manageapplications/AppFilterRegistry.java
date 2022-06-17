package com.android.settings.applications.manageapplications;

import com.android.settings.C1992R$string;
import com.android.settings.applications.AppStateAlarmsAndRemindersBridge;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.applications.AppStateInstallAppsBridge;
import com.android.settings.applications.AppStateManageExternalStorageBridge;
import com.android.settings.applications.AppStateMediaManagementAppsBridge;
import com.android.settings.applications.AppStateNotificationBridge;
import com.android.settings.applications.AppStateOverlayBridge;
import com.android.settings.applications.AppStatePowerBridge;
import com.android.settings.applications.AppStateUsageBridge;
import com.android.settings.applications.AppStateWriteSettingsBridge;
import com.android.settings.wifi.AppStateChangeWifiStateBridge;
import com.android.settingslib.applications.ApplicationsState;
import com.motorola.settings.display.edge.EndlessApplicationsBridge;
import com.motorola.settings.display.fullscreen.FullscreenAppsBridge;

public class AppFilterRegistry {
    private static AppFilterRegistry sRegistry;
    private final AppFilterItem[] mFilters;

    public int getDefaultFilterType(int i) {
        switch (i) {
            case 1:
                return 2;
            case 4:
                return 10;
            case 5:
                return 0;
            case 6:
                return 11;
            case 7:
                return 12;
            case 8:
                return 13;
            case 10:
                return 15;
            case 11:
                return 17;
            case 12:
                return 18;
            case 13:
                return 19;
            case 14:
            case 15:
                return 20;
            default:
                return 4;
        }
    }

    private AppFilterRegistry() {
        AppFilterItem[] appFilterItemArr = new AppFilterItem[26];
        this.mFilters = appFilterItemArr;
        ApplicationsState.AppFilter appFilter = AppStatePowerBridge.FILTER_POWER_ALLOWLISTED;
        ApplicationsState.AppFilter appFilter2 = ApplicationsState.FILTER_ALL_ENABLED;
        appFilterItemArr[0] = new AppFilterItem(new ApplicationsState.CompoundFilter(appFilter, appFilter2), 0, C1992R$string.high_power_filter_on);
        ApplicationsState.CompoundFilter compoundFilter = new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_WITHOUT_DISABLED_UNTIL_USED, appFilter2);
        int i = C1992R$string.filter_all_apps;
        appFilterItemArr[1] = new AppFilterItem(compoundFilter, 1, i);
        appFilterItemArr[4] = new AppFilterItem(ApplicationsState.FILTER_EVERYTHING, 4, i);
        appFilterItemArr[5] = new AppFilterItem(appFilter2, 5, C1992R$string.filter_enabled_apps);
        appFilterItemArr[7] = new AppFilterItem(ApplicationsState.FILTER_DISABLED, 7, C1992R$string.filter_apps_disabled);
        appFilterItemArr[6] = new AppFilterItem(ApplicationsState.FILTER_INSTANT, 6, C1992R$string.filter_instant_apps);
        appFilterItemArr[2] = new AppFilterItem(AppStateNotificationBridge.FILTER_APP_NOTIFICATION_RECENCY, 2, C1992R$string.sort_order_recent_notification);
        appFilterItemArr[3] = new AppFilterItem(AppStateNotificationBridge.FILTER_APP_NOTIFICATION_FREQUENCY, 3, C1992R$string.sort_order_frequent_notification);
        appFilterItemArr[8] = new AppFilterItem(ApplicationsState.FILTER_PERSONAL, 8, C1992R$string.category_personal);
        appFilterItemArr[9] = new AppFilterItem(ApplicationsState.FILTER_WORK, 9, C1992R$string.category_work);
        appFilterItemArr[10] = new AppFilterItem(AppStateUsageBridge.FILTER_APP_USAGE, 10, i);
        appFilterItemArr[11] = new AppFilterItem(AppStateOverlayBridge.FILTER_SYSTEM_ALERT_WINDOW, 11, C1992R$string.filter_overlay_apps);
        ApplicationsState.AppFilter appFilter3 = AppStateWriteSettingsBridge.FILTER_WRITE_SETTINGS;
        int i2 = C1992R$string.filter_write_settings_apps;
        appFilterItemArr[12] = new AppFilterItem(appFilter3, 12, i2);
        appFilterItemArr[13] = new AppFilterItem(AppStateInstallAppsBridge.FILTER_APP_SOURCES, 13, C1992R$string.filter_install_sources_apps);
        appFilterItemArr[15] = new AppFilterItem(AppStateChangeWifiStateBridge.FILTER_CHANGE_WIFI_STATE, 15, i2);
        appFilterItemArr[16] = new AppFilterItem(AppStateNotificationBridge.FILTER_APP_NOTIFICATION_BLOCKED, 16, C1992R$string.filter_notif_blocked_apps);
        appFilterItemArr[17] = new AppFilterItem(AppStateManageExternalStorageBridge.FILTER_MANAGE_EXTERNAL_STORAGE, 17, C1992R$string.filter_manage_external_storage);
        appFilterItemArr[18] = new AppFilterItem(AppStateAlarmsAndRemindersBridge.FILTER_CLOCK_APPS, 18, C1992R$string.alarms_and_reminders_title);
        appFilterItemArr[19] = new AppFilterItem(AppStateMediaManagementAppsBridge.FILTER_MEDIA_MANAGEMENT_APPS, 19, C1992R$string.media_management_apps_title);
        appFilterItemArr[20] = new AppFilterItem(AppStateBaseBridge.FILTER_APPS_LIST_RECENT, 20, C1992R$string.manage_apps_filter_recents);
        appFilterItemArr[21] = new AppFilterItem(AppStateBaseBridge.FILTER_APPS_LIST_ALPHA, 21, C1992R$string.manage_apps_filter_alpha);
        ApplicationsState.AppFilter appFilter4 = FullscreenAppsBridge.FILTER_APPS_FULL_SCREEN_ENABLED;
        int i3 = C1992R$string.manage_apps_filter_enabled;
        appFilterItemArr[22] = new AppFilterItem(appFilter4, 22, i3);
        ApplicationsState.AppFilter appFilter5 = FullscreenAppsBridge.FILTER_APPS_FULL_SCREEN_DISABLED;
        int i4 = C1992R$string.manage_apps_filter_disabled;
        appFilterItemArr[23] = new AppFilterItem(appFilter5, 23, i4);
        appFilterItemArr[24] = new AppFilterItem(EndlessApplicationsBridge.FILTER_APP_ENDLESS_ENABLED, 24, i3);
        appFilterItemArr[25] = new AppFilterItem(EndlessApplicationsBridge.FILTER_APP_ENDLESS_DISABLED, 25, i4);
    }

    public static AppFilterRegistry getInstance() {
        if (sRegistry == null) {
            sRegistry = new AppFilterRegistry();
        }
        return sRegistry;
    }

    public AppFilterItem get(int i) {
        return this.mFilters[i];
    }
}
