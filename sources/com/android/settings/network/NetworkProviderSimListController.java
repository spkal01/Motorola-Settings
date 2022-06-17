package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.ArrayMap;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.MobileNetworkActivity;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.motorola.settings.network.MotoMobileNetworkUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkProviderSimListController extends AbstractPreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private SubscriptionsChangeListener mChangeListener;
    final BroadcastReceiver mDataSubscriptionChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
                NetworkProviderSimListController.this.update();
            }
        }
    };
    private PreferenceCategory mPreferenceCategory;
    private Map<Integer, Preference> mPreferences;
    private SubscriptionManager mSubscriptionManager;

    public String getPreferenceKey() {
        return "provider_model_sim_list";
    }

    public void onAirplaneModeChanged(boolean z) {
    }

    public NetworkProviderSimListController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
        this.mPreferences = new ArrayMap();
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        this.mContext.registerReceiver(this.mDataSubscriptionChangedReceiver, intentFilter);
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
        BroadcastReceiver broadcastReceiver = this.mDataSubscriptionChangedReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("provider_model_sim_category");
        update();
    }

    /* access modifiers changed from: private */
    public void update() {
        if (this.mPreferenceCategory != null) {
            Map<Integer, Preference> map = this.mPreferences;
            this.mPreferences = new ArrayMap();
            for (SubscriptionInfo next : getAvailablePhysicalSubscription()) {
                int subscriptionId = next.getSubscriptionId();
                Preference remove = map.remove(Integer.valueOf(subscriptionId));
                if (remove == null) {
                    remove = new Preference(this.mPreferenceCategory.getContext());
                    this.mPreferenceCategory.addPreference(remove);
                }
                CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(next, this.mContext);
                remove.setTitle(uniqueSubscriptionDisplayName);
                remove.setSummary(getSummary(subscriptionId, uniqueSubscriptionDisplayName));
                remove.setOnPreferenceClickListener(new NetworkProviderSimListController$$ExternalSyntheticLambda0(this, subscriptionId, next));
                this.mPreferences.put(Integer.valueOf(subscriptionId), remove);
            }
            for (Preference removePreference : map.values()) {
                this.mPreferenceCategory.removePreference(removePreference);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$update$0(int i, SubscriptionInfo subscriptionInfo, Preference preference) {
        if (MotoMobileNetworkUtils.isSimActive(this.mContext, i) || SubscriptionUtil.showToggleForPhysicalSim(this.mSubscriptionManager)) {
            Intent intent = new Intent(this.mContext, MobileNetworkActivity.class);
            intent.putExtra("android.provider.extra.SUB_ID", subscriptionInfo.getSubscriptionId());
            this.mContext.startActivity(intent);
        } else {
            SubscriptionUtil.startToggleSubscriptionDialogActivity(this.mContext, i, true);
        }
        return true;
    }

    public CharSequence getSummary(int i, CharSequence charSequence) {
        if (MotoMobileNetworkUtils.isSimActive(this.mContext, i)) {
            CharSequence defaultSimConfig = SubscriptionUtil.getDefaultSimConfig(this.mContext, i);
            String string = this.mContext.getResources().getString(C1992R$string.sim_category_active_sim);
            if (defaultSimConfig == null) {
                return string;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            sb.append(defaultSimConfig);
            return sb;
        } else if (SubscriptionUtil.showToggleForPhysicalSim(this.mSubscriptionManager)) {
            return this.mContext.getString(C1992R$string.sim_category_inactive_sim);
        } else {
            return this.mContext.getString(C1992R$string.mobile_network_tap_to_activate, new Object[]{charSequence});
        }
    }

    public boolean isAvailable() {
        return !getAvailablePhysicalSubscription().isEmpty();
    }

    /* access modifiers changed from: protected */
    public List<SubscriptionInfo> getAvailablePhysicalSubscription() {
        ArrayList arrayList = new ArrayList();
        for (SubscriptionInfo next : SubscriptionUtil.getAvailableSubscriptions(this.mContext)) {
            if (!next.isEmbedded()) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public void onSubscriptionsChanged() {
        update();
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        refreshSummary(this.mPreferenceCategory);
        update();
    }

    /* access modifiers changed from: protected */
    public int getDefaultVoiceSubscriptionId() {
        return SubscriptionManager.getDefaultVoiceSubscriptionId();
    }

    /* access modifiers changed from: protected */
    public int getDefaultSmsSubscriptionId() {
        return SubscriptionManager.getDefaultSmsSubscriptionId();
    }

    /* access modifiers changed from: protected */
    public int getDefaultDataSubscriptionId() {
        return SubscriptionManager.getDefaultDataSubscriptionId();
    }
}
