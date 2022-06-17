package com.android.settings.network;

import android.content.Context;
import android.content.Intent;
import android.os.UserManager;
import android.telephony.SubscriptionManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1990R$plurals;
import com.android.settings.C1992R$string;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.helper.SubscriptionAnnotation;
import com.android.settings.network.telephony.MobileNetworkActivity;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.AddPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MobileNetworkSummaryController extends AbstractPreferenceController implements SubscriptionsChangeListener.SubscriptionsChangeListenerClient, LifecycleObserver, PreferenceControllerMixin {
    private SubscriptionsChangeListener mChangeListener;
    private final MetricsFeatureProvider mMetricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
    private AddPreference mPreference;
    private MobileNetworkSummaryStatus mStatusCache = new MobileNetworkSummaryStatus();
    private SubscriptionManager mSubscriptionManager;
    private UserManager mUserManager;

    public String getPreferenceKey() {
        return "mobile_network_list";
    }

    public MobileNetworkSummaryController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        if (lifecycle != null) {
            this.mChangeListener = new SubscriptionsChangeListener(context, this);
            lifecycle.addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (AddPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public CharSequence getSummary() {
        this.mStatusCache.update(this.mContext, (Consumer<MobileNetworkSummaryStatus>) null);
        List<SubscriptionAnnotation> subscriptionList = this.mStatusCache.getSubscriptionList();
        if (subscriptionList.isEmpty()) {
            return this.mStatusCache.isEuiccConfigSupport() ? this.mContext.getResources().getString(C1992R$string.mobile_network_summary_add_a_network) : "";
        }
        if (subscriptionList.size() == 1) {
            SubscriptionAnnotation subscriptionAnnotation = subscriptionList.get(0);
            CharSequence displayName = this.mStatusCache.getDisplayName(subscriptionAnnotation.getSubscriptionId());
            if (subscriptionAnnotation.getSubInfo().isEmbedded() || subscriptionAnnotation.isActive() || this.mStatusCache.isPhysicalSimDisableSupport()) {
                return displayName;
            }
            return this.mContext.getString(C1992R$string.mobile_network_tap_to_activate, new Object[]{displayName});
        } else if (Utils.isProviderModelEnabled(this.mContext)) {
            return getSummaryForProviderModel(subscriptionList);
        } else {
            int size = subscriptionList.size();
            return this.mContext.getResources().getQuantityString(C1990R$plurals.mobile_network_summary_count, size, new Object[]{Integer.valueOf(size)});
        }
    }

    private CharSequence getSummaryForProviderModel(List<SubscriptionAnnotation> list) {
        return (CharSequence) list.stream().mapToInt(MobileNetworkSummaryController$$ExternalSyntheticLambda7.INSTANCE).mapToObj(new MobileNetworkSummaryController$$ExternalSyntheticLambda6(this)).collect(Collectors.joining(", "));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ CharSequence lambda$getSummaryForProviderModel$0(int i) {
        return this.mStatusCache.getDisplayName(i);
    }

    private void logPreferenceClick(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, preference.getExtras().getInt("category"));
    }

    private void startAddSimFlow() {
        Intent intent = new Intent("android.telephony.euicc.action.PROVISION_EMBEDDED_SUBSCRIPTION");
        intent.putExtra("android.telephony.euicc.extra.FORCE_PROVISION", true);
        this.mContext.startActivity(intent);
    }

    private void initPreference() {
        refreshSummary(this.mPreference);
        this.mPreference.setOnPreferenceClickListener((Preference.OnPreferenceClickListener) null);
        this.mPreference.setOnAddClickListener((AddPreference.OnAddClickListener) null);
        this.mPreference.setFragment((String) null);
        this.mPreference.setEnabled(!this.mChangeListener.isAirplaneModeOn() && !this.mPreference.isDisabledByAdmin());
    }

    private void update() {
        AddPreference addPreference = this.mPreference;
        if (addPreference != null && !addPreference.isDisabledByAdmin()) {
            this.mStatusCache.update(this.mContext, new MobileNetworkSummaryController$$ExternalSyntheticLambda3(this));
            List<SubscriptionAnnotation> subscriptionList = this.mStatusCache.getSubscriptionList();
            if (!subscriptionList.isEmpty()) {
                if (this.mStatusCache.isEuiccConfigSupport()) {
                    this.mPreference.setAddWidgetEnabled(!this.mChangeListener.isAirplaneModeOn());
                    this.mPreference.setOnAddClickListener(new MobileNetworkSummaryController$$ExternalSyntheticLambda2(this));
                }
                if (subscriptionList.size() == 1) {
                    this.mPreference.setOnPreferenceClickListener(new MobileNetworkSummaryController$$ExternalSyntheticLambda1(this, subscriptionList));
                } else {
                    this.mPreference.setFragment(MobileNetworkListFragment.class.getCanonicalName());
                }
            } else if (this.mStatusCache.isEuiccConfigSupport()) {
                this.mPreference.setOnPreferenceClickListener(new MobileNetworkSummaryController$$ExternalSyntheticLambda0(this));
            } else {
                this.mPreference.setEnabled(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$update$1(MobileNetworkSummaryStatus mobileNetworkSummaryStatus) {
        initPreference();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$update$2(Preference preference) {
        logPreferenceClick(preference);
        startAddSimFlow();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$update$3(AddPreference addPreference) {
        logPreferenceClick(addPreference);
        startAddSimFlow();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$update$4(List list, Preference preference) {
        logPreferenceClick(preference);
        SubscriptionAnnotation subscriptionAnnotation = (SubscriptionAnnotation) list.get(0);
        if (subscriptionAnnotation.getSubInfo().isEmbedded() || subscriptionAnnotation.isActive() || this.mStatusCache.isPhysicalSimDisableSupport()) {
            Intent intent = new Intent(this.mContext, MobileNetworkActivity.class);
            intent.putExtra("android.provider.extra.SUB_ID", subscriptionAnnotation.getSubscriptionId());
            this.mContext.startActivity(intent);
            return true;
        }
        SubscriptionUtil.startToggleSubscriptionDialogActivity(this.mContext, subscriptionAnnotation.getSubscriptionId(), true);
        return true;
    }

    public boolean isAvailable() {
        return !com.android.settingslib.Utils.isWifiOnly(this.mContext) && this.mUserManager.isAdminUser();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAirplaneModeChanged$5(MobileNetworkSummaryStatus mobileNetworkSummaryStatus) {
        update();
    }

    public void onAirplaneModeChanged(boolean z) {
        this.mStatusCache.update(this.mContext, new MobileNetworkSummaryController$$ExternalSyntheticLambda5(this));
    }

    public void onSubscriptionsChanged() {
        this.mStatusCache.update(this.mContext, new MobileNetworkSummaryController$$ExternalSyntheticLambda4(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSubscriptionsChanged$6(MobileNetworkSummaryStatus mobileNetworkSummaryStatus) {
        refreshSummary(this.mPreference);
        update();
    }
}
