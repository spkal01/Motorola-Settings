package com.motorola.settings.wifi;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;
import com.motorola.settings.tether.WifiTetherDisconnectedWarningDialog;

public class BroadcastSsidInvisibleDialogFragment extends DialogFragment {
    private static final String TAG = BroadcastSsidInvisibleDialogFragment.class.getSimpleName();
    /* access modifiers changed from: private */
    public static SsidInvisibleDialogFragmentListener mListener;

    public interface SsidInvisibleDialogFragmentListener {
        void OnDialogClickListener(boolean z);
    }

    public static boolean needsDialog(Context context) {
        return !context.getResources().getBoolean(C1980R$bool.droid_wifi_ap_settings);
    }

    public static void show(FragmentManager fragmentManager, SsidInvisibleDialogFragmentListener ssidInvisibleDialogFragmentListener) {
        BroadcastSsidInvisibleDialogFragment broadcastSsidInvisibleDialogFragment = new BroadcastSsidInvisibleDialogFragment();
        mListener = ssidInvisibleDialogFragmentListener;
        broadcastSsidInvisibleDialogFragment.setCancelable(false);
        broadcastSsidInvisibleDialogFragment.show(fragmentManager, TAG);
    }

    public Dialog onCreateDialog(Bundle bundle) {
        String string = getString(C1992R$string.broadcast_ssid_invisible_warning_msg);
        if (WifiTetherDisconnectedWarningDialog.shouldShow(getActivity())) {
            string = string + "\n\n" + getString(C1992R$string.wifi_tethering_disconnect_warning_dialog_message);
        }
        return new AlertDialog.Builder(getContext()).setTitle(C1992R$string.broadcast_ssid_invisible_warning_title).setMessage((CharSequence) string).setPositiveButton(C1992R$string.braodcast_ssid_warning_button_hide, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (BroadcastSsidInvisibleDialogFragment.mListener != null) {
                    BroadcastSsidInvisibleDialogFragment.mListener.OnDialogClickListener(true);
                }
            }
        }).setNegativeButton(C1992R$string.braodcast_ssid_warning_button_cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (BroadcastSsidInvisibleDialogFragment.mListener != null) {
                    BroadcastSsidInvisibleDialogFragment.mListener.OnDialogClickListener(false);
                }
            }
        }).create();
    }
}
