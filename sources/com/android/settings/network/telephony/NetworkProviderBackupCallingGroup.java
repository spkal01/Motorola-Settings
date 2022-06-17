package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsManager;
import android.telephony.ims.ImsMmTelManager;
import android.util.ArrayMap;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C1992R$string;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkProviderBackupCallingGroup extends TelephonyTogglePreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private static final String KEY_PREFERENCE_BACKUPCALLING_GROUP = "provider_model_backup_call_group";
    private static final int PREF_START_ORDER = 10;
    private static final String TAG = "NetworkProviderBackupCallingGroup";
    private Map<Integer, SwitchPreference> mBackupCallingForSubPreferences;
    private PreferenceGroup mPreferenceGroup;
    private String mPreferenceGroupKey;
    private List<SubscriptionInfo> mSubInfoListForBackupCall;
    private SubscriptionsChangeListener mSubscriptionsChangeListener;
    private Map<Integer, TelephonyManager> mTelephonyManagerList = new HashMap();

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public String getPreferenceKey() {
        return KEY_PREFERENCE_BACKUPCALLING_GROUP;
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public boolean isChecked() {
        return false;
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public void onAirplaneModeChanged(boolean z) {
    }

    public boolean setChecked(boolean z) {
        return false;
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public NetworkProviderBackupCallingGroup(Context context, Lifecycle lifecycle, List<SubscriptionInfo> list, String str) {
        super(context, str);
        this.mPreferenceGroupKey = str;
        this.mSubInfoListForBackupCall = list;
        this.mBackupCallingForSubPreferences = new ArrayMap();
        setSubscriptionInfoList(context);
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (this.mSubscriptionsChangeListener == null) {
            this.mSubscriptionsChangeListener = new SubscriptionsChangeListener(this.mContext, this);
        }
        this.mSubscriptionsChangeListener.start();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        SubscriptionsChangeListener subscriptionsChangeListener = this.mSubscriptionsChangeListener;
        if (subscriptionsChangeListener != null) {
            subscriptionsChangeListener.stop();
        }
    }

    public int getAvailabilityStatus(int i) {
        List<SubscriptionInfo> list = this.mSubInfoListForBackupCall;
        if (list == null || getSubscriptionInfoFromList(list, i) == null || this.mSubInfoListForBackupCall.size() <= 1) {
            return 2;
        }
        return 0;
    }

    private boolean setCrossSimCallingEnabled(int i, boolean z) {
        ImsMmTelManager imsMmTelManager = getImsMmTelManager(i);
        if (imsMmTelManager == null) {
            Log.d(TAG, "setCrossSimCallingEnabled(), ImsMmTelManager is null");
            return false;
        }
        try {
            imsMmTelManager.setCrossSimCallingEnabled(z);
            return true;
        } catch (ImsException e) {
            Log.w(TAG, "fail to get cross SIM calling configuration", e);
            return false;
        }
    }

    private boolean isCrossSimCallingEnabled(int i) {
        ImsMmTelManager imsMmTelManager = getImsMmTelManager(i);
        if (imsMmTelManager == null) {
            Log.d(TAG, "isCrossSimCallingEnabled(), ImsMmTelManager is null");
            return false;
        }
        try {
            return imsMmTelManager.isCrossSimCallingEnabled();
        } catch (ImsException e) {
            Log.w(TAG, "fail to get cross SIM calling configuration", e);
            return false;
        }
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(this.mPreferenceGroupKey);
        update();
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            update();
        }
    }

    private void update() {
        if (this.mPreferenceGroup != null) {
            setSubscriptionInfoList(this.mContext);
            List<SubscriptionInfo> list = this.mSubInfoListForBackupCall;
            if (list == null || list.size() < 2) {
                for (SwitchPreference removePreference : this.mBackupCallingForSubPreferences.values()) {
                    this.mPreferenceGroup.removePreference(removePreference);
                }
                this.mBackupCallingForSubPreferences.clear();
                return;
            }
            Map<Integer, SwitchPreference> map = this.mBackupCallingForSubPreferences;
            this.mBackupCallingForSubPreferences = new ArrayMap();
            setSubscriptionInfoForPreference(map);
        }
    }

    private void setSubscriptionInfoForPreference(Map<Integer, SwitchPreference> map) {
        int i = 10;
        for (SubscriptionInfo next : this.mSubInfoListForBackupCall) {
            int subscriptionId = next.getSubscriptionId();
            SwitchPreference remove = map.remove(Integer.valueOf(subscriptionId));
            if (remove == null) {
                remove = new SwitchPreference(this.mPreferenceGroup.getContext());
                this.mPreferenceGroup.addPreference(remove);
            }
            CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(next, this.mContext);
            remove.setTitle(uniqueSubscriptionDisplayName);
            int i2 = i + 1;
            remove.setOrder(i);
            remove.setSummary((CharSequence) getSummary(uniqueSubscriptionDisplayName));
            boolean isCrossSimCallingEnabled = isCrossSimCallingEnabled(subscriptionId);
            remove.setChecked(isCrossSimCallingEnabled);
            remove.setOnPreferenceClickListener(new NetworkProviderBackupCallingGroup$$ExternalSyntheticLambda0(this, subscriptionId, isCrossSimCallingEnabled));
            this.mBackupCallingForSubPreferences.put(Integer.valueOf(subscriptionId), remove);
            i = i2;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setSubscriptionInfoForPreference$0(int i, boolean z, Preference preference) {
        setCrossSimCallingEnabled(i, !z);
        return true;
    }

    private String getSummary(CharSequence charSequence) {
        return String.format(getResourcesForSubId().getString(C1992R$string.backup_calling_setting_summary), new Object[]{charSequence}).toString();
    }

    private void setSubscriptionInfoList(Context context) {
        List<SubscriptionInfo> list = this.mSubInfoListForBackupCall;
        if (list != null) {
            list.removeIf(new NetworkProviderBackupCallingGroup$$ExternalSyntheticLambda1(this, context));
        } else {
            Log.d(TAG, "No active subscriptions");
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setSubscriptionInfoList$1(Context context, SubscriptionInfo subscriptionInfo) {
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        setTelephonyManagerForSubscriptionId(context, subscriptionId);
        return !hasBackupCallingFeature(subscriptionId) && this.mSubInfoListForBackupCall.contains(subscriptionInfo);
    }

    private void setTelephonyManagerForSubscriptionId(Context context, int i) {
        this.mTelephonyManagerList.put(Integer.valueOf(i), ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i));
    }

    /* access modifiers changed from: protected */
    public boolean hasBackupCallingFeature(int i) {
        return isCrossSimEnabledByPlatform(this.mContext, i);
    }

    /* access modifiers changed from: protected */
    public boolean isCrossSimEnabledByPlatform(Context context, int i) {
        if (new WifiCallingQueryImsState(context, i).isWifiCallingSupported()) {
            PersistableBundle carrierConfigForSubId = getCarrierConfigForSubId(i);
            if (carrierConfigForSubId == null || !carrierConfigForSubId.getBoolean("carrier_cross_sim_ims_available_bool", false)) {
                return false;
            }
            return true;
        }
        Log.d(TAG, "WifiCalling is not supported by framework. subId = " + i);
        return false;
    }

    private ImsMmTelManager getImsMmTelManager(int i) {
        ImsManager imsManager;
        if (SubscriptionManager.isUsableSubscriptionId(i) && (imsManager = (ImsManager) this.mContext.getSystemService(ImsManager.class)) != null) {
            return imsManager.getImsMmTelManager(i);
        }
        return null;
    }

    private SubscriptionInfo getSubscriptionInfoFromList(List<SubscriptionInfo> list, int i) {
        for (SubscriptionInfo next : list) {
            if (next != null && next.getSubscriptionId() == i) {
                return next;
            }
        }
        return null;
    }

    public void onSubscriptionsChanged() {
        this.mSubInfoListForBackupCall = SubscriptionUtil.getActiveSubscriptions((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class));
        update();
    }
}
