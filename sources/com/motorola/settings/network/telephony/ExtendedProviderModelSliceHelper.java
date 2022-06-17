package com.motorola.settings.network.telephony;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.Utils;
import com.android.settings.network.ProviderModelSliceHelper;
import com.android.settings.network.ProxySubscriptionManager;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBroadcastReceiver;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.utils.ThreadUtils;
import com.motorola.settings.network.MotoMobileNetworkUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class ExtendedProviderModelSliceHelper extends ProviderModelSliceHelper {
    private static final Uri[] SIM_SLOT_URI_REGISTRY = {CustomSliceRegistry.MOBILE_DATA_SIM1_SLICE_URI, CustomSliceRegistry.MOBILE_DATA_SIM2_SLICE_URI};
    private String mDefaultNetworkDescription = "";
    private final ProxySubscriptionManager mProxySubscriptionManager;
    private final SharedPreferences mSharedPref;
    private final TelecomManager mTelecomManager;

    public ExtendedProviderModelSliceHelper(Context context, CustomSliceable customSliceable) {
        super(context, customSliceable);
        this.mTelecomManager = (TelecomManager) context.getSystemService(TelecomManager.class);
        this.mSharedPref = context.getSharedPreferences("ProviderModelSlice", 0);
        this.mProxySubscriptionManager = ProxySubscriptionManager.getInstance(context);
    }

    public boolean isDebuggable() {
        return Build.IS_DEBUGGABLE || Log.isLoggable("ExtendedProviderModel", 3);
    }

    public boolean isDualSIM() {
        ProxySubscriptionManager proxySubscriptionManager = this.mProxySubscriptionManager;
        if (proxySubscriptionManager == null || proxySubscriptionManager.get().getActiveSubscriptionIdList().length <= 1) {
            return false;
        }
        return true;
    }

    public boolean isAirplaneModeEnabled() {
        return WirelessUtils.isAirplaneModeOn(this.mContext);
    }

    public void setDefaultNetworkDescription(String str) {
        if (!TextUtils.isEmpty(str)) {
            this.mDefaultNetworkDescription = str;
        }
    }

    public List<ListBuilder.RowBuilder> createMobileDataRows() {
        List<SubscriptionInfo> activeSubscriptionsInfo = this.mProxySubscriptionManager.getActiveSubscriptionsInfo();
        ArrayList arrayList = new ArrayList();
        for (SubscriptionInfo next : activeSubscriptionsInfo) {
            arrayList.add(createCarrierRowInternal(SIM_SLOT_URI_REGISTRY[next.getSimSlotIndex()], next));
        }
        return arrayList;
    }

    private ListBuilder.RowBuilder createCarrierRowInternal(Uri uri, SubscriptionInfo subscriptionInfo) {
        if (uri == null || subscriptionInfo == null) {
            return null;
        }
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        String mobileRowTitle = getMobileRowTitle(subscriptionInfo);
        String mobileRowSummary = getMobileRowSummary(subscriptionInfo);
        IconCompat createIconWithDrawable = Utils.createIconWithDrawable(getMobileRowDrawable(subscriptionInfo));
        ListBuilder.RowBuilder subtitle = new ListBuilder.RowBuilder().setTitle(mobileRowTitle).setTitleItem(createIconWithDrawable, 0).setSubtitle(Html.fromHtml(mobileRowSummary, 0));
        if (MotoMobileNetworkUtils.isSimInSlotUnlocked(this.mContext, subscriptionId)) {
            boolean z = SubscriptionManager.getDefaultDataSubscriptionId() == subscriptionId && MobileNetworkUtils.isMobileDataEnabled(this.mContext);
            PendingIntent createMobileDataPendingIntent = createMobileDataPendingIntent(uri, subscriptionInfo);
            SliceAction create = SliceAction.create(createMobileDataPendingIntent, createIconWithDrawable, 0, mobileRowTitle);
            subtitle.addEndItem(SliceAction.createToggle(createMobileDataPendingIntent, "mobile_data_toggle", z));
            subtitle.setPrimaryAction(create);
        } else {
            subtitle.addEndItem(Utils.createIconWithDrawable(getMobilePadlockDrawable(subscriptionInfo)), 0);
        }
        return subtitle;
    }

    private PendingIntent createMobileDataPendingIntent(Uri uri, SubscriptionInfo subscriptionInfo) {
        return PendingIntent.getBroadcast(this.mContext, 0, new Intent(uri.toString()).setData(uri).setClass(this.mContext, SliceBroadcastReceiver.class).putExtra("android.provider.extra.SUB_ID", subscriptionInfo != null ? subscriptionInfo.getSubscriptionId() : -1), 167772160);
    }

    private String getMobileRowSummary(SubscriptionInfo subscriptionInfo) {
        if (isDefaultDataSubscription(subscriptionInfo)) {
            return getMobileSummary(this.mDefaultNetworkDescription);
        }
        return this.mContext.getString(C1992R$string.mobile_data_off_summary);
    }

    private String getMobileRowTitle(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo == null) {
            return this.mContext.getText(C1992R$string.mobile_data_settings_title).toString();
        }
        return SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this.mContext).toString();
    }

    private Drawable getMobilePadlockDrawable(SubscriptionInfo subscriptionInfo) {
        Drawable drawable = this.mContext.getDrawable(C1983R$drawable.ic_padlock);
        setColorAccentWhenDDS(subscriptionInfo, drawable);
        return drawable;
    }

    private Drawable getMobileRowDrawable(SubscriptionInfo subscriptionInfo) {
        Drawable drawable = this.mContext.getDrawable(C1983R$drawable.ic_signal_strength_zero_bar_no_internet);
        if (subscriptionInfo == null) {
            return drawable;
        }
        if (isDataStateInService() || isVoiceStateInService()) {
            Semaphore semaphore = new Semaphore(0);
            AtomicReference atomicReference = new AtomicReference();
            ThreadUtils.postOnMainThread(new ExtendedProviderModelSliceHelper$$ExternalSyntheticLambda1(this, atomicReference, subscriptionInfo, semaphore));
            try {
                semaphore.acquire();
                drawable = (Drawable) atomicReference.get();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        setColorAccentWhenDDS(subscriptionInfo, drawable);
        return drawable;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMobileRowDrawable$0(AtomicReference atomicReference, SubscriptionInfo subscriptionInfo, Semaphore semaphore) {
        atomicReference.set(getDrawableWithSignalStrength(subscriptionInfo.getSubscriptionId()));
        semaphore.release();
    }

    private boolean isDefaultDataSubscription(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo != null && subscriptionInfo.getSubscriptionId() == SubscriptionManager.getDefaultDataSubscriptionId()) {
            return true;
        }
        return false;
    }

    private void setColorAccentWhenDDS(SubscriptionInfo subscriptionInfo, Drawable drawable) {
        if (subscriptionInfo != null && drawable != null) {
            drawable.setTint(com.android.settingslib.Utils.getColorAttrDefaultColor(this.mContext, 16843817));
            if (isDefaultDataSubscription(subscriptionInfo) && isMobileDataEnabled()) {
                drawable.setTint(com.android.settingslib.Utils.getColorAccentDefaultColor(this.mContext));
            }
        }
    }

    public String getMobileTitle() {
        return super.getMobileTitle();
    }

    public boolean isInCall() {
        return this.mTelecomManager.isInCall();
    }

    public boolean isMobileDataDisableDialogEnabled() {
        return this.mSharedPref.getBoolean("PrefHasTurnedOffMobileData", true);
    }

    public void setMobileDataDisableDialogEnabled(boolean z) {
        this.mSharedPref.edit().putBoolean("PrefHasTurnedOffMobileData", z).apply();
    }

    public AlertDialog getMobileDataDisableDialog(String str, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onClickListener2) {
        return new AlertDialog.Builder(this.mContext).setTitle(C1992R$string.mobile_data_disable_title).setMessage(this.mContext.getString(C1992R$string.mobile_data_disable_message, new Object[]{str})).setNegativeButton(17039360, onClickListener2).setPositiveButton(17039650, onClickListener).setOnCancelListener(new ExtendedProviderModelSliceHelper$$ExternalSyntheticLambda0(onClickListener2)).create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$getMobileDataDisableDialog$1(DialogInterface.OnClickListener onClickListener, DialogInterface dialogInterface) {
        onClickListener.onClick(dialogInterface, -2);
        dialogInterface.dismiss();
    }
}
