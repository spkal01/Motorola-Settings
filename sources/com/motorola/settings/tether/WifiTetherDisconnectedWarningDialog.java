package com.motorola.settings.tether;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1992R$string;
import com.motorola.settingslib.hotspothelper.HotspotHelper;

public class WifiTetherDisconnectedWarningDialog extends DialogFragment {
    private static final String TAG = WifiTetherDisconnectedWarningDialog.class.getSimpleName();
    private DialogInterface.OnClickListener mOnClickListener;

    public static boolean shouldShow(Context context) {
        if (!(context instanceof Activity)) {
            Log.d(TAG, "Can't show dialog without an Activity context");
            return false;
        } else if (!HotspotHelper.isWifiHotspotActive(context) || !HotspotHelper.shouldWarnWhenEditingTethering(context)) {
            return false;
        } else {
            return true;
        }
    }

    public static void show(FragmentManager fragmentManager, DialogInterface.OnClickListener onClickListener) {
        WifiTetherDisconnectedWarningDialog wifiTetherDisconnectedWarningDialog = new WifiTetherDisconnectedWarningDialog();
        wifiTetherDisconnectedWarningDialog.setOnClickListener(onClickListener);
        wifiTetherDisconnectedWarningDialog.setCancelable(false);
        wifiTetherDisconnectedWarningDialog.setRetainInstance(true);
        wifiTetherDisconnectedWarningDialog.show(fragmentManager, TAG);
    }

    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getContext()).setTitle(C1992R$string.wifi_tethering_disconnect_warning_dialog_title).setMessage(C1992R$string.wifi_tethering_disconnect_warning_dialog_message).setPositiveButton(C1992R$string.wifi_tethering_disconnect_warning_dialog_positive_button, this.mOnClickListener).setNegativeButton(17039360, this.mOnClickListener).create();
    }

    private void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage((Message) null);
        }
        super.onDestroyView();
    }
}
