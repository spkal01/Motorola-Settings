package com.android.settings.wifi.tether;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.FeatureFlagUtils;
import android.util.Log;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settingslib.TetherUtil;
import com.android.settingslib.core.AbstractPreferenceController;
import com.motorola.settings.wifi.tether.WifiTetherHiddenSSIDPreferenceController;
import com.motorola.settings.wifi.tether.WifiTetherManageDevicesController;
import com.motorola.settings.wifi.tether.WifiTetherTimeoutPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class WifiTetherSettings extends RestrictedDashboardFragment implements WifiTetherBasePreferenceController.OnTetherConfigUpdateListener {
    static final String KEY_WIFI_TETHER_AUTO_OFF = "wifi_tether_auto_turn_off";
    static final String KEY_WIFI_TETHER_MAXIMIZE_COMPATIBILITY = "wifi_tether_maximize_compatibility";
    static final String KEY_WIFI_TETHER_NETWORK_NAME = "wifi_tether_network_name";
    static final String KEY_WIFI_TETHER_NETWORK_PASSWORD = "wifi_tether_network_password";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.wifi_tether_settings) {
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!TetherUtil.isTetherAvailable(context)) {
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_NETWORK_NAME);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_NETWORK_PASSWORD);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_AUTO_OFF);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_MAXIMIZE_COMPATIBILITY);
                nonIndexableKeys.add("wifi_tether_hidden_network");
                nonIndexableKeys.add("wifi_tether_timeout_settings");
                nonIndexableKeys.add("wifi_tether_manage_devices");
            }
            nonIndexableKeys.add("wifi_tether_settings_screen");
            return nonIndexableKeys;
        }

        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return !FeatureFlagUtils.isEnabled(context, "settings_tether_all_in_one");
        }

        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return WifiTetherSettings.buildPreferenceControllers(context, (WifiTetherBasePreferenceController.OnTetherConfigUpdateListener) null);
        }
    };
    private static final IntentFilter TETHER_STATE_CHANGE_FILTER;
    private WifiTetherHiddenSSIDPreferenceController mHiddenSSIDPreferenceController;
    private WifiTetherMaximizeCompatibilityPreferenceController mMaxCompatibilityPrefController;
    private WifiTetherPasswordPreferenceController mPasswordPreferenceController;
    /* access modifiers changed from: private */
    public boolean mRestartWifiApAfterConfigChange;
    private WifiTetherSSIDPreferenceController mSSIDPreferenceController;
    private WifiTetherSecurityPreferenceController mSecurityPreferenceController;
    private WifiTetherSwitchBarController mSwitchBarController;
    TetherChangeReceiver mTetherChangeReceiver;
    private boolean mUnavailable;
    /* access modifiers changed from: private */
    public WifiManager mWifiManager;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "WifiTetherSettings";
    }

    public int getMetricsCategory() {
        return 1014;
    }

    static {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.TETHER_STATE_CHANGED");
        TETHER_STATE_CHANGE_FILTER = intentFilter;
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
    }

    public WifiTetherSettings() {
        super("no_config_tethering");
    }

    public int getHelpResource() {
        return C1992R$string.help_uri_wifi_hotspot;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setIfOnlyAvailableForAdmins(true);
        if (isUiRestricted()) {
            this.mUnavailable = true;
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mTetherChangeReceiver = new TetherChangeReceiver();
        this.mSSIDPreferenceController = (WifiTetherSSIDPreferenceController) use(WifiTetherSSIDPreferenceController.class);
        this.mSecurityPreferenceController = (WifiTetherSecurityPreferenceController) use(WifiTetherSecurityPreferenceController.class);
        this.mPasswordPreferenceController = (WifiTetherPasswordPreferenceController) use(WifiTetherPasswordPreferenceController.class);
        this.mMaxCompatibilityPrefController = (WifiTetherMaximizeCompatibilityPreferenceController) use(WifiTetherMaximizeCompatibilityPreferenceController.class);
        this.mHiddenSSIDPreferenceController = (WifiTetherHiddenSSIDPreferenceController) use(WifiTetherHiddenSSIDPreferenceController.class);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (!this.mUnavailable) {
            SettingsActivity settingsActivity = (SettingsActivity) getActivity();
            SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
            switchBar.setTitle(getContext().getString(C1992R$string.use_wifi_hotsopt_main_switch_title));
            this.mSwitchBarController = new WifiTetherSwitchBarController(settingsActivity, switchBar);
            getSettingsLifecycle().addObserver(this.mSwitchBarController);
            switchBar.show();
        }
    }

    public void onStart() {
        super.onStart();
        if (this.mUnavailable) {
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(C1992R$string.tethering_settings_not_available);
            }
            getPreferenceScreen().removeAll();
            return;
        }
        Context context = getContext();
        if (context != null) {
            context.registerReceiver(this.mTetherChangeReceiver, TETHER_STATE_CHANGE_FILTER);
        }
    }

    public void onStop() {
        Context context;
        super.onStop();
        if (!this.mUnavailable && (context = getContext()) != null) {
            context.unregisterReceiver(this.mTetherChangeReceiver);
        }
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.wifi_tether_settings;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, new WifiTetherSettings$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WifiTetherSSIDPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherSecurityPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherPasswordPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherHiddenSSIDPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherTimeoutPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherAutoOffPreferenceController(context, KEY_WIFI_TETHER_AUTO_OFF));
        arrayList.add(new WifiTetherMaximizeCompatibilityPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherManageDevicesController(context, "wifi_tether_manage_devices"));
        return arrayList;
    }

    public void onTetherConfigUpdated(AbstractPreferenceController abstractPreferenceController) {
        SoftApConfiguration buildNewConfig = buildNewConfig();
        this.mPasswordPreferenceController.setSecurityType(buildNewConfig.getSecurityType());
        if (this.mWifiManager.getWifiApState() == 13) {
            Log.d("TetheringSettings", "Wifi AP config changed while enabled, stop and restart");
            this.mRestartWifiApAfterConfigChange = true;
            this.mSwitchBarController.stopTether();
        }
        this.mWifiManager.setSoftApConfiguration(buildNewConfig);
    }

    private SoftApConfiguration buildNewConfig() {
        SoftApConfiguration.Builder builder = new SoftApConfiguration.Builder();
        int securityType = this.mSecurityPreferenceController.getSecurityType();
        builder.setSsid(this.mSSIDPreferenceController.getSSID());
        builder.setHiddenSsid(this.mHiddenSSIDPreferenceController.getHiddenNetworkStatus());
        if (securityType != 0) {
            builder.setPassphrase(this.mPasswordPreferenceController.getPasswordValidated(securityType), securityType);
        }
        this.mMaxCompatibilityPrefController.setupMaximizeCompatibility(builder);
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        if (softApConfiguration != null) {
            if (softApConfiguration.getMaxNumberOfClients() > 0) {
                builder.setMaxNumberOfClients(softApConfiguration.getMaxNumberOfClients());
            }
            builder.setAutoShutdownEnabled(softApConfiguration.isAutoShutdownEnabled());
            builder.setShutdownTimeoutMillis(softApConfiguration.getShutdownTimeoutMillis());
            builder.setClientControlByUserEnabled(softApConfiguration.isClientControlByUserEnabled());
            builder.setAllowedClientList(softApConfiguration.getAllowedClientList());
            builder.setBlockedClientList(softApConfiguration.getBlockedClientList());
        }
        return builder.build();
    }

    /* access modifiers changed from: private */
    public void startTether() {
        this.mRestartWifiApAfterConfigChange = false;
        this.mSwitchBarController.startTether();
    }

    /* access modifiers changed from: private */
    public void updateDisplayWithNewConfig() {
        ((WifiTetherSSIDPreferenceController) use(WifiTetherSSIDPreferenceController.class)).updateDisplay();
        ((WifiTetherSecurityPreferenceController) use(WifiTetherSecurityPreferenceController.class)).updateDisplay();
        ((WifiTetherPasswordPreferenceController) use(WifiTetherPasswordPreferenceController.class)).updateDisplay();
        ((WifiTetherMaximizeCompatibilityPreferenceController) use(WifiTetherMaximizeCompatibilityPreferenceController.class)).updateDisplay();
        ((WifiTetherHiddenSSIDPreferenceController) use(WifiTetherHiddenSSIDPreferenceController.class)).updateDisplay();
        ((WifiTetherTimeoutPreferenceController) use(WifiTetherTimeoutPreferenceController.class)).updateDisplay();
    }

    class TetherChangeReceiver extends BroadcastReceiver {
        TetherChangeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("WifiTetherSettings", "updating display config due to receiving broadcast action " + action);
            WifiTetherSettings.this.updateDisplayWithNewConfig();
            if (action.equals("android.net.conn.TETHER_STATE_CHANGED")) {
                if (WifiTetherSettings.this.mWifiManager.getWifiApState() == 11 && WifiTetherSettings.this.mRestartWifiApAfterConfigChange) {
                    WifiTetherSettings.this.startTether();
                }
            } else if (action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED") && intent.getIntExtra("wifi_state", 0) == 11 && WifiTetherSettings.this.mRestartWifiApAfterConfigChange) {
                WifiTetherSettings.this.startTether();
            }
        }
    }
}
