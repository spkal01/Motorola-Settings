package com.motorola.settings.tether;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.motorola.settingslib.hotspothelper.HotspotHelper;
import com.motorola.settingslib.hotspothelper.R$id;
import com.motorola.settingslib.hotspothelper.R$layout;
import com.motorola.settingslib.hotspothelper.R$string;

public class WifiHotspotConflictWarningDialog extends DialogFragment {
    private static final String TAG = WifiHotspotConflictWarningDialog.class.getSimpleName();
    private WarningDialogCallback mCallback;

    public static boolean shouldShow(Context context) {
        if (!(context instanceof Activity)) {
            Log.d(TAG, "Can't show dialog without an Activity context");
            return false;
        } else if (!needsWarning(context) || !isConflictingWithHotspot(context)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean needsWarning(Context context) {
        return !HotspotHelper.getDoNotShowWifiWarning(context) && HotspotHelper.shouldWarnWhenEnablingWifi(context);
    }

    public static boolean isConflictingWithHotspot(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        return !wifiManager.isStaApConcurrencySupported() && !wifiManager.isWifiEnabled() && HotspotHelper.isWifiHotspotActive(context);
    }

    public static void show(FragmentManager fragmentManager, WarningDialogCallback warningDialogCallback) {
        WifiHotspotConflictWarningDialog wifiHotspotConflictWarningDialog = new WifiHotspotConflictWarningDialog();
        wifiHotspotConflictWarningDialog.setCallback(warningDialogCallback);
        wifiHotspotConflictWarningDialog.setCancelable(false);
        wifiHotspotConflictWarningDialog.setRetainInstance(true);
        wifiHotspotConflictWarningDialog.show(fragmentManager, TAG);
    }

    public void setCallback(WarningDialogCallback warningDialogCallback) {
        this.mCallback = warningDialogCallback;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Context context = getContext();
        View inflate = View.inflate(context, R$layout.do_not_show_again_checkbox, (ViewGroup) null);
        return new AlertDialog.Builder(context).setTitle(HotspotHelper.getWarningDialogTitle(context)).setView(inflate).setMessage(context.getString(R$string.wifi_enable_warning_message)).setCancelable(false).setNegativeButton(17039360, new WifiHotspotConflictWarningDialog$$ExternalSyntheticLambda0(this)).setPositiveButton(17039370, new WifiHotspotConflictWarningDialog$$ExternalSyntheticLambda1(this, (CheckBox) inflate.findViewById(R$id.do_not_show_again), context)).create();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        WarningDialogCallback warningDialogCallback = this.mCallback;
        if (warningDialogCallback != null) {
            warningDialogCallback.onNegative();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(CheckBox checkBox, Context context, DialogInterface dialogInterface, int i) {
        if (checkBox.isChecked()) {
            HotspotHelper.setDoNotShowWifiWarning(context);
        }
        WarningDialogCallback warningDialogCallback = this.mCallback;
        if (warningDialogCallback != null) {
            warningDialogCallback.onPositive();
        }
    }

    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage((Message) null);
        }
        super.onDestroyView();
    }
}
