package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.telecom.TelecomManager;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import androidx.collection.ArrayMap;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.network.MobileDataEnabledListener;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.DataConnectivityListener;
import com.android.settings.network.telephony.MobileNetworkActivity;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.SignalStrengthListener;
import com.android.settings.network.telephony.TelephonyDisplayInfoListener;
import com.android.settings.widget.GearPreference;
import com.android.settings.widget.MutableGearPreference;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.SignalIcon$MobileIconGroup;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.mobile.MobileMappings;
import com.android.settingslib.mobile.TelephonyIcons;
import com.android.settingslib.net.SignalStrengthUtil;
import com.android.wifitrackerlib.WifiEntry;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SubscriptionsPreferenceController extends AbstractPreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient, MobileDataEnabledListener.Client, DataConnectivityListener.Client, SignalStrengthListener.Callback, TelephonyDisplayInfoListener.Callback {
    /* access modifiers changed from: private */
    public MobileMappings.Config mConfig = null;
    final BroadcastReceiver mConnectionChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
                SubscriptionsPreferenceController subscriptionsPreferenceController = SubscriptionsPreferenceController.this;
                MobileMappings.Config unused = subscriptionsPreferenceController.mConfig = subscriptionsPreferenceController.mSubsPrefCtrlInjector.getConfig(SubscriptionsPreferenceController.this.mContext);
                SubscriptionsPreferenceController.this.update();
            } else if (action.equals("android.net.wifi.supplicant.CONNECTION_CHANGE")) {
                SubscriptionsPreferenceController.this.update();
            }
        }
    };
    private DataConnectivityListener mConnectivityListener;
    private MobileDataEnabledListener mDataEnabledListener;
    private PreferenceGroup mPreferenceGroup;
    private String mPreferenceGroupKey;
    private SignalStrengthListener mSignalStrengthListener;
    private int mStartOrder;
    private MutableGearPreference mSubsGearPref;
    /* access modifiers changed from: private */
    public SubsPrefCtrlInjector mSubsPrefCtrlInjector;
    private SubscriptionManager mSubscriptionManager;
    private Map<Integer, Preference> mSubscriptionPreferences;
    private SubscriptionsChangeListener mSubscriptionsListener;
    private TelephonyDisplayInfo mTelephonyDisplayInfo = new TelephonyDisplayInfo(0, 0);
    private TelephonyDisplayInfoListener mTelephonyDisplayInfoListener;
    private TelephonyManager mTelephonyManager;
    private UpdateListener mUpdateListener;
    private WifiPickerTrackerHelper mWifiPickerTrackerHelper;

    public interface UpdateListener {
        void onChildrenUpdated();
    }

    public String getPreferenceKey() {
        return null;
    }

    public SubscriptionsPreferenceController(Context context, Lifecycle lifecycle, UpdateListener updateListener, String str, int i) {
        super(context);
        this.mUpdateListener = updateListener;
        this.mPreferenceGroupKey = str;
        this.mStartOrder = i;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mSubscriptionPreferences = new ArrayMap();
        this.mSubscriptionsListener = new SubscriptionsChangeListener(context, this);
        this.mDataEnabledListener = new MobileDataEnabledListener(context, this);
        this.mConnectivityListener = new DataConnectivityListener(context, this);
        this.mSignalStrengthListener = new SignalStrengthListener(context, this);
        this.mTelephonyDisplayInfoListener = new TelephonyDisplayInfoListener(context, this);
        lifecycle.addObserver(this);
        SubsPrefCtrlInjector createSubsPrefCtrlInjector = createSubsPrefCtrlInjector();
        this.mSubsPrefCtrlInjector = createSubsPrefCtrlInjector;
        this.mConfig = createSubsPrefCtrlInjector.getConfig(this.mContext);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        intentFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        this.mContext.registerReceiver(this.mConnectionChangeReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        BroadcastReceiver broadcastReceiver = this.mConnectionChangeReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mSubscriptionsListener.start();
        this.mDataEnabledListener.start(this.mSubsPrefCtrlInjector.getDefaultDataSubscriptionId());
        this.mConnectivityListener.start();
        this.mSignalStrengthListener.resume();
        this.mTelephonyDisplayInfoListener.resume();
        registerReceiver();
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mSubscriptionsListener.stop();
        this.mDataEnabledListener.stop();
        this.mConnectivityListener.stop();
        this.mSignalStrengthListener.pause();
        this.mTelephonyDisplayInfoListener.pause();
        unRegisterReceiver();
        resetProviderPreferenceSummary();
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(this.mPreferenceGroupKey);
        update();
    }

    /* access modifiers changed from: private */
    public void update() {
        if (this.mPreferenceGroup != null) {
            if (!isAvailable()) {
                MutableGearPreference mutableGearPreference = this.mSubsGearPref;
                if (mutableGearPreference != null) {
                    this.mPreferenceGroup.removePreference(mutableGearPreference);
                }
                for (Preference removePreference : this.mSubscriptionPreferences.values()) {
                    this.mPreferenceGroup.removePreference(removePreference);
                }
                this.mSubscriptionPreferences.clear();
                this.mSignalStrengthListener.updateSubscriptionIds(Collections.emptySet());
                this.mTelephonyDisplayInfoListener.updateSubscriptionIds(Collections.emptySet());
                this.mUpdateListener.onChildrenUpdated();
            } else if (this.mSubsPrefCtrlInjector.isProviderModelEnabled(this.mContext)) {
                updateForProvider();
            } else {
                updateForBase();
            }
        }
    }

    private void updateForProvider() {
        SubscriptionInfo defaultDataSubscriptionInfo = this.mSubscriptionManager.getDefaultDataSubscriptionInfo();
        if (defaultDataSubscriptionInfo == null) {
            this.mPreferenceGroup.removeAll();
            return;
        }
        if (this.mSubsGearPref == null) {
            this.mPreferenceGroup.removeAll();
            MutableGearPreference mutableGearPreference = new MutableGearPreference(this.mContext, (AttributeSet) null);
            this.mSubsGearPref = mutableGearPreference;
            mutableGearPreference.setOnPreferenceClickListener(new SubscriptionsPreferenceController$$ExternalSyntheticLambda0(this));
        }
        this.mSubsGearPref.setOnGearClickListener(new SubscriptionsPreferenceController$$ExternalSyntheticLambda2(this, defaultDataSubscriptionInfo));
        if (!((UserManager) this.mContext.getSystemService(UserManager.class)).isAdminUser()) {
            this.mSubsGearPref.setGearEnabled(false);
        }
        this.mSubsGearPref.setTitle(SubscriptionUtil.getUniqueSubscriptionDisplayName(defaultDataSubscriptionInfo, this.mContext));
        this.mSubsGearPref.setOrder(this.mStartOrder);
        this.mSubsGearPref.setSummary(getMobilePreferenceSummary(defaultDataSubscriptionInfo.getSubscriptionId()));
        this.mSubsGearPref.setIcon(getIcon(defaultDataSubscriptionInfo.getSubscriptionId()));
        this.mPreferenceGroup.addPreference(this.mSubsGearPref);
        ArraySet arraySet = new ArraySet();
        arraySet.add(Integer.valueOf(defaultDataSubscriptionInfo.getSubscriptionId()));
        this.mSignalStrengthListener.updateSubscriptionIds(arraySet);
        this.mTelephonyDisplayInfoListener.updateSubscriptionIds(arraySet);
        this.mUpdateListener.onChildrenUpdated();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateForProvider$0(Preference preference) {
        connectCarrierNetwork();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateForProvider$1(SubscriptionInfo subscriptionInfo, GearPreference gearPreference) {
        startMobileNetworkActivity(this.mContext, subscriptionInfo.getSubscriptionId());
    }

    private CharSequence getMobilePreferenceSummary(int i) {
        NetworkRegistrationInfo networkRegistrationInfo;
        boolean z;
        TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(i);
        if (!createForSubscriptionId.isDataEnabled()) {
            return this.mContext.getString(C1992R$string.mobile_data_off_summary);
        }
        ServiceState serviceState = createForSubscriptionId.getServiceState();
        if (serviceState == null) {
            networkRegistrationInfo = null;
        } else {
            networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, 1);
        }
        if (networkRegistrationInfo == null) {
            z = false;
        } else {
            z = networkRegistrationInfo.isRegistered();
        }
        WifiPickerTrackerHelper wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper;
        boolean z2 = wifiPickerTrackerHelper != null && wifiPickerTrackerHelper.isCarrierNetworkActive();
        String networkType = this.mSubsPrefCtrlInjector.getNetworkType(this.mContext, this.mConfig, this.mTelephonyDisplayInfo, i, z2);
        if (this.mSubsPrefCtrlInjector.isActiveCellularNetwork(this.mContext) || z2) {
            Log.i("SubscriptionsPrefCntrlr", "Active cellular network or active carrier network.");
            Context context = this.mContext;
            networkType = context.getString(C1992R$string.preference_summary_default_combination, new Object[]{context.getString(C1992R$string.mobile_data_connection_active), networkType});
        } else if (!z) {
            networkType = this.mContext.getString(C1992R$string.mobile_data_no_connection);
        }
        return Html.fromHtml(networkType, 0);
    }

    private Drawable getIcon(int i) {
        int i2;
        WifiPickerTrackerHelper wifiPickerTrackerHelper;
        NetworkRegistrationInfo networkRegistrationInfo;
        boolean z;
        TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(i);
        SignalStrength signalStrength = createForSubscriptionId.getSignalStrength();
        boolean z2 = false;
        if (signalStrength == null) {
            i2 = 0;
        } else {
            i2 = signalStrength.getLevel();
        }
        int i3 = 5;
        if (shouldInflateSignalStrength(i)) {
            i2++;
            i3 = 6;
        }
        Drawable icon = this.mSubsPrefCtrlInjector.getIcon(this.mContext, i2, i3, false);
        if (this.mSubsPrefCtrlInjector.isActiveCellularNetwork(this.mContext) || ((wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper) != null && wifiPickerTrackerHelper.isCarrierNetworkActive())) {
            icon.setTint(Utils.getColorAccentDefaultColor(this.mContext));
            return icon;
        }
        ServiceState serviceState = createForSubscriptionId.getServiceState();
        if (serviceState == null) {
            networkRegistrationInfo = null;
        } else {
            networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, 1);
        }
        if (networkRegistrationInfo == null) {
            z = false;
        } else {
            z = networkRegistrationInfo.isRegistered();
        }
        if (serviceState != null && serviceState.getState() == 0) {
            z2 = true;
        }
        return (z || z2) ? icon : this.mContext.getDrawable(C1983R$drawable.ic_signal_strength_zero_bar_no_internet);
    }

    private void resetProviderPreferenceSummary() {
        MutableGearPreference mutableGearPreference = this.mSubsGearPref;
        if (mutableGearPreference != null) {
            mutableGearPreference.setSummary((CharSequence) "");
        }
    }

    private void updateForBase() {
        Map<Integer, Preference> map = this.mSubscriptionPreferences;
        this.mSubscriptionPreferences = new ArrayMap();
        int i = this.mStartOrder;
        ArraySet arraySet = new ArraySet();
        int defaultDataSubscriptionId = this.mSubsPrefCtrlInjector.getDefaultDataSubscriptionId();
        for (SubscriptionInfo next : SubscriptionUtil.getActiveSubscriptions(this.mSubscriptionManager)) {
            int subscriptionId = next.getSubscriptionId();
            if (this.mSubsPrefCtrlInjector.canSubscriptionBeDisplayed(this.mContext, subscriptionId)) {
                arraySet.add(Integer.valueOf(subscriptionId));
                RestrictedPreference restrictedPreference = (RestrictedPreference) map.remove(Integer.valueOf(subscriptionId));
                boolean z = true;
                if (restrictedPreference == null) {
                    restrictedPreference = new RestrictedPreference(this.mPreferenceGroup.getContext());
                    restrictedPreference.checkRestrictionAndSetDisabled("no_config_mobile_networks");
                    restrictedPreference.useAdminDisabledSummary(true);
                    this.mPreferenceGroup.addPreference(restrictedPreference);
                }
                restrictedPreference.setTitle(SubscriptionUtil.getUniqueSubscriptionDisplayName(next, this.mContext));
                if (subscriptionId != defaultDataSubscriptionId) {
                    z = false;
                }
                restrictedPreference.setSummary((CharSequence) getSummary(subscriptionId, z));
                setIcon(restrictedPreference, subscriptionId, z);
                restrictedPreference.setOrder(i);
                restrictedPreference.setOnPreferenceClickListener(new SubscriptionsPreferenceController$$ExternalSyntheticLambda1(this, subscriptionId));
                this.mSubscriptionPreferences.put(Integer.valueOf(subscriptionId), restrictedPreference);
                i++;
            }
        }
        this.mSignalStrengthListener.updateSubscriptionIds(arraySet);
        for (Preference removePreference : map.values()) {
            this.mPreferenceGroup.removePreference(removePreference);
        }
        this.mUpdateListener.onChildrenUpdated();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateForBase$2(int i, Preference preference) {
        startMobileNetworkActivity(this.mContext, i);
        return true;
    }

    private static void startMobileNetworkActivity(Context context, int i) {
        Intent intent = new Intent(context, MobileNetworkActivity.class);
        intent.putExtra("android.provider.extra.SUB_ID", i);
        context.startActivity(intent);
    }

    /* access modifiers changed from: package-private */
    public boolean shouldInflateSignalStrength(int i) {
        return SignalStrengthUtil.shouldInflateSignalStrength(this.mContext, i);
    }

    /* access modifiers changed from: package-private */
    public void setIcon(Preference preference, int i, boolean z) {
        int i2;
        SignalStrength signalStrength = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).getSignalStrength();
        if (signalStrength == null) {
            i2 = 0;
        } else {
            i2 = signalStrength.getLevel();
        }
        int i3 = 5;
        if (shouldInflateSignalStrength(i)) {
            i2++;
            i3 = 6;
        }
        preference.setIcon(this.mSubsPrefCtrlInjector.getIcon(this.mContext, i2, i3, false));
    }

    /* access modifiers changed from: protected */
    public String getSummary(int i, boolean z) {
        String str;
        TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService(TelecomManager.class);
        int subIdForPhoneAccount = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).getSubIdForPhoneAccount(telecomManager.getPhoneAccount(telecomManager.getUserSelectedOutgoingPhoneAccount()));
        int defaultSmsSubscriptionId = this.mSubsPrefCtrlInjector.getDefaultSmsSubscriptionId();
        String str2 = null;
        if (i == subIdForPhoneAccount && i == defaultSmsSubscriptionId) {
            str = this.mContext.getString(C1992R$string.default_for_calls_and_sms);
        } else if (i == subIdForPhoneAccount) {
            str = this.mContext.getString(C1992R$string.default_for_calls);
        } else {
            str = i == defaultSmsSubscriptionId ? this.mContext.getString(C1992R$string.default_for_sms) : null;
        }
        if (z) {
            boolean isDataEnabled = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).isDataEnabled();
            if (isDataEnabled && this.mSubsPrefCtrlInjector.isActiveCellularNetwork(this.mContext)) {
                str2 = this.mContext.getString(C1992R$string.mobile_data_active);
            } else if (!isDataEnabled) {
                str2 = this.mContext.getString(C1992R$string.mobile_data_off);
            } else {
                str2 = this.mContext.getString(C1992R$string.default_for_mobile_data);
            }
        }
        if (str != null && str2 != null) {
            return String.join(System.lineSeparator(), new CharSequence[]{str, str2});
        } else if (str != null) {
            return str;
        } else {
            if (str2 != null) {
                return str2;
            }
            return this.mContext.getString(C1992R$string.subscription_available);
        }
    }

    public boolean isAvailable() {
        List<SubscriptionInfo> activeSubscriptions;
        if (this.mSubscriptionsListener.isAirplaneModeOn() || (activeSubscriptions = SubscriptionUtil.getActiveSubscriptions(this.mSubscriptionManager)) == null) {
            return false;
        }
        if (activeSubscriptions.stream().filter(new SubscriptionsPreferenceController$$ExternalSyntheticLambda3(this)).count() >= ((long) (this.mSubsPrefCtrlInjector.isProviderModelEnabled(this.mContext) ? 1 : 2))) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$isAvailable$3(SubscriptionInfo subscriptionInfo) {
        return this.mSubsPrefCtrlInjector.canSubscriptionBeDisplayed(this.mContext, subscriptionInfo.getSubscriptionId());
    }

    public void onAirplaneModeChanged(boolean z) {
        update();
    }

    public void onSubscriptionsChanged() {
        int defaultDataSubscriptionId = this.mSubsPrefCtrlInjector.getDefaultDataSubscriptionId();
        if (defaultDataSubscriptionId != this.mDataEnabledListener.getSubId()) {
            this.mDataEnabledListener.stop();
            this.mDataEnabledListener.start(defaultDataSubscriptionId);
        }
        update();
    }

    public void onMobileDataEnabledChange() {
        update();
    }

    public void onDataConnectivityChange() {
        update();
    }

    public void onSignalStrengthChanged() {
        update();
    }

    public void onTelephonyDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) {
        this.mTelephonyDisplayInfo = telephonyDisplayInfo;
        update();
    }

    /* access modifiers changed from: package-private */
    public boolean canSubscriptionBeDisplayed(Context context, int i) {
        return SubscriptionUtil.getAvailableSubscription(context, ProxySubscriptionManager.getInstance(context), i) != null;
    }

    public void setWifiPickerTrackerHelper(WifiPickerTrackerHelper wifiPickerTrackerHelper) {
        this.mWifiPickerTrackerHelper = wifiPickerTrackerHelper;
    }

    public void connectCarrierNetwork() {
        WifiPickerTrackerHelper wifiPickerTrackerHelper;
        if (MobileNetworkUtils.isMobileDataEnabled(this.mContext) && (wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper) != null) {
            wifiPickerTrackerHelper.connectCarrierNetwork((WifiEntry.ConnectCallback) null);
        }
    }

    /* access modifiers changed from: package-private */
    public SubsPrefCtrlInjector createSubsPrefCtrlInjector() {
        return new SubsPrefCtrlInjector();
    }

    public static class SubsPrefCtrlInjector {
        public boolean canSubscriptionBeDisplayed(Context context, int i) {
            return SubscriptionUtil.getAvailableSubscription(context, ProxySubscriptionManager.getInstance(context), i) != null;
        }

        public int getDefaultSmsSubscriptionId() {
            return SubscriptionManager.getDefaultSmsSubscriptionId();
        }

        public int getDefaultDataSubscriptionId() {
            return SubscriptionManager.getDefaultDataSubscriptionId();
        }

        public boolean isActiveCellularNetwork(Context context) {
            return MobileNetworkUtils.activeNetworkIsCellular(context);
        }

        public boolean isProviderModelEnabled(Context context) {
            return com.android.settings.Utils.isProviderModelEnabled(context);
        }

        public MobileMappings.Config getConfig(Context context) {
            return MobileMappings.Config.readConfig(context);
        }

        public String getNetworkType(Context context, MobileMappings.Config config, TelephonyDisplayInfo telephonyDisplayInfo, int i) {
            SignalIcon$MobileIconGroup signalIcon$MobileIconGroup = MobileMappings.mapIconSets(config).get(MobileMappings.getIconKey(telephonyDisplayInfo));
            int i2 = signalIcon$MobileIconGroup != null ? signalIcon$MobileIconGroup.dataContentDescription : 0;
            return i2 != 0 ? SubscriptionManager.getResourcesForSubId(context, i).getString(i2) : "";
        }

        public String getNetworkType(Context context, MobileMappings.Config config, TelephonyDisplayInfo telephonyDisplayInfo, int i, boolean z) {
            if (!z) {
                return getNetworkType(context, config, telephonyDisplayInfo, i);
            }
            int i2 = TelephonyIcons.CARRIER_MERGED_WIFI.dataContentDescription;
            return i2 != 0 ? SubscriptionManager.getResourcesForSubId(context, i).getString(i2) : "";
        }

        public Drawable getIcon(Context context, int i, int i2, boolean z) {
            return MobileNetworkUtils.getSignalStrengthIcon(context, i, i2, 0, z);
        }
    }
}
