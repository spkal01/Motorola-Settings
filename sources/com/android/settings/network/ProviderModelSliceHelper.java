package com.android.settings.network;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.slice.builders.ListBuilder;
import com.android.settings.C1992R$string;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.wifi.slice.WifiSliceItem;
import com.android.settingslib.Utils;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.net.SignalStrengthUtil;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ProviderModelSliceHelper {
    protected final Context mContext;
    private CustomSliceable mSliceable;
    private final SubscriptionManager mSubscriptionManager;
    private TelephonyManager mTelephonyManager;

    public ProviderModelSliceHelper(Context context, CustomSliceable customSliceable) {
        this.mContext = context;
        this.mSliceable = customSliceable;
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.mSubscriptionManager;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasCarrier() {
        /*
            r1 = this;
            boolean r0 = r1.isAirplaneModeEnabled()
            if (r0 != 0) goto L_0x0018
            android.telephony.SubscriptionManager r0 = r1.mSubscriptionManager
            if (r0 == 0) goto L_0x0018
            android.telephony.TelephonyManager r1 = r1.mTelephonyManager
            if (r1 == 0) goto L_0x0018
            int[] r1 = r0.getActiveSubscriptionIdList()
            int r1 = r1.length
            if (r1 > 0) goto L_0x0016
            goto L_0x0018
        L_0x0016:
            r1 = 1
            return r1
        L_0x0018:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.ProviderModelSliceHelper.hasCarrier():boolean");
    }

    public boolean isMobileDataEnabled() {
        return this.mTelephonyManager.isDataEnabled();
    }

    public boolean isDataSimActive() {
        return MobileNetworkUtils.activeNetworkIsCellular(this.mContext);
    }

    public boolean isDataStateInService() {
        NetworkRegistrationInfo networkRegistrationInfo;
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        if (serviceState == null) {
            networkRegistrationInfo = null;
        } else {
            networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, 1);
        }
        if (networkRegistrationInfo == null) {
            return false;
        }
        return networkRegistrationInfo.isRegistered();
    }

    public boolean isVoiceStateInService() {
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        return serviceState != null && serviceState.getState() == 0;
    }

    public Drawable getDrawableWithSignalStrength() {
        return getDrawableWithSignalStrength(SubscriptionManager.getDefaultDataSubscriptionId());
    }

    public Drawable getDrawableWithSignalStrength(int i) {
        int i2;
        SignalStrength signalStrength = this.mTelephonyManager.getSignalStrength();
        if (signalStrength == null) {
            i2 = 0;
        } else {
            i2 = signalStrength.getLevel();
        }
        int i3 = 5;
        if (this.mSubscriptionManager != null && shouldInflateSignalStrength(i)) {
            i2++;
            i3 = 6;
        }
        return MobileNetworkUtils.getSignalStrengthIcon(this.mContext, i2, i3, 0, false);
    }

    public void updateTelephony() {
        if (this.mSubscriptionManager != null && SubscriptionManager.getDefaultDataSubscriptionId() != -1) {
            this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId());
        }
    }

    /* access modifiers changed from: protected */
    public ListBuilder createListBuilder(Uri uri) {
        return new ListBuilder(this.mContext, uri, -1).setAccentColor(-1).setKeywords(getKeywords());
    }

    /* access modifiers changed from: protected */
    public WifiSliceItem getConnectedWifiItem(List<WifiSliceItem> list) {
        if (list == null) {
            return null;
        }
        Optional findFirst = list.stream().filter(ProviderModelSliceHelper$$ExternalSyntheticLambda2.INSTANCE).findFirst();
        if (findFirst.isPresent()) {
            return (WifiSliceItem) findFirst.get();
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getConnectedWifiItem$0(WifiSliceItem wifiSliceItem) {
        return wifiSliceItem.getConnectedState() == 2;
    }

    /* access modifiers changed from: protected */
    public boolean isAirplaneModeEnabled() {
        return WirelessUtils.isAirplaneModeOn(this.mContext);
    }

    /* access modifiers changed from: protected */
    public SubscriptionManager getSubscriptionManager() {
        return this.mSubscriptionManager;
    }

    private static void log(String str) {
        Log.d("ProviderModelSlice", str);
    }

    private boolean shouldInflateSignalStrength(int i) {
        return SignalStrengthUtil.shouldInflateSignalStrength(this.mContext, i);
    }

    /* access modifiers changed from: protected */
    public Drawable getMobileDrawable(Drawable drawable) throws Throwable {
        if (this.mTelephonyManager == null) {
            log("mTelephonyManager == null");
            return drawable;
        }
        if (isDataStateInService() || isVoiceStateInService()) {
            Semaphore semaphore = new Semaphore(0);
            AtomicReference atomicReference = new AtomicReference();
            ThreadUtils.postOnMainThread(new ProviderModelSliceHelper$$ExternalSyntheticLambda0(this, atomicReference, semaphore));
            semaphore.acquire();
            drawable = (Drawable) atomicReference.get();
        }
        drawable.setTint(Utils.getColorAttrDefaultColor(this.mContext, 16843817));
        if (isDataSimActive()) {
            drawable.setTint(Utils.getColorAccentDefaultColor(this.mContext));
        }
        return drawable;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMobileDrawable$1(AtomicReference atomicReference, Semaphore semaphore) {
        atomicReference.set(getDrawableWithSignalStrength());
        semaphore.release();
    }

    /* access modifiers changed from: protected */
    public String getMobileSummary(String str) {
        if (!isMobileDataEnabled()) {
            return this.mContext.getString(C1992R$string.mobile_data_off_summary);
        }
        if (!isDataStateInService()) {
            return this.mContext.getString(C1992R$string.mobile_data_no_connection);
        }
        if (!isDataSimActive()) {
            return str;
        }
        Context context = this.mContext;
        return context.getString(C1992R$string.preference_summary_default_combination, new Object[]{context.getString(C1992R$string.mobile_data_connection_active), str});
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0011, code lost:
        r1 = r1.getActiveSubscriptionInfo(android.telephony.SubscriptionManager.getDefaultDataSubscriptionId());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getMobileTitle() {
        /*
            r3 = this;
            android.content.Context r0 = r3.mContext
            int r1 = com.android.settings.C1992R$string.mobile_data_settings_title
            java.lang.CharSequence r0 = r0.getText(r1)
            java.lang.String r0 = r0.toString()
            android.telephony.SubscriptionManager r1 = r3.mSubscriptionManager
            if (r1 != 0) goto L_0x0011
            return r0
        L_0x0011:
            int r2 = android.telephony.SubscriptionManager.getDefaultDataSubscriptionId()
            android.telephony.SubscriptionInfo r1 = r1.getActiveSubscriptionInfo(r2)
            if (r1 == 0) goto L_0x0025
            android.content.Context r3 = r3.mContext
            java.lang.CharSequence r3 = com.android.settings.network.SubscriptionUtil.getUniqueSubscriptionDisplayName((android.telephony.SubscriptionInfo) r1, (android.content.Context) r3)
            java.lang.String r0 = r3.toString()
        L_0x0025:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.ProviderModelSliceHelper.getMobileTitle():java.lang.String");
    }

    private Set<String> getKeywords() {
        return (Set) Arrays.stream(TextUtils.split(this.mContext.getString(C1992R$string.keywords_internet), ",")).map(ProviderModelSliceHelper$$ExternalSyntheticLambda1.INSTANCE).collect(Collectors.toSet());
    }
}
