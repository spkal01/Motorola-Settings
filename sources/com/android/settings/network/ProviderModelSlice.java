package com.android.settings.network;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.telephony.SubscriptionManager;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.SubSettings;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.NetworkProviderWorker;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.SliceBroadcastReceiver;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settings.wifi.slice.WifiSlice;
import com.android.settings.wifi.slice.WifiSliceItem;
import com.android.settingslib.Utils;
import com.android.settingslib.wifi.WifiUtils;
import com.motorola.settings.network.CarrierMobileDataWarningDialogUtils;
import com.motorola.settings.network.telephony.ExtendedProviderModelSliceHelper;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProviderModelSlice extends WifiSlice {
    private final ExtendedProviderModelSliceHelper mHelper = getHelper();
    private final SharedPreferences mSharedPref = getSharedPreference();

    /* access modifiers changed from: protected */
    public boolean isApRowCollapsed() {
        return false;
    }

    public ProviderModelSlice(Context context) {
        super(context);
    }

    public Uri getUri() {
        return CustomSliceRegistry.PROVIDER_MODEL_SLICE_URI;
    }

    private static void log(String str) {
        Log.d("ProviderModelSlice", str);
    }

    public Slice getSlice() {
        int i;
        ListBuilder createListBuilder = this.mHelper.createListBuilder(getUri());
        NetworkProviderWorker worker = getWorker();
        if (worker != null) {
            i = worker.getApRowCount();
        } else {
            log("network provider worker is null.");
            i = 0;
        }
        if (getInternetType() == 4) {
            log("get Ethernet item which is connected");
            createListBuilder.addRow(createEthernetRow());
            i--;
        }
        if (!this.mHelper.isAirplaneModeEnabled()) {
            boolean hasCarrier = this.mHelper.hasCarrier();
            log("hasCarrier: " + hasCarrier);
            if (hasCarrier) {
                this.mHelper.updateTelephony();
                String networkTypeDescription = worker != null ? worker.getNetworkTypeDescription() : "";
                log("isDualSIM: " + this.mHelper.isDualSIM());
                this.mHelper.setDefaultNetworkDescription(networkTypeDescription);
                List<ListBuilder.RowBuilder> createMobileDataRows = this.mHelper.createMobileDataRows();
                Objects.requireNonNull(createListBuilder);
                createMobileDataRows.forEach(new ProviderModelSlice$$ExternalSyntheticLambda2(createListBuilder));
                i -= createMobileDataRows.size();
            }
        }
        boolean isWifiEnabled = this.mWifiManager.isWifiEnabled();
        createListBuilder.addRow(createWifiToggleRow(this.mContext, isWifiEnabled));
        int i2 = i - 1;
        if (!isWifiEnabled) {
            log("Wi-Fi is disabled");
            return createListBuilder.build();
        }
        List results = worker != null ? worker.getResults() : null;
        if (results == null || results.size() <= 0) {
            log("Wi-Fi list is empty");
            return createListBuilder.build();
        }
        WifiSliceItem connectedWifiItem = this.mHelper.getConnectedWifiItem(results);
        if (connectedWifiItem != null) {
            log("get Wi-Fi item which is connected");
            createListBuilder.addRow(getWifiSliceItemRow(connectedWifiItem));
            i2--;
        }
        log("get Wi-Fi items which are not connected. Wi-Fi items : " + results.size());
        for (WifiSliceItem wifiSliceItemRow : (List) results.stream().filter(ProviderModelSlice$$ExternalSyntheticLambda3.INSTANCE).limit((long) (i2 + -1)).collect(Collectors.toList())) {
            createListBuilder.addRow(getWifiSliceItemRow(wifiSliceItemRow));
        }
        log("add See-All");
        createListBuilder.addRow(getSeeAllRow());
        return createListBuilder.build();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getSlice$0(WifiSliceItem wifiSliceItem) {
        return wifiSliceItem.getConnectedState() != 2;
    }

    public PendingIntent getBroadcastIntent(Context context) {
        return PendingIntent.getBroadcast(context, 0, new Intent(getUri().toString()).addFlags(268435456).setData(getUri()).setClass(context, SliceBroadcastReceiver.class), 167772160);
    }

    public void onNotifyChange(Intent intent) {
        SharedPreferences sharedPreferences;
        if (this.mHelper.getSubscriptionManager() != null) {
            int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
            log("defaultSubId:" + defaultDataSubscriptionId);
            if (defaultSubscriptionIsUsable(defaultDataSubscriptionId)) {
                boolean hasExtra = intent.hasExtra("android.app.slice.extra.TOGGLE_STATE");
                boolean booleanExtra = intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", this.mHelper.isMobileDataEnabled());
                if (hasExtra) {
                    if (!booleanExtra && CarrierMobileDataWarningDialogUtils.isCarrierFtrEnabled(this.mContext)) {
                        CarrierMobileDataWarningDialogUtils.sendDialogIntent(this.mContext, defaultDataSubscriptionId);
                        return;
                    } else if (booleanExtra || (sharedPreferences = this.mSharedPref) == null || !sharedPreferences.getBoolean("PrefHasTurnedOffMobileData", true)) {
                        MobileNetworkUtils.setMobileDataEnabled(this.mContext, defaultDataSubscriptionId, booleanExtra, false);
                    } else {
                        String mobileTitle = this.mHelper.getMobileTitle();
                        if (mobileTitle.equals(this.mContext.getString(C1992R$string.mobile_data_settings_title))) {
                            mobileTitle = this.mContext.getString(C1992R$string.mobile_data_disable_message_default_carrier);
                        }
                        showMobileDataDisableDialog(getMobileDataDisableDialog(defaultDataSubscriptionId, mobileTitle));
                        return;
                    }
                }
                if (!hasExtra) {
                    booleanExtra = MobileNetworkUtils.isMobileDataEnabled(this.mContext);
                }
                doCarrierNetworkAction(hasExtra, booleanExtra, defaultDataSubscriptionId);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public AlertDialog getMobileDataDisableDialog(int i, String str) {
        return new AlertDialog.Builder(this.mContext).setTitle(C1992R$string.mobile_data_disable_title).setMessage(this.mContext.getString(C1992R$string.mobile_data_disable_message, new Object[]{str})).setNegativeButton(17039360, new ProviderModelSlice$$ExternalSyntheticLambda0(this)).setPositiveButton(17039650, new ProviderModelSlice$$ExternalSyntheticLambda1(this, i)).create();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMobileDataDisableDialog$1(DialogInterface dialogInterface, int i) {
        NetworkProviderWorker worker = getWorker();
        if (worker != null) {
            worker.updateSlice();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMobileDataDisableDialog$2(int i, DialogInterface dialogInterface, int i2) {
        MobileNetworkUtils.setMobileDataEnabled(this.mContext, i, false, false);
        doCarrierNetworkAction(true, false, i);
        SharedPreferences sharedPreferences = this.mSharedPref;
        if (sharedPreferences != null) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("PrefHasTurnedOffMobileData", false);
            edit.apply();
        }
    }

    private void showMobileDataDisableDialog(AlertDialog alertDialog) {
        if (alertDialog == null) {
            log("AlertDialog is null");
            return;
        }
        alertDialog.getWindow().setType(2009);
        alertDialog.show();
    }

    /* access modifiers changed from: package-private */
    public void doCarrierNetworkAction(boolean z, boolean z2, int i) {
        NetworkProviderWorker worker = getWorker();
        if (worker != null) {
            if (z) {
                worker.setCarrierNetworkEnabledIfNeeded(z2, i);
            } else if (z2) {
                worker.connectCarrierNetwork();
            }
        }
    }

    public Intent getIntent() {
        return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, NetworkProviderSettings.class.getName(), "", this.mContext.getText(C1992R$string.provider_internet_settings).toString(), 1401).setClassName(this.mContext.getPackageName(), SubSettings.class.getName()).setData(getUri());
    }

    public Class getBackgroundWorkerClass() {
        return NetworkProviderWorker.class;
    }

    /* access modifiers changed from: package-private */
    public ExtendedProviderModelSliceHelper getHelper() {
        return new ExtendedProviderModelSliceHelper(this.mContext, this);
    }

    /* access modifiers changed from: package-private */
    public NetworkProviderWorker getWorker() {
        return (NetworkProviderWorker) SliceBackgroundWorker.getInstance(getUri());
    }

    /* access modifiers changed from: package-private */
    public SharedPreferences getSharedPreference() {
        return this.mContext.getSharedPreferences("ProviderModelSlice", 0);
    }

    private int getInternetType() {
        NetworkProviderWorker worker = getWorker();
        if (worker == null) {
            return 1;
        }
        return worker.getInternetType();
    }

    /* access modifiers changed from: package-private */
    public ListBuilder.RowBuilder createEthernetRow() {
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        Drawable drawable = this.mContext.getDrawable(C1983R$drawable.ic_settings_ethernet);
        if (drawable != null) {
            drawable.setTintList(Utils.getColorAttr(this.mContext, 16843829));
            rowBuilder.setTitleItem(com.android.settings.Utils.createIconWithDrawable(drawable), 0);
        }
        return rowBuilder.setTitle(this.mContext.getText(C1992R$string.ethernet)).setSubtitle(this.mContext.getText(C1992R$string.to_switch_networks_disconnect_ethernet));
    }

    /* access modifiers changed from: protected */
    public ListBuilder.RowBuilder createWifiToggleRow(Context context, boolean z) {
        Uri uri = CustomSliceRegistry.WIFI_SLICE_URI;
        return new ListBuilder.RowBuilder().setTitle(context.getString(C1992R$string.wifi_settings)).setPrimaryAction(SliceAction.createToggle(PendingIntent.getBroadcast(context, 0, new Intent(uri.toString()).setData(uri).setClass(context, SliceBroadcastReceiver.class).putExtra("android.app.slice.extra.TOGGLE_STATE", !z).addFlags(268435456), 201326592), (CharSequence) null, z));
    }

    /* access modifiers changed from: protected */
    public ListBuilder.RowBuilder getSeeAllRow() {
        CharSequence text = this.mContext.getText(C1992R$string.previous_connected_see_all);
        IconCompat seeAllIcon = getSeeAllIcon();
        return new ListBuilder.RowBuilder().setTitleItem(seeAllIcon, 0).setTitle(text).setPrimaryAction(getPrimaryAction(seeAllIcon, text));
    }

    /* access modifiers changed from: protected */
    public IconCompat getSeeAllIcon() {
        Drawable drawable = this.mContext.getDrawable(C1983R$drawable.ic_arrow_forward);
        if (drawable == null) {
            return com.android.settings.Utils.createIconWithDrawable(new ColorDrawable(0));
        }
        drawable.setTint(Utils.getColorAttrDefaultColor(this.mContext, 16843817));
        return com.android.settings.Utils.createIconWithDrawable(drawable);
    }

    /* access modifiers changed from: protected */
    public SliceAction getPrimaryAction(IconCompat iconCompat, CharSequence charSequence) {
        return SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, 0, getIntent(), 67108864), iconCompat, 0, charSequence);
    }

    /* access modifiers changed from: protected */
    public IconCompat getWifiSliceItemLevelIcon(WifiSliceItem wifiSliceItem) {
        if (wifiSliceItem.getConnectedState() != 2 || getInternetType() == 2) {
            return super.getWifiSliceItemLevelIcon(wifiSliceItem);
        }
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(this.mContext, 16843817);
        Drawable drawable = this.mContext.getDrawable(WifiUtils.getInternetIconResource(wifiSliceItem.getLevel(), wifiSliceItem.shouldShowXLevelIcon()));
        drawable.setTint(colorAttrDefaultColor);
        return com.android.settings.Utils.createIconWithDrawable(drawable);
    }

    /* access modifiers changed from: protected */
    public boolean defaultSubscriptionIsUsable(int i) {
        return SubscriptionManager.isUsableSubscriptionId(i);
    }
}
