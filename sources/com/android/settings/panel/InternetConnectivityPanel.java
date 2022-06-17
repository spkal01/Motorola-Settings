package com.android.settings.panel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.C1992R$string;
import com.android.settings.Utils;
import com.android.settings.network.AirplaneModePreferenceController;
import com.android.settings.network.InternetUpdater;
import com.android.settings.network.ProviderModelSliceHelper;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.DataConnectivityListener;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import java.util.ArrayList;
import java.util.List;

public class InternetConnectivityPanel implements PanelContent, LifecycleObserver, InternetUpdater.InternetChangeListener, DataConnectivityListener.Client, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private static final int SUBTITLE_TEXT_ALL_CARRIER_NETWORK_UNAVAILABLE = C1992R$string.all_network_unavailable;
    private static final int SUBTITLE_TEXT_NON_CARRIER_NETWORK_UNAVAILABLE = C1992R$string.non_carrier_network_unavailable;
    private static final int SUBTITLE_TEXT_SEARCHING_FOR_NETWORKS = C1992R$string.wifi_empty_list_wifi_on;
    private static final int SUBTITLE_TEXT_TAP_A_NETWORK_TO_CONNECT = C1992R$string.tap_a_network_to_connect;
    private static final int SUBTITLE_TEXT_WIFI_IS_OFF = C1992R$string.wifi_is_off;
    private PanelContentCallback mCallback;
    private DataConnectivityListener mConnectivityListener;
    private final Context mContext;
    private int mDefaultDataSubid = -1;
    protected HandlerInjector mHandlerInjector;
    protected Runnable mHideProgressBarRunnable = new InternetConnectivityPanel$$ExternalSyntheticLambda1(this);
    protected Runnable mHideScanningSubTitleRunnable = new InternetConnectivityPanel$$ExternalSyntheticLambda0(this);
    InternetUpdater mInternetUpdater;
    protected boolean mIsProgressBarVisible;
    boolean mIsProviderModelEnabled;
    protected boolean mIsScanningSubTitleShownOnce;
    ProviderModelSliceHelper mProviderModelSliceHelper;
    private SubscriptionsChangeListener mSubscriptionsListener;
    private int mSubtitle = -1;
    private final NetworkProviderTelephonyCallback mTelephonyCallback;
    private TelephonyManager mTelephonyManager;
    private final WifiManager mWifiManager;
    private final IntentFilter mWifiStateFilter;
    private final BroadcastReceiver mWifiStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (TextUtils.equals(intent.getAction(), "android.net.wifi.SCAN_RESULTS")) {
                    InternetConnectivityPanel.this.updateProgressBar();
                    InternetConnectivityPanel.this.updatePanelTitle();
                } else if (TextUtils.equals(intent.getAction(), "android.net.wifi.STATE_CHANGE")) {
                    InternetConnectivityPanel.this.updateProgressBar();
                    InternetConnectivityPanel.this.updatePanelTitle();
                }
            }
        }
    };

    public int getMetricsCategory() {
        return 1654;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        setProgressBarVisible(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        this.mIsScanningSubTitleShownOnce = true;
        updatePanelTitle();
    }

    static class HandlerInjector {
        protected final Handler mHandler;

        HandlerInjector(Context context) {
            this.mHandler = context.getMainThreadHandler();
        }

        public void postDelay(Runnable runnable) {
            this.mHandler.postDelayed(runnable, 2000);
        }

        public void removeCallbacks(Runnable runnable) {
            this.mHandler.removeCallbacks(runnable);
        }
    }

    private InternetConnectivityPanel(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mHandlerInjector = new HandlerInjector(context);
        this.mIsProviderModelEnabled = Utils.isProviderModelEnabled(applicationContext);
        this.mInternetUpdater = new InternetUpdater(context, (Lifecycle) null, this);
        this.mSubscriptionsListener = new SubscriptionsChangeListener(context, this);
        this.mConnectivityListener = new DataConnectivityListener(context, this);
        this.mTelephonyCallback = new NetworkProviderTelephonyCallback();
        this.mDefaultDataSubid = getDefaultDataSubscriptionId();
        this.mTelephonyManager = (TelephonyManager) applicationContext.getSystemService(TelephonyManager.class);
        this.mWifiManager = (WifiManager) applicationContext.getSystemService(WifiManager.class);
        IntentFilter intentFilter = new IntentFilter("android.net.wifi.STATE_CHANGE");
        this.mWifiStateFilter = intentFilter;
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        this.mProviderModelSliceHelper = new ProviderModelSliceHelper(applicationContext, (CustomSliceable) null);
    }

    public static InternetConnectivityPanel create(Context context) {
        return new InternetConnectivityPanel(context);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (this.mIsProviderModelEnabled) {
            this.mInternetUpdater.onResume();
            this.mSubscriptionsListener.start();
            this.mConnectivityListener.start();
            this.mTelephonyManager.registerTelephonyCallback(new HandlerExecutor(new Handler(Looper.getMainLooper())), this.mTelephonyCallback);
            this.mContext.registerReceiver(this.mWifiStateReceiver, this.mWifiStateFilter);
            updateProgressBar();
            updatePanelTitle();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (this.mIsProviderModelEnabled) {
            this.mInternetUpdater.onPause();
            this.mSubscriptionsListener.stop();
            this.mConnectivityListener.stop();
            this.mTelephonyManager.unregisterTelephonyCallback(this.mTelephonyCallback);
            this.mContext.unregisterReceiver(this.mWifiStateReceiver);
            this.mHandlerInjector.removeCallbacks(this.mHideProgressBarRunnable);
            this.mHandlerInjector.removeCallbacks(this.mHideScanningSubTitleRunnable);
        }
    }

    public CharSequence getTitle() {
        if (!this.mIsProviderModelEnabled) {
            return this.mContext.getText(C1992R$string.internet_connectivity_panel_title);
        }
        return this.mContext.getText(this.mInternetUpdater.isAirplaneModeOn() ? C1992R$string.airplane_mode : C1992R$string.provider_internet_settings);
    }

    public CharSequence getSubTitle() {
        int i;
        if (!this.mIsProviderModelEnabled || (i = this.mSubtitle) == -1) {
            return null;
        }
        return this.mContext.getText(i);
    }

    public List<Uri> getSlices() {
        ArrayList arrayList = new ArrayList();
        if (this.mIsProviderModelEnabled) {
            arrayList.add(CustomSliceRegistry.PROVIDER_MODEL_SLICE_URI);
        } else {
            arrayList.add(CustomSliceRegistry.WIFI_SLICE_URI);
            arrayList.add(CustomSliceRegistry.MOBILE_DATA_SLICE_URI);
            arrayList.add(AirplaneModePreferenceController.SLICE_URI);
        }
        return arrayList;
    }

    public Intent getSeeMoreIntent() {
        if (this.mIsProviderModelEnabled) {
            return null;
        }
        return new Intent("android.settings.WIRELESS_SETTINGS").addFlags(268435456);
    }

    public boolean isProgressBarVisible() {
        return this.mIsProgressBarVisible;
    }

    public void registerCallback(PanelContentCallback panelContentCallback) {
        this.mCallback = panelContentCallback;
    }

    public void onAirplaneModeChanged(boolean z) {
        log("onAirplaneModeChanged: isAirplaneModeOn:" + z);
        updatePanelTitle();
    }

    public void onWifiEnabledChanged(boolean z) {
        log("onWifiEnabledChanged: enabled:" + z);
        updatePanelTitle();
    }

    public void onSubscriptionsChanged() {
        int defaultDataSubscriptionId = getDefaultDataSubscriptionId();
        log("onSubscriptionsChanged: defaultDataSubId:" + defaultDataSubscriptionId);
        if (this.mDefaultDataSubid != defaultDataSubscriptionId) {
            if (SubscriptionManager.isUsableSubscriptionId(defaultDataSubscriptionId)) {
                this.mTelephonyManager.unregisterTelephonyCallback(this.mTelephonyCallback);
                this.mTelephonyManager.registerTelephonyCallback(new HandlerExecutor(new Handler(Looper.getMainLooper())), this.mTelephonyCallback);
            }
            updatePanelTitle();
        }
    }

    public void onDataConnectivityChange() {
        log("onDataConnectivityChange");
        updatePanelTitle();
    }

    /* access modifiers changed from: package-private */
    public void updatePanelTitle() {
        if (this.mCallback != null) {
            updateSubtitleText();
            this.mCallback.onHeaderChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public int getDefaultDataSubscriptionId() {
        return SubscriptionManager.getDefaultDataSubscriptionId();
    }

    private void updateSubtitleText() {
        this.mSubtitle = -1;
        if (!this.mInternetUpdater.isWifiEnabled()) {
            if (!this.mInternetUpdater.isAirplaneModeOn()) {
                log("Airplane mode off + Wi-Fi off.");
                this.mSubtitle = SUBTITLE_TEXT_WIFI_IS_OFF;
            }
        } else if (!this.mInternetUpdater.isAirplaneModeOn()) {
            List<ScanResult> scanResults = this.mWifiManager.getScanResults();
            if (scanResults != null && scanResults.size() != 0) {
                this.mSubtitle = SUBTITLE_TEXT_TAP_A_NETWORK_TO_CONNECT;
            } else if (this.mIsScanningSubTitleShownOnce || !this.mIsProgressBarVisible) {
                log("No Wi-Fi item.");
                if (!this.mProviderModelSliceHelper.hasCarrier() || (!this.mProviderModelSliceHelper.isVoiceStateInService() && !this.mProviderModelSliceHelper.isDataStateInService())) {
                    log("no carrier or service is out of service.");
                    this.mSubtitle = SUBTITLE_TEXT_ALL_CARRIER_NETWORK_UNAVAILABLE;
                } else if (!this.mProviderModelSliceHelper.isMobileDataEnabled()) {
                    log("mobile data off");
                    this.mSubtitle = SUBTITLE_TEXT_NON_CARRIER_NETWORK_UNAVAILABLE;
                } else if (!this.mProviderModelSliceHelper.isDataSimActive()) {
                    log("no carrier data.");
                    this.mSubtitle = SUBTITLE_TEXT_ALL_CARRIER_NETWORK_UNAVAILABLE;
                } else {
                    this.mSubtitle = SUBTITLE_TEXT_NON_CARRIER_NETWORK_UNAVAILABLE;
                }
            } else {
                this.mSubtitle = SUBTITLE_TEXT_SEARCHING_FOR_NETWORKS;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void updateProgressBar() {
        if (this.mWifiManager == null || !this.mInternetUpdater.isWifiEnabled()) {
            setProgressBarVisible(false);
            return;
        }
        setProgressBarVisible(true);
        List<ScanResult> scanResults = this.mWifiManager.getScanResults();
        if (scanResults != null && scanResults.size() > 0) {
            this.mHandlerInjector.postDelay(this.mHideProgressBarRunnable);
        } else if (!this.mIsScanningSubTitleShownOnce) {
            this.mHandlerInjector.postDelay(this.mHideScanningSubTitleRunnable);
        }
    }

    /* access modifiers changed from: protected */
    public void setProgressBarVisible(boolean z) {
        if (this.mIsProgressBarVisible != z) {
            this.mIsProgressBarVisible = z;
            PanelContentCallback panelContentCallback = this.mCallback;
            if (panelContentCallback != null) {
                panelContentCallback.onProgressBarVisibleChanged();
                updatePanelTitle();
            }
        }
    }

    private class NetworkProviderTelephonyCallback extends TelephonyCallback implements TelephonyCallback.DataConnectionStateListener, TelephonyCallback.ServiceStateListener {
        private NetworkProviderTelephonyCallback() {
        }

        public void onServiceStateChanged(ServiceState serviceState) {
            InternetConnectivityPanel.log("onServiceStateChanged voiceState=" + serviceState.getState() + " dataState=" + serviceState.getDataRegistrationState());
            InternetConnectivityPanel.this.updatePanelTitle();
        }

        public void onDataConnectionStateChanged(int i, int i2) {
            InternetConnectivityPanel.log("onDataConnectionStateChanged: networkType=" + i2 + " state=" + i);
            InternetConnectivityPanel.this.updatePanelTitle();
        }
    }

    /* access modifiers changed from: private */
    public static void log(String str) {
        Log.d("InternetConnectivityPanel", str);
    }
}
