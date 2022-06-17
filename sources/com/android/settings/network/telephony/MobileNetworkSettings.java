package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import com.android.settings.C1985R$id;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.datausage.BillingCyclePreferenceController;
import com.android.settings.datausage.DataUsageSummaryPreferenceController;
import com.android.settings.network.ActiveSubscriptionsListener;
import com.android.settings.network.CarrierWifiTogglePreferenceController;
import com.android.settings.network.telephony.cdma.CdmaSubscriptionPreferenceController;
import com.android.settings.network.telephony.cdma.CdmaSystemSelectPreferenceController;
import com.android.settings.network.telephony.gsm.AutoSelectPreferenceController;
import com.android.settings.network.telephony.gsm.OpenNetworkSelectPagePreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
import com.android.wifitrackerlib.WifiPickerTracker;
import com.motorola.settings.network.MotoMobileNetworkUtils;
import com.motorola.settings.network.MotoTelephonyFactory;
import com.motorola.settings.network.cache.MotoMnsCache;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MobileNetworkSettings extends AbstractMobileNetworkSettings {
    static final String KEY_CLICKED_PREF = "key_clicked_pref";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.mobile_network_settings) {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return ((UserManager) context.getSystemService(UserManager.class)).isAdminUser() && !MotoMobileNetworkUtils.isAirplaneModeOn(context);
        }

        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            String str;
            MotoMnsCache.getIns(context).reset();
            int[] activeSubIds = MotoMnsCache.getIns(context).getActiveSubIds();
            ArrayList arrayList = new ArrayList();
            if (activeSubIds.length < 1) {
                return null;
            }
            if (activeSubIds.length > 1) {
                str = "com.android.settings.network.MobileNetworkListFragment";
            } else {
                str = MotoTelephonyFactory.getMnsClassName(context, activeSubIds[0]);
            }
            for (Integer intValue : MotoTelephonyFactory.getAllSimFragmentXml(context)) {
                arrayList.add(getSearchIndexableResource(context, str, intValue.intValue()));
            }
            return arrayList;
        }

        private SearchIndexableResource getSearchIndexableResource(Context context, String str, int i) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = i;
            searchIndexableResource.className = str;
            return searchIndexableResource;
        }

        public List<String> getNonIndexableKeys(Context context) {
            int i;
            Log.d("NetworkSettings", "getNonIndexableKeys +");
            if (!isPageSearchEnabled(context)) {
                Log.d("NetworkSettings", "getNonIndexableKeys -");
                return getNonIndexableKeysFromXml(context, true);
            }
            ArrayList arrayList = new ArrayList();
            ArrayList<String> arrayList2 = new ArrayList<>();
            int[] activeSubIds = MotoMnsCache.getIns(context).getActiveSubIds();
            for (int i2 : activeSubIds) {
                for (AbstractPreferenceController next : MotoTelephonyFactory.getControllersForSubId(context, i2)) {
                    boolean z = !next.isAvailable();
                    if (next instanceof TelephonyAvailabilityCallback) {
                        i = ((TelephonyAvailabilityCallback) next).getAvailabilityStatus(i2);
                    } else {
                        i = next instanceof BasePreferenceController ? ((BasePreferenceController) next).getAvailabilityStatus() : 0;
                    }
                    boolean z2 = z | (i == 1);
                    String preferenceKey = next.getPreferenceKey();
                    if (z2 && !arrayList.contains(preferenceKey)) {
                        arrayList.add(preferenceKey);
                    } else if (!z2 && !arrayList2.contains(preferenceKey)) {
                        arrayList2.add(preferenceKey);
                    }
                }
            }
            for (String str : arrayList2) {
                if (arrayList.contains(str)) {
                    arrayList.remove(str);
                }
            }
            arrayList.addAll(getNonIndexableKeysFromXml(context, false));
            Log.d("NetworkSettings", "getNonIndexableKeys -");
            return arrayList;
        }
    };
    private ActiveSubscriptionsListener mActiveSubscriptionsListener;
    private int mActiveSubscriptionsListenerCount;
    protected AutoSelectPreferenceController mAutoSelectPreferenceController;
    private String mClickedPrefKey;
    private boolean mDropFirstSubscriptionChangeNotify;
    protected FragmentManager mFragmentManager = null;
    protected Lifecycle mLifecycle = null;
    protected PreferenceManager mPreferenceManager = null;
    protected com.android.settingslib.core.lifecycle.Lifecycle mSettingsLifecycle = null;
    protected int mSubId = -1;
    protected TelephonyManager mTelephonyManager;
    private UserManager mUserManager;

    /* access modifiers changed from: protected */
    public void createMotoPreferenceControllers(Context context, int i) {
    }

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "NetworkSettings";
    }

    public int getMetricsCategory() {
        return 1571;
    }

    /* access modifiers changed from: protected */
    public void updateControllers(List<AbstractPreferenceController> list) {
    }

    public /* bridge */ /* synthetic */ void onExpandButtonClick() {
        super.onExpandButtonClick();
    }

    public MobileNetworkSettings() {
        super("no_config_mobile_networks");
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (super.onPreferenceTreeClick(preference)) {
            return true;
        }
        String key = preference.getKey();
        if (!TextUtils.equals(key, "cdma_system_select_key") && !TextUtils.equals(key, "cdma_subscription_key")) {
            return false;
        }
        if (this.mTelephonyManager.getEmergencyCallbackMode()) {
            startActivityForResult(new Intent("android.telephony.action.SHOW_NOTICE_ECM_BLOCK_OTHERS", (Uri) null), 17);
            this.mClickedPrefKey = key;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public final List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        this.mSubId = getArguments().getInt("android.provider.extra.SUB_ID", MobileNetworkUtils.getSearchableSubscriptionId(context));
        Log.i("NetworkSettings", "display subId: " + this.mSubId);
        ArrayList arrayList = new ArrayList();
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            arrayList.add(new DataUsageSummaryPreferenceController(getActivity(), getSettingsLifecycle(), this, this.mSubId));
        }
        createMotoPreferenceControllers(context, this.mSubId);
        updateControllers(arrayList);
        return arrayList;
    }

    public void onAttach(Context context) {
        Class cls = MobileDataPreferenceController.class;
        super.onAttach(context);
        DataUsageSummaryPreferenceController dataUsageSummaryPreferenceController = (DataUsageSummaryPreferenceController) use(DataUsageSummaryPreferenceController.class);
        if (dataUsageSummaryPreferenceController != null) {
            dataUsageSummaryPreferenceController.init(this.mSubId);
        }
        this.mSettingsLifecycle = getSettingsLifecycle();
        this.mLifecycle = getLifecycle();
        this.mFragmentManager = getFragmentManager();
        this.mPreferenceManager = getPreferenceManager();
        Optional.ofNullable((CallsDefaultSubscriptionController) use(CallsDefaultSubscriptionController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda5(this));
        Optional.ofNullable((SmsDefaultSubscriptionController) use(SmsDefaultSubscriptionController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda24(this));
        Optional.ofNullable((MobileNetworkSwitchController) use(MobileNetworkSwitchController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda19(this));
        Optional.ofNullable((CarrierSettingsVersionPreferenceController) use(CarrierSettingsVersionPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda7(this));
        Optional.ofNullable((BillingCyclePreferenceController) use(BillingCyclePreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda1(this));
        Optional.ofNullable((MmsMessagePreferenceController) use(MmsMessagePreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda17(this));
        Optional.ofNullable((DataDuringCallsPreferenceController) use(DataDuringCallsPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda9(this));
        Optional.ofNullable((DisabledSubscriptionController) use(DisabledSubscriptionController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda14(this));
        Optional.ofNullable((DeleteSimProfilePreferenceController) use(DeleteSimProfilePreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda12(this));
        Optional.ofNullable((DisableSimFooterPreferenceController) use(DisableSimFooterPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda13(this));
        Optional.ofNullable((NrDisabledInDsdsFooterPreferenceController) use(NrDisabledInDsdsFooterPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda21(this));
        Optional.ofNullable((MobileDataPreferenceController) use(cls)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda18(this));
        Optional.ofNullable((MobileDataPreferenceController) use(cls)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda30(this, context));
        Optional.ofNullable((RoamingPreferenceController) use(RoamingPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda23(this));
        Optional.ofNullable((ApnPreferenceController) use(ApnPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda3(this));
        Optional.ofNullable((CarrierPreferenceController) use(CarrierPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda6(this));
        Optional.ofNullable((DataUsagePreferenceController) use(DataUsagePreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda11(this));
        Optional.ofNullable((PreferredNetworkModePreferenceController) use(PreferredNetworkModePreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda22(this));
        Optional.ofNullable((EnabledNetworkModePreferenceController) use(EnabledNetworkModePreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda16(this));
        Optional.ofNullable((DataServiceSetupPreferenceController) use(DataServiceSetupPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda10(this));
        Optional.ofNullable((Enable2gPreferenceController) use(Enable2gPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda15(this));
        Optional.ofNullable((CarrierWifiTogglePreferenceController) use(CarrierWifiTogglePreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda2(this));
        OpenNetworkSelectPagePreferenceController openNetworkSelectPagePrefController = getOpenNetworkSelectPagePrefController();
        this.mAutoSelectPreferenceController = getAutoSelectPreferenceController();
        Optional.ofNullable(openNetworkSelectPagePrefController).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda29(this));
        Optional.ofNullable(this.mAutoSelectPreferenceController).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda34(this, openNetworkSelectPagePrefController));
        Optional.ofNullable((NetworkPreferenceCategoryController) use(NetworkPreferenceCategoryController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda20(this));
        Optional.ofNullable((CdmaSystemSelectPreferenceController) use(CdmaSystemSelectPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda28(this));
        Optional.ofNullable((CdmaSubscriptionPreferenceController) use(CdmaSubscriptionPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda27(this));
        WifiCallingPreferenceController wifiCallingPreferenceController = getWifiCallingPreferenceController();
        VideoCallingPreferenceController videoCallingPreferenceController = getVideoCallingPreferenceController();
        BackupCallingPreferenceController backupCallingPreferenceController = getBackupCallingPreferenceController();
        Optional.ofNullable(wifiCallingPreferenceController).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda26(this));
        Optional.ofNullable(videoCallingPreferenceController).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda25(this));
        Optional.ofNullable(backupCallingPreferenceController).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda4(this));
        Optional.ofNullable((CallingPreferenceCategoryController) use(CallingPreferenceCategoryController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda35(wifiCallingPreferenceController, videoCallingPreferenceController, backupCallingPreferenceController));
        Optional.ofNullable((Enhanced4gLtePreferenceController) use(Enhanced4gLtePreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda33(this, videoCallingPreferenceController));
        Optional.ofNullable((Enhanced4gCallingPreferenceController) use(Enhanced4gCallingPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda32(this, videoCallingPreferenceController));
        Optional.ofNullable((Enhanced4gAdvancedCallingPreferenceController) use(Enhanced4gAdvancedCallingPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda31(this, videoCallingPreferenceController));
        Optional.ofNullable((ContactDiscoveryPreferenceController) use(ContactDiscoveryPreferenceController.class)).ifPresent(new MobileNetworkSettings$$ExternalSyntheticLambda8(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$0(CallsDefaultSubscriptionController callsDefaultSubscriptionController) {
        callsDefaultSubscriptionController.init(this.mLifecycle);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$1(SmsDefaultSubscriptionController smsDefaultSubscriptionController) {
        smsDefaultSubscriptionController.init(this.mLifecycle);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$2(MobileNetworkSwitchController mobileNetworkSwitchController) {
        mobileNetworkSwitchController.init(this.mLifecycle, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$3(CarrierSettingsVersionPreferenceController carrierSettingsVersionPreferenceController) {
        carrierSettingsVersionPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$4(BillingCyclePreferenceController billingCyclePreferenceController) {
        billingCyclePreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$5(MmsMessagePreferenceController mmsMessagePreferenceController) {
        mmsMessagePreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$6(DataDuringCallsPreferenceController dataDuringCallsPreferenceController) {
        dataDuringCallsPreferenceController.init(this.mLifecycle, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$7(DisabledSubscriptionController disabledSubscriptionController) {
        disabledSubscriptionController.init(this.mLifecycle, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$8(DeleteSimProfilePreferenceController deleteSimProfilePreferenceController) {
        deleteSimProfilePreferenceController.init(this.mSubId, this, 18);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$9(DisableSimFooterPreferenceController disableSimFooterPreferenceController) {
        disableSimFooterPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$10(NrDisabledInDsdsFooterPreferenceController nrDisabledInDsdsFooterPreferenceController) {
        nrDisabledInDsdsFooterPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$11(MobileDataPreferenceController mobileDataPreferenceController) {
        mobileDataPreferenceController.init(this.mFragmentManager, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$12(Context context, MobileDataPreferenceController mobileDataPreferenceController) {
        mobileDataPreferenceController.setWifiPickerTrackerHelper(new WifiPickerTrackerHelper(this.mSettingsLifecycle, context, (WifiPickerTracker.WifiPickerTrackerCallback) null));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$13(RoamingPreferenceController roamingPreferenceController) {
        roamingPreferenceController.init(this.mFragmentManager, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$14(ApnPreferenceController apnPreferenceController) {
        apnPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$15(CarrierPreferenceController carrierPreferenceController) {
        carrierPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$16(DataUsagePreferenceController dataUsagePreferenceController) {
        dataUsagePreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$17(PreferredNetworkModePreferenceController preferredNetworkModePreferenceController) {
        preferredNetworkModePreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$18(EnabledNetworkModePreferenceController enabledNetworkModePreferenceController) {
        enabledNetworkModePreferenceController.init(this.mLifecycle, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$19(DataServiceSetupPreferenceController dataServiceSetupPreferenceController) {
        dataServiceSetupPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$20(Enable2gPreferenceController enable2gPreferenceController) {
        enable2gPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$21(CarrierWifiTogglePreferenceController carrierWifiTogglePreferenceController) {
        carrierWifiTogglePreferenceController.init(this.mLifecycle, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$22(OpenNetworkSelectPagePreferenceController openNetworkSelectPagePreferenceController) {
        openNetworkSelectPagePreferenceController.init(this.mLifecycle, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$23(OpenNetworkSelectPagePreferenceController openNetworkSelectPagePreferenceController, AutoSelectPreferenceController autoSelectPreferenceController) {
        autoSelectPreferenceController.init(this.mLifecycle, this.mSubId).addListener(openNetworkSelectPagePreferenceController);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$24(NetworkPreferenceCategoryController networkPreferenceCategoryController) {
        networkPreferenceCategoryController.init(this.mLifecycle, this.mSubId).setChildren(Arrays.asList(new AbstractPreferenceController[]{this.mAutoSelectPreferenceController}));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$25(CdmaSystemSelectPreferenceController cdmaSystemSelectPreferenceController) {
        cdmaSystemSelectPreferenceController.init(this.mPreferenceManager, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$26(CdmaSubscriptionPreferenceController cdmaSubscriptionPreferenceController) {
        cdmaSubscriptionPreferenceController.init(this.mPreferenceManager, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$27(WifiCallingPreferenceController wifiCallingPreferenceController) {
        wifiCallingPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$28(VideoCallingPreferenceController videoCallingPreferenceController) {
        videoCallingPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$29(BackupCallingPreferenceController backupCallingPreferenceController) {
        backupCallingPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$31(VideoCallingPreferenceController videoCallingPreferenceController, Enhanced4gLtePreferenceController enhanced4gLtePreferenceController) {
        enhanced4gLtePreferenceController.init(this.mSubId).addListener(videoCallingPreferenceController);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$32(VideoCallingPreferenceController videoCallingPreferenceController, Enhanced4gCallingPreferenceController enhanced4gCallingPreferenceController) {
        enhanced4gCallingPreferenceController.init(this.mSubId).addListener(videoCallingPreferenceController);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$33(VideoCallingPreferenceController videoCallingPreferenceController, Enhanced4gAdvancedCallingPreferenceController enhanced4gAdvancedCallingPreferenceController) {
        enhanced4gAdvancedCallingPreferenceController.init(this.mSubId).addListener(videoCallingPreferenceController);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$34(ContactDiscoveryPreferenceController contactDiscoveryPreferenceController) {
        contactDiscoveryPreferenceController.init(this.mFragmentManager, this.mSubId, this.mLifecycle);
    }

    /* access modifiers changed from: protected */
    public WifiCallingPreferenceController getWifiCallingPreferenceController() {
        return (WifiCallingPreferenceController) use(WifiCallingPreferenceController.class);
    }

    /* access modifiers changed from: protected */
    public VideoCallingPreferenceController getVideoCallingPreferenceController() {
        return (VideoCallingPreferenceController) use(VideoCallingPreferenceController.class);
    }

    /* access modifiers changed from: protected */
    public BackupCallingPreferenceController getBackupCallingPreferenceController() {
        return (BackupCallingPreferenceController) use(BackupCallingPreferenceController.class);
    }

    /* access modifiers changed from: protected */
    public OpenNetworkSelectPagePreferenceController getOpenNetworkSelectPagePrefController() {
        return (OpenNetworkSelectPagePreferenceController) use(OpenNetworkSelectPagePreferenceController.class);
    }

    /* access modifiers changed from: protected */
    public AutoSelectPreferenceController getAutoSelectPreferenceController() {
        return (AutoSelectPreferenceController) use(AutoSelectPreferenceController.class);
    }

    public void onCreate(Bundle bundle) {
        Log.i("NetworkSettings", "onCreate:+");
        TelephonyStatusControlSession telephonyAvailabilityStatus = setTelephonyAvailabilityStatus(getPreferenceControllersAsList());
        super.onCreate(bundle);
        Context context = getContext();
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mTelephonyManager = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        telephonyAvailabilityStatus.close();
        onRestoreInstance(bundle);
    }

    public void onResume() {
        super.onResume();
        Log.d("NetworkSettings", "onResume() subId=" + this.mSubId);
        if (this.mActiveSubscriptionsListener == null) {
            this.mActiveSubscriptionsListener = new ActiveSubscriptionsListener(getContext().getMainLooper(), getContext(), this.mSubId) {
                public void onChanged() {
                    MobileNetworkSettings.this.onSubscriptionDetailChanged();
                }
            };
            this.mDropFirstSubscriptionChangeNotify = true;
        }
        this.mActiveSubscriptionsListener.start();
    }

    /* access modifiers changed from: private */
    public void onSubscriptionDetailChanged() {
        if (this.mDropFirstSubscriptionChangeNotify) {
            this.mDropFirstSubscriptionChangeNotify = false;
            Log.d("NetworkSettings", "Callback during onResume()");
            return;
        }
        int i = this.mActiveSubscriptionsListenerCount + 1;
        this.mActiveSubscriptionsListenerCount = i;
        if (i == 1) {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.invalidateOptionsMenu();
            }
            ThreadUtils.postOnMainThread(new MobileNetworkSettings$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSubscriptionDetailChanged$35() {
        this.mActiveSubscriptionsListenerCount = 0;
        redrawPreferenceControllers();
    }

    public void onDestroy() {
        ActiveSubscriptionsListener activeSubscriptionsListener = this.mActiveSubscriptionsListener;
        if (activeSubscriptionsListener != null) {
            activeSubscriptionsListener.stop();
        }
        super.onDestroy();
        MotoMnsCache.close();
    }

    /* access modifiers changed from: package-private */
    public void onRestoreInstance(Bundle bundle) {
        if (bundle != null) {
            this.mClickedPrefKey = bundle.getString(KEY_CLICKED_PREF);
        }
    }

    /* access modifiers changed from: protected */
    public final int getPreferenceScreenResId() {
        return C1994R$xml.mobile_network_settings;
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(KEY_CLICKED_PREF, this.mClickedPrefKey);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        Preference findPreference;
        FragmentActivity activity;
        if (i != 17) {
            if (i == 18 && i2 != 0 && (activity = getActivity()) != null && !activity.isFinishing()) {
                activity.finish();
            }
        } else if (i2 != 0 && (findPreference = getPreferenceScreen().findPreference(this.mClickedPrefKey)) != null) {
            findPreference.performClick();
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId) && MotoMobileNetworkUtils.isSimInSlotUsable(getContext(), this.mSubId)) {
            MenuItem add = menu.add(0, C1985R$id.edit_sim_name, 0, C1992R$string.mobile_network_sim_name);
            add.setIcon(17303644);
            add.setShowAsAction(2);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId) || menuItem.getItemId() != C1985R$id.edit_sim_name) {
            return super.onOptionsItemSelected(menuItem);
        }
        RenameMobileNetworkDialogFragment.newInstance(this.mSubId).show(getFragmentManager(), "RenameMobileNetwork");
        return true;
    }
}
