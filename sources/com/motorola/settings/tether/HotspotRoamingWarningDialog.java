package com.motorola.settings.tether;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1992R$string;
import com.motorola.android.provider.MotorolaSettings;

public class HotspotRoamingWarningDialog extends DialogFragment {
    private static final String TAG = HotspotRoamingWarningDialog.class.getSimpleName();
    private WarningDialogCallback mCallback;

    public static boolean shouldShow(Context context) {
        if (!(context instanceof Activity)) {
            Log.d(TAG, "Can't show dialog without an Activity context");
            return false;
        }
        boolean isNetworkRoaming = ((TelephonyManager) context.getSystemService("phone")).isNetworkRoaming();
        String str = TAG;
        Log.d(str, "Network roaming while enabling tethering: " + isNetworkRoaming);
        if (!isNetworkRoaming || !isVzw(context)) {
            return false;
        }
        return true;
    }

    public static void show(FragmentManager fragmentManager, WarningDialogCallback warningDialogCallback) {
        HotspotRoamingWarningDialog hotspotRoamingWarningDialog = new HotspotRoamingWarningDialog();
        hotspotRoamingWarningDialog.setCallback(warningDialogCallback);
        hotspotRoamingWarningDialog.setCancelable(false);
        hotspotRoamingWarningDialog.setRetainInstance(true);
        hotspotRoamingWarningDialog.show(fragmentManager, TAG);
    }

    public static boolean isVzw(Context context) {
        String string = MotorolaSettings.Global.getString(context.getContentResolver(), "channel_id");
        return "vzw".equalsIgnoreCase(string) || "vzwpre".equalsIgnoreCase(string);
    }

    public void setCallback(WarningDialogCallback warningDialogCallback) {
        this.mCallback = warningDialogCallback;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Log.d(TAG, "Displaying VZW roaming while enabling tethering warning");
        return new AlertDialog.Builder(getContext()).setTitle(C1992R$string.roaming).setMessage((CharSequence) getString(C1992R$string.hotspot_roaming_msg)).setPositiveButton(C1992R$string.continue_button, (DialogInterface.OnClickListener) new HotspotRoamingWarningDialog$$ExternalSyntheticLambda1(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) new HotspotRoamingWarningDialog$$ExternalSyntheticLambda0(this)).create();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
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
