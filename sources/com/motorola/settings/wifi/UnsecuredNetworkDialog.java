package com.motorola.settings.wifi;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1992R$string;
import com.android.settings.wifi.dpp.WifiNetworkConfig;
import com.android.wifitrackerlib.WifiEntry;

public class UnsecuredNetworkDialog extends DialogFragment {
    private static final String TAG = UnsecuredNetworkDialog.class.getSimpleName();
    private DialogInterface.OnClickListener mNegativeListener;
    private DialogInterface.OnClickListener mPositiveListener;
    private String mSsid;

    public static void show(Context context, String str, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onClickListener2) {
        FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
        UnsecuredNetworkDialog unsecuredNetworkDialog = new UnsecuredNetworkDialog();
        unsecuredNetworkDialog.setCancelable(false);
        unsecuredNetworkDialog.setRetainInstance(true);
        unsecuredNetworkDialog.setSsid(str);
        unsecuredNetworkDialog.setPositiveListener(onClickListener);
        unsecuredNetworkDialog.setNegativeListener(onClickListener2);
        unsecuredNetworkDialog.show(fragmentManager, TAG);
    }

    private void setPositiveListener(DialogInterface.OnClickListener onClickListener) {
        this.mPositiveListener = onClickListener;
    }

    private void setNegativeListener(DialogInterface.OnClickListener onClickListener) {
        this.mNegativeListener = onClickListener;
    }

    private void setSsid(String str) {
        this.mSsid = str;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getContext()).setMessage((CharSequence) getContext().getResources().getString(C1992R$string.wifi_conn_unsecure_dialog_msg, new Object[]{this.mSsid})).setTitle(C1992R$string.wifi_conn_unsecure_dialog_title).setPositiveButton(C1992R$string.wifi_connect, this.mPositiveListener).setNegativeButton(C1992R$string.cancel, this.mNegativeListener).create();
    }

    public static boolean shouldShowDialog(Context context, WifiEntry wifiEntry) {
        return isFtr4397Enabled(context) && wifiEntry.getSecurity() == 0;
    }

    public static boolean shouldShowDialog(Context context, int i) {
        return isFtr4397Enabled(context) && i == 0;
    }

    public static boolean shouldShowQRCodeDialog(Context context, int i, WifiNetworkConfig wifiNetworkConfig) {
        if (!isFtr4397Enabled(context) || !"nopass".equals(wifiNetworkConfig.getSecurity()) || i == 1) {
            return false;
        }
        return true;
    }

    private static boolean isFtr4397Enabled(Context context) {
        PersistableBundle config;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        if (carrierConfigManager == null || (config = carrierConfigManager.getConfig()) == null) {
            return false;
        }
        return config.getBoolean("moto_show_unsecured_wifi_network_dialog");
    }
}
