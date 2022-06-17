package com.motorola.settings.tether;

import android.app.Activity;
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
import androidx.appcompat.app.AlertDialog;
import com.motorola.settingslib.hotspothelper.HotspotHelper;
import com.motorola.settingslib.hotspothelper.R$id;
import com.motorola.settingslib.hotspothelper.R$layout;

public class HotspotWifiConflictWarningDialog extends DialogFragment {
    private static final String TAG = HotspotWifiConflictWarningDialog.class.getSimpleName();
    private WarningDialogCallback mCallback;

    public static boolean shouldShow(Context context) {
        if (!(context instanceof Activity)) {
            Log.d(TAG, "Can't show dialog without an Activity context");
            return false;
        } else if (!HotspotHelper.shouldWarnWhenEnablingTethering(context) || HotspotHelper.getDoNotShowHotspotWarning(context) || !isConflictingWithWifi(context)) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean isConflictingWithWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        return wifiManager.isWifiEnabled() && !wifiManager.isStaApConcurrencySupported();
    }

    public static void show(FragmentManager fragmentManager, WarningDialogCallback warningDialogCallback) {
        HotspotWifiConflictWarningDialog hotspotWifiConflictWarningDialog = new HotspotWifiConflictWarningDialog();
        hotspotWifiConflictWarningDialog.setCallback(warningDialogCallback);
        hotspotWifiConflictWarningDialog.setCancelable(false);
        hotspotWifiConflictWarningDialog.setRetainInstance(true);
        hotspotWifiConflictWarningDialog.show(fragmentManager, TAG);
    }

    public void setCallback(WarningDialogCallback warningDialogCallback) {
        this.mCallback = warningDialogCallback;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        View inflate = View.inflate(getContext(), R$layout.do_not_show_again_checkbox, (ViewGroup) null);
        return new AlertDialog.Builder(getContext()).setTitle((CharSequence) HotspotHelper.getWarningDialogTitle(getContext())).setMessage((CharSequence) HotspotHelper.getTetheringWarningDialogMessage(getContext())).setView(inflate).setPositiveButton(17039370, (DialogInterface.OnClickListener) new HotspotWifiConflictWarningDialog$$ExternalSyntheticLambda1(this, (CheckBox) inflate.findViewById(R$id.do_not_show_again))).setNegativeButton(17039360, (DialogInterface.OnClickListener) new HotspotWifiConflictWarningDialog$$ExternalSyntheticLambda0(this)).create();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(CheckBox checkBox, DialogInterface dialogInterface, int i) {
        if (checkBox.isChecked()) {
            HotspotHelper.setDoNotShowHotspotWarning(getContext());
        }
        if (HotspotRoamingWarningDialog.shouldShow(getContext())) {
            HotspotRoamingWarningDialog.show(getFragmentManager(), this.mCallback);
            return;
        }
        WarningDialogCallback warningDialogCallback = this.mCallback;
        if (warningDialogCallback != null) {
            warningDialogCallback.onPositive();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        WarningDialogCallback warningDialogCallback = this.mCallback;
        if (warningDialogCallback != null) {
            warningDialogCallback.onNegative();
        }
    }

    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage((Message) null);
        }
        super.onDestroyView();
    }
}
