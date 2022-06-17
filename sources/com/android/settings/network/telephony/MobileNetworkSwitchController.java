package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.widget.Switch;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.SettingsMainSwitchPreference;
import com.motorola.settings.network.MotoMobileNetworkUtils;
import com.motorola.settings.network.PhoneCallStateListener;
import com.motorola.settings.network.cache.MotoMnsCache;

public class MobileNetworkSwitchController extends BasePreferenceController implements SubscriptionsChangeListener.SubscriptionsChangeListenerClient, LifecycleObserver, PhoneCallStateListener.Client {
    private static final String TAG = "MobileNetworkSwitchCtrl";
    private SubscriptionsChangeListener mChangeListener;
    private PhoneCallStateListener mPhoneCallStateListener;
    protected int mSubId = -1;
    protected SubscriptionManager mSubscriptionManager = ((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class));
    private SettingsMainSwitchPreference mSwitchBar;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public int getAvailabilityStatus() {
        return 1;
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public void onAirplaneModeChanged(boolean z) {
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public MobileNetworkSwitchController(Context context, String str) {
        super(context, str);
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
        this.mPhoneCallStateListener = new PhoneCallStateListener(context, this);
    }

    public void init(Lifecycle lifecycle, int i) {
        lifecycle.addObserver(this);
        this.mSubId = i;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        this.mPhoneCallStateListener.register(this.mSubId);
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
        this.mPhoneCallStateListener.unregister();
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SettingsMainSwitchPreference settingsMainSwitchPreference = (SettingsMainSwitchPreference) preferenceScreen.findPreference(this.mPreferenceKey);
        this.mSwitchBar = settingsMainSwitchPreference;
        settingsMainSwitchPreference.setTitle(this.mContext.getString(C1992R$string.mobile_network_use_sim_on));
        this.mSwitchBar.setOnBeforeCheckedChangeListener(new MobileNetworkSwitchController$$ExternalSyntheticLambda0(this));
        update();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$displayPreference$0(Switch switchR, boolean z) {
        if (MotoMobileNetworkUtils.isSimActive(this.mContext, this.mSubId) == z) {
            return false;
        }
        SubscriptionUtil.startToggleSubscriptionDialogActivity(this.mContext, this.mSubId, z);
        return true;
    }

    /* access modifiers changed from: protected */
    public void update() {
        if (this.mSwitchBar != null) {
            SubscriptionInfo availableSubInfo = MotoMnsCache.getIns(this.mContext).getAvailableSubInfo(this.mSubId);
            boolean isMultiSimEnabled = TelephonyManager.from(this.mContext).isMultiSimEnabled();
            if (availableSubInfo == null || (!availableSubInfo.isEmbedded() && !isMultiSimEnabled)) {
                this.mSwitchBar.hide();
                return;
            }
            this.mSwitchBar.show();
            this.mSwitchBar.setCheckedInternal(MotoMobileNetworkUtils.isSimActive(this.mContext, this.mSubId));
        }
    }

    public void onSubscriptionsChanged() {
        update();
    }

    public void setEnabled(boolean z) {
        SettingsMainSwitchPreference settingsMainSwitchPreference = this.mSwitchBar;
        if (settingsMainSwitchPreference != null) {
            settingsMainSwitchPreference.setEnabled(z);
        }
    }

    public void onCallStateChanged(int i) {
        setEnabled(i == 0);
    }
}
